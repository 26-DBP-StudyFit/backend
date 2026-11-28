package com.DBP.StudyFit.manager;

import com.DBP.StudyFit.JDBCUtil;
import com.DBP.StudyFit.dao.JoinApplicationDAO;
import com.DBP.StudyFit.dao.TeamDAO;
import com.DBP.StudyFit.dao.TeamMemberDAO;
import com.DBP.StudyFit.domain.JoinApplication;
import com.DBP.StudyFit.domain.JoinApplicationStatus;
import com.DBP.StudyFit.domain.Team;
import com.DBP.StudyFit.dto.ApplicationListDTO;
import com.DBP.StudyFit.exception.NotTeamLeaderException;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class JoinApplicationManager {

    private final JoinApplicationDAO joinApplicationDAO;
    private final TeamMemberDAO teamMemberDAO;
    private final TeamDAO teamDAO;

    public JoinApplicationManager(JoinApplicationDAO joinApplicationDAO,
                                  TeamMemberDAO teamMemberDAO, TeamDAO teamDAO) {
        this.joinApplicationDAO = joinApplicationDAO;
        this.teamMemberDAO = teamMemberDAO;
        this.teamDAO = teamDAO;
    }

    // 팀 가입 신청
    public void createApplication(int teamId, int userId) {
        Team team = teamDAO.findTeamById(teamId);
        if (team == null)
            throw new IllegalStateException("존재하지 않는 팀입니다.");

        try (Connection conn = new JDBCUtil().getConnection()) {
            if (teamMemberDAO.existsByTeamIdAndMemberId(conn, teamId, userId))
                throw new IllegalStateException("이미 가입된 팀입니다.");

            if (teamMemberDAO.countByTeamId(conn, teamId) >= team.getRecruitCount())
                throw new IllegalStateException("모집 인원이 마감된 팀입니다.");
        } catch (SQLException e) {
            throw new RuntimeException("가입 신청 확인 중 오류가 발생했습니다.", e);
        }

        if (joinApplicationDAO.existingPendingApplication(teamId, userId))
            throw new IllegalStateException("이미 가입 신청한 팀입니다.");

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
                Team team = teamDAO.findTeamById(teamId);
                if (team == null)
                    throw new IllegalStateException("존재하지 않는 팀입니다.");
                if (teamMemberDAO.countByTeamId(conn, teamId) >= team.getRecruitCount())
                    throw new IllegalStateException("모집 인원이 마감되어 승인할 수 없습니다.");

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
    public List<ApplicationListDTO> getPendingApplications(int teamId, int userId) {
        if (!teamMemberDAO.isLeader(teamId, userId))
            throw new NotTeamLeaderException("팀장만 가입 신청 목록을 확인할 수 있습니다.");

        List<ApplicationListDTO> list = joinApplicationDAO.findPendingApplicationsWithApplicant(teamId);
        if (list == null)
            throw new IllegalStateException("가입 신청 목록 조회 중 오류가 발생했습니다.");
        return list;
    }

    //팀 가입 거절
    public void rejectApplication(int teamId, int applicationId, int userId) {
        // 신청 존재·팀·팀장 확인
        JoinApplication application = getApplicationDetail(teamId, applicationId, userId);
        if (application.getStatus() != JoinApplicationStatus.PENDING)
            throw new IllegalStateException("이미 처리된 신청입니다.");

        int updated = joinApplicationDAO.updateStatus(applicationId,
                JoinApplicationStatus.REJECTED.getCode());
        if (updated == 0)   // 그 사이 다른 요청이 먼저 처리한 경우
            throw new IllegalStateException("이미 처리된 신청입니다.");
    }

    // 가입 신청 상세 조회
    public JoinApplication getApplicationDetail(int teamId, int applicationId, int userId) {
        JoinApplication application = joinApplicationDAO.findApplicationById(applicationId);
        if (application == null || application.getTeamId() != teamId)
            throw new IllegalStateException("존재하지 않는 신청입니다.");

        if (!teamMemberDAO.isLeader(teamId, userId))
            throw new NotTeamLeaderException("팀장만 가입 신청을 확인·처리할 수 있습니다.");

        return application;
    }
}