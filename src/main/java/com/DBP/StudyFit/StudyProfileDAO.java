package com.DBP.StudyFit;
import java.sql.*;

public class StudyProfileDAO {
    private JDBCUtil jdbcUtil = null;	// JDBCUtil 필드 선언

    public StudyProfileDAO() {			// 생성자
        jdbcUtil = new JDBCUtil();		// JDBCUtil 객체 생성
    }

    public int create(StudyProfile profile) {
        /* 첫 설문 결과 저장 (학습 성향 설문)
         * updated_at은 DEFAULT SYSDATE로 자동 입력됨
         * 저장된 행 수를 반환함 (실패 시 0을 반환)
         */
        StringBuffer insert = new StringBuffer();
        insert.append("INSERT INTO study_profile (member_id, study_time, activity_mode, planning_style, ");
        insert.append("study_intensity, communication_style, study_method, type_code) ");
        insert.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

        jdbcUtil.setSqlAndParameters(insert.toString(), new Object[] {
                profile.getUserId(), profile.getStudyTime(), profile.getActivityMode(),
                profile.getPlanningStyle(), profile.getStudyIntensity(),
                profile.getCommunicationStyle(), profile.getStudyMethod(), typeCodeOf(profile)});

        try {
            int result = jdbcUtil.executeUpdate();
            jdbcUtil.commit();
            return result;
        } catch (Exception ex) {
            jdbcUtil.rollback();	// 트랜잭션 rollback 실행
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }
        return 0;
    }

    public StudyProfile findProfileByUserId(int userId) {
        /* 회원번호로 설문 결과 조회 (마이페이지)
         * 검색 결과를 StudyProfile 객체에 저장해서 반환함 (검색 결과가 없을 경우 null을 반환)
         */
        StringBuffer query = new StringBuffer();
        query.append("SELECT member_id, study_time, activity_mode, planning_style, study_intensity, ");
        query.append("communication_style, study_method, type_code, updated_at ");
        query.append("FROM study_profile ");
        query.append("WHERE member_id = ?");

        jdbcUtil.setSqlAndParameters(query.toString(), new Object[] {userId});

        try {
            ResultSet rs = jdbcUtil.executeQuery();
            if (rs.next()) {		// 검색 결과 존재
                StudyProfile profile = new StudyProfile(
                        rs.getInt("member_id"),
                        rs.getString("study_time"),
                        rs.getString("activity_mode"),
                        rs.getString("planning_style"),
                        rs.getString("study_intensity"),
                        rs.getString("communication_style"),
                        rs.getString("study_method"));

                String typeCode = rs.getString("type_code");
                profile.setStudyType(StudyType.fromCode(typeCode == null ? null : typeCode.trim()));
                profile.setUpdatedAt(rs.getTimestamp("updated_at"));

                return profile;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();		// ResultSet, PreparedStatement, Connection 등 해제
        }
        return null;
    }

    public int update(StudyProfile profile) {
        /* 설문 재검사: 응답과 유형을 덮어쓰고 갱신 일시 기록
         * 수정된 행 수를 반환함 (실패 시 0을 반환)
         */
        StringBuffer update = new StringBuffer();
        update.append("UPDATE study_profile SET study_time = ?, activity_mode = ?, planning_style = ?, ");
        update.append("study_intensity = ?, communication_style = ?, study_method = ?, ");
        update.append("type_code = ?, updated_at = SYSDATE ");
        update.append("WHERE member_id = ?");

        jdbcUtil.setSqlAndParameters(update.toString(), new Object[] {
                profile.getStudyTime(), profile.getActivityMode(), profile.getPlanningStyle(),
                profile.getStudyIntensity(), profile.getCommunicationStyle(), profile.getStudyMethod(),
                typeCodeOf(profile), profile.getUserId()});

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

    public int remove(int userId) {
        /* 설문 결과 실제 삭제 (테스트 데이터 정리용)
         * member 행을 지우기 전에 먼저 호출해야 FK 오류가 나지 않음
         * 삭제된 행 수를 반환함 (실패 시 0을 반환)
         */
        String delete = "DELETE FROM study_profile WHERE member_id = ?";
        jdbcUtil.setSqlAndParameters(delete, new Object[] {userId});

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

    private String typeCodeOf(StudyProfile profile) {
        // StudyType 객체 → DB에 저장할 코드 문자열 (예: "101")
        return profile.getStudyType() == null ? null : profile.getStudyType().getTypeCode();
    }
}
