package com.DBP.StudyFit.manager;

import com.DBP.StudyFit.JDBCUtil;
import com.DBP.StudyFit.dao.JoinApplicationDAO;
import com.DBP.StudyFit.dao.TeamMemberDAO;
import com.DBP.StudyFit.domain.JoinApplication;
import com.DBP.StudyFit.domain.JoinApplicationStatus;
import com.DBP.StudyFit.domain.TeamMemberRole;
import com.DBP.StudyFit.exception.NotTeamLeaderException;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class JoinApplicationManager {

    private final JoinApplicationDAO joinApplicationDAO;
    private final TeamMemberDAO teamMemberDAO;

    public JoinApplicationManager(JoinApplicationDAO joinApplicationDAO, TeamMemberDAO teamMemberDAO) {
        this.joinApplicationDAO = joinApplicationDAO;
        this.teamMemberDAO = teamMemberDAO;
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

        // 팀장인 지 확인도 함
        JoinApplication application = getApplicationDetail(teamId, applicationId, userId);
        if (application.getStatus() != JoinApplicationStatus.PENDING)
            throw new IllegalStateException("이미 처리된 신청입니다.");

        try (Connection conn = new JDBCUtil().getConnection()) {
            conn.setAutoCommit(false);
            try {
                int updated = joinApplicationDAO.updateStatus(conn, applicationId,
                        JoinApplicationStatus.APPROVED.getCode());
                if (updated == 0)
                    throw new IllegalStateException("이미 처리된 신청입니다.");

                teamMemberDAO.insertMember(conn, teamId, application.getUserId());
                conn.commit();   // 둘 다 성공 → 반영

            } catch (IllegalStateException e) {
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

    // 받은 가입 신청 목록 조회
    public List<JoinApplication> getPendingApplications(int teamId, int userId) {
        if (!teamMemberDAO.isLeader(teamId, userId))
            throw new NotTeamLeaderException("팀장만 가입 신청 목록을 확인할 수 있습니다.");

        List<JoinApplication> all = joinApplicationDAO.findApplicationsByTeamId(teamId);
        if (all == null)   // DAO는 DB 오류 시 null을 반환
            throw new IllegalStateException("가입 신청 목록 조회 중 오류가 발생했습니다.");

        List<JoinApplication> pending = new ArrayList<>();
        for (JoinApplication app : all) {
            if (app.getStatus() == JoinApplicationStatus.PENDING)
                pending.add(app);
        }
        return pending;
    }

    // 가입 신청 상세 조회
    public JoinApplication getApplicationDetail(int teamId, int applicationId, int userId) {
        JoinApplication application = joinApplicationDAO.findApplicationById(applicationId);
        if (application == null || application.getTeamId() != teamId)
            throw new IllegalStateException("존재하지 않는 신청입니다.");

        if (!isLeader(teamId, userId))
            throw new NotTeamLeaderException("신청 내용을 볼 권한이 없습니다.");

        return application;
    }

    // 해당 팀의 팀장인지 확인
    private boolean isLeader(int teamId, int userId) {
        try (Connection conn = new JDBCUtil().getConnection()) {
            return teamMemberDAO.findByTeamIdAndMemberId(conn, teamId, userId)
                    .map(member -> member.getRole() == TeamMemberRole.LEADER)
                    .orElse(false);
        } catch (SQLException e) {
            throw new IllegalStateException("팀장 확인 중 오류가 발생했습니다.", e);
        }
    }
}
