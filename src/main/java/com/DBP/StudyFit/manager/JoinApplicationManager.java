package com.DBP.StudyFit.manager;

import com.DBP.StudyFit.dao.JoinApplicationDAO;
import com.DBP.StudyFit.dao.TeamMemberDAO;
import com.DBP.StudyFit.domain.JoinApplication;
import com.DBP.StudyFit.domain.JoinApplicationStatus;
import com.DBP.StudyFit.exception.NotTeamLeaderException;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class JoinApplicationManager {

    private final JoinApplicationDAO joinApplicationDAO;
    private final TeamMemberDAO teamMemberDAO;
    private final DataSource dataSource;   // javax.sql.DataSource

    public JoinApplicationManager(JoinApplicationDAO joinApplicationDAO,
                                  TeamMemberDAO teamMemberDAO, DataSource dataSource) {
        this.joinApplicationDAO = joinApplicationDAO;
        this.teamMemberDAO = teamMemberDAO;
        this.dataSource = dataSource;
    }

    // 팀 가입 신청
    // Manager
    public void createApplication(int teamId, int userId) {
        if (joinApplicationDAO.existingPendingApplication(teamId, userId))
            throw new IllegalStateException("이미 가입 신청한 팀입니다");

        JoinApplication application = new JoinApplication();
        application.setTeamId(teamId);
        application.setUserId(userId);

        if (joinApplicationDAO.create(application) < 0)
            throw new RuntimeException("가입 신청 실패");
    }

    //팀 가입 승인
    public void approveApplication(int teamId, int applicationId, int userId)
            throws NotTeamLeaderException {

        JoinApplication app = getApplicationDetail(teamId, applicationId, userId);
        if (app.getStatus() != JoinApplicationStatus.PENDING)
            throw new IllegalStateException("이미 처리된 신청입니다.");

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (!teamMemberDAO.isLeader(conn, teamId, userId))
                    throw new NotTeamLeaderException("팀장만 승인할 수 있습니다.");

                int updated = joinApplicationDAO.updateStatus(conn, applicationId,
                        JoinApplicationStatus.APPROVED.getCode());
                if (updated == 0)
                    throw new IllegalStateException("이미 처리된 신청입니다.");

                teamMemberDAO.insertMember(conn, teamId, app.getUserId());
                conn.commit();   // 둘 다 성공 → 반영

            } catch (NotTeamLeaderException | IllegalStateException e) {
                conn.rollback();
                throw e;         // 컨트롤러가 받아서 메시지 표시
            } catch (SQLException e) {
                conn.rollback(); // DB 오류 → 둘 다 취소
                throw new RuntimeException("승인 처리 실패", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB 연결 실패", e);
        }
    }

    // 가입 신청 상세 조회
    public JoinApplication getApplicationDetail(int teamId, int applicationId, int userId) {
        JoinApplication application = joinApplicationDAO.findApplicationById(applicationId);
        if (application == null || application.getTeamId() != teamId)
            throw new IllegalStateException("존재하지 않는 신청입니다.");
        return application;
    }
}
