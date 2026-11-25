package com.DBP.StudyFit.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.DBP.StudyFit.domain.JoinApplication;
import com.DBP.StudyFit.JDBCUtil;
import com.DBP.StudyFit.domain.JoinApplicationStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ModelAttribute;

@Repository
public class JoinApplicationDAO {
    private JDBCUtil jdbcUtil = null;

    public JoinApplicationDAO() {
        jdbcUtil = new JDBCUtil();
    }

    // 새로운 팀 가입 신청 등록
    public int create(JoinApplication application) {
        int newId = -1;

        try {
            String query = "SELECT seq_join_application.NEXTVAL FROM dual";
            jdbcUtil.setSqlAndParameters(query, null);

            ResultSet rs = jdbcUtil.executeQuery();
            if (rs.next())
                newId = rs.getInt(1);

            StringBuffer insert = new StringBuffer();
            insert.append("INSERT INTO join_application ");
            insert.append("(application_id, team_id, member_id, ");
            insert.append("status, created_at, processed_at) ");
            insert.append("VALUES (?, ?, ?, 'P', SYSDATE, NULL)");

            jdbcUtil.setSqlAndParameters(
                    insert.toString(),
                    new Object[] {
                            newId,
                            application.getTeamId(),
                            application.getUserId()
                    }
            );

            jdbcUtil.executeUpdate();
            jdbcUtil.commit();

            application.setApplicationId(newId);
            application.setStatus(JoinApplicationStatus.PENDING);
            return newId;

        } catch (Exception ex) {
            jdbcUtil.rollback();
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }

        return -1;
    }

    // 신청 번호로 가입 신청 조회
    public JoinApplication findApplicationById(int applicationId) {
        StringBuffer query = new StringBuffer();
        query.append("SELECT application_id, team_id, member_id, ");
        query.append("status, created_at, processed_at ");
        query.append("FROM join_application ");
        query.append("WHERE application_id = ?");

        jdbcUtil.setSqlAndParameters(
                query.toString(),
                new Object[] {applicationId}
        );

        try {
            ResultSet rs = jdbcUtil.executeQuery();

            if (rs.next())
                return map(rs);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }

        return null;
    }

    // 특정 팀에 들어온 가입 신청 목록 조회
    public List<JoinApplication> findApplicationsByTeamId(int teamId) {
        StringBuffer query = new StringBuffer();
        query.append("SELECT application_id, team_id, member_id, ");
        query.append("status, created_at, processed_at ");
        query.append("FROM join_application ");
        query.append("WHERE team_id = ? ");
        query.append("ORDER BY created_at DESC");

        jdbcUtil.setSqlAndParameters(
                query.toString(),
                new Object[] {teamId}
        );

        try {
            ResultSet rs = jdbcUtil.executeQuery();

            List<JoinApplication> applicationList =
                    new ArrayList<JoinApplication>();

            while (rs.next())
                applicationList.add(map(rs));

            return applicationList;

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }

        return null;
    }

    // 특정 회원이 보낸 가입 신청 목록 조회
    public List<JoinApplication> findApplicationsByApplicantId(
            int applicantId) {

        StringBuffer query = new StringBuffer();
        query.append("SELECT application_id, team_id, member_id, ");
        query.append("status, created_at, processed_at ");
        query.append("FROM join_application ");
        query.append("WHERE member_id = ? ");
        query.append("ORDER BY created_at DESC");

        jdbcUtil.setSqlAndParameters(
                query.toString(),
                new Object[] {applicantId}
        );

        try {
            ResultSet rs = jdbcUtil.executeQuery();

            List<JoinApplication> applicationList =
                    new ArrayList<JoinApplication>();

            while (rs.next())
                applicationList.add(map(rs));

            return applicationList;

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }

        return null;
    }

    // 동일한 팀에 처리 대기 중인 신청이 있는지 확인
    public boolean existingPendingApplication(
            int teamId, int applicantId) {

        String query =
                "SELECT COUNT(*) FROM join_application "
                        + "WHERE team_id = ? "
                        + "AND member_id = ? "
                        + "AND status = 'P'";

        jdbcUtil.setSqlAndParameters(
                query,
                new Object[] {teamId, applicantId}
        );

        try {
            ResultSet rs = jdbcUtil.executeQuery();

            if (rs.next())
                return rs.getInt(1) > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }

        return false;
    }

    // 신청 상태 변경: A(승인), R(거절)
    public int updateStatus(int applicationId, String status) {
        String update =
                "UPDATE join_application "
                        + "SET status = ?, processed_at = SYSDATE "
                        + "WHERE application_id = ? "
                        + "AND status = 'P'";

        jdbcUtil.setSqlAndParameters(
                update,
                new Object[] {status, applicationId}
        );

        try {
            int result = jdbcUtil.executeUpdate();
            jdbcUtil.commit();

            return result;

        } catch (Exception ex) {
            jdbcUtil.rollback();
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }

        return 0;
    }

    // 신청자가 처리 전 신청을 취소
    public int remove(int applicationId, int applicantId) {
        String delete =
                "DELETE FROM join_application "
                        + "WHERE application_id = ? "
                        + "AND member_id = ? "
                        + "AND status = 'P'";

        jdbcUtil.setSqlAndParameters(
                delete,
                new Object[] {applicationId, applicantId}
        );

        try {
            int result = jdbcUtil.executeUpdate();
            jdbcUtil.commit();

            return result;

        } catch (Exception ex) {
            jdbcUtil.rollback();
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }

        return 0;
    }

    // 조회 결과를 JoinApplication 객체로 변환
    private JoinApplication map(ResultSet rs) throws SQLException {
        JoinApplication application = new JoinApplication();

        application.setApplicationId(
                rs.getInt("application_id")
        );
        application.setTeamId(
                rs.getInt("team_id")
        );
        application.setUserId(
                rs.getInt("member_id")
        );
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            // 예: DB에 "P"가 들어오고 enum 상수가 PENDING인 경우
            application.setStatus("P".equals(statusStr) ? JoinApplicationStatus.PENDING : JoinApplicationStatus.valueOf(statusStr));
        }
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            application.setCreatedAt(createdTs.toLocalDateTime());
        }

        Timestamp processedTs = rs.getTimestamp("processed_at");
        if (processedTs != null) {
            application.setProcessedAt(processedTs.toLocalDateTime());
        }

        return application;
    }
}