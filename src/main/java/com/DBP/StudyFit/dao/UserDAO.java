package com.DBP.StudyFit.dao;
import com.DBP.StudyFit.JDBCUtil;
import com.DBP.StudyFit.domain.User;
import org.springframework.stereotype.Repository;

import java.sql.*;

// Spring이 객체를 하나만 만들어 관리하므로, 요청끼리 섞이지 않도록
// JDBCUtil은 필드에 두지 않고 메서드마다 새로 만든다.
@Repository
public class UserDAO {

    public int create(User user) {
        JDBCUtil jdbcUtil = new JDBCUtil();
        /* 회원 저장 (회원가입)
         시퀀스 seq_member로 새 회원번호를 얻은 후 MEMBER 테이블에 INSERT함.
         생성된 회원번호를 반환함 (실패 시 -1을 반환)
         */
        int newId = -1;

        try {
            String query1 = "SELECT seq_member.NEXTVAL FROM dual";
            jdbcUtil.setSqlAndParameters(query1, null);
            ResultSet rs = jdbcUtil.executeQuery();
            if (rs.next())
                newId = rs.getInt(1);

            StringBuffer insert = new StringBuffer();
            insert.append("INSERT INTO member (member_id, email, password_hash, member_name, gender, major, student_id) ");
            insert.append("VALUES (?, ?, ?, ?, ?, ?, ?)");
            jdbcUtil.setSqlAndParameters(insert.toString(), new Object[] {newId, user.getEmail(), user.getPassword(),
                    user.getName(), user.getGender(), user.getMajor(), user.getStudentId()});
            jdbcUtil.executeUpdate();

            jdbcUtil.commit();
            user.setUserId(newId);
            return newId;

        } catch (Exception ex) {
            jdbcUtil.rollback();	// 트랜잭션 rollback 실행
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }
        return -1;
    }

    public User findUserById(int userId) {
        JDBCUtil jdbcUtil = new JDBCUtil();
        /* 마이페이지 조회
         * 검색 결과를 User 객체에 저장해서 반환함 (검색 결과가 없을 경우 null을 반환)
         */
        StringBuffer query = new StringBuffer();
        query.append("SELECT member_id, email, password_hash, member_name, gender, major, student_id, status, created_at, temperature ");
        query.append("FROM member ");
        query.append("WHERE member_id = ?");

        jdbcUtil.setSqlAndParameters(query.toString(), new Object[] {userId});

        try {
            ResultSet rs = jdbcUtil.executeQuery();
            if (rs.next()) {		// 검색 결과 존재
                User user = new User();

                user.setUserId(rs.getInt("member_id"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password_hash"));
                user.setName(rs.getString("member_name"));
                user.setGender(rs.getString("gender"));
                user.setMajor(rs.getString("major"));
                user.setStudentId(rs.getString("student_id"));
                user.setStatus(rs.getString("status"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                // getDouble은 NULL을 0.0으로 바꾸므로, NULL이면 null 그대로 넣음
                double temperature = rs.getDouble("temperature");
                user.setTemperature(rs.wasNull() ? null : temperature);

                return user;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();		// ResultSet, PreparedStatement, Connection 등 해제
        }
        return null;
    }

    public User findUserByEmail(String email) {
        JDBCUtil jdbcUtil = new JDBCUtil();
        /* 로그인, 이메일을 회원 아이디로 사용
         * 검색 결과를 User 객체에 저장해서 반환함 (검색 결과가 없을 경우 null을 반환)
         */
        StringBuffer query = new StringBuffer();
        query.append("SELECT member_id, email, password_hash, member_name, gender, major, student_id, status, created_at, temperature ");
        query.append("FROM member ");
        query.append("WHERE email = ?");

        jdbcUtil.setSqlAndParameters(query.toString(), new Object[] {email});

        try {
            ResultSet rs = jdbcUtil.executeQuery();
            if (rs.next()) {		// 검색 결과 존재
                User user = new User();

                user.setUserId(rs.getInt("member_id"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password_hash"));
                user.setName(rs.getString("member_name"));
                user.setGender(rs.getString("gender"));
                user.setMajor(rs.getString("major"));
                user.setStudentId(rs.getString("student_id"));
                user.setStatus(rs.getString("status"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                // getDouble은 NULL을 0.0으로 바꾸므로, NULL이면 null 그대로 넣음
                double temperature = rs.getDouble("temperature");
                user.setTemperature(rs.wasNull() ? null : temperature);

                return user;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }
        return null;
    }

    public boolean existingEmail(String email) {
        JDBCUtil jdbcUtil = new JDBCUtil();
        /* 이메일(아이디) 중복 여부 검사 (회원가입)
         * 이미 존재하면 true, 없으면 false를 반환함
         */
        String query = "SELECT COUNT(*) FROM member WHERE email = ?";
        jdbcUtil.setSqlAndParameters(query, new Object[] {email});

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

    public boolean existingStudentId(String studentId) {
        JDBCUtil jdbcUtil = new JDBCUtil();
        /* 학번 중복 여부 검사 (회원가입)
         * 이미 존재하면 true, 없으면 false를 반환함
         */
        String query = "SELECT COUNT(*) FROM member WHERE student_id = ?";
        jdbcUtil.setSqlAndParameters(query, new Object[] {studentId});

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

    public int update(User user) {
        JDBCUtil jdbcUtil = new JDBCUtil();
        /* 회원 기본 정보(이름, 성별, 전공) 수정 (프로필 수정)
         * 수정된 행 수를 반환함 (실패 시 0을 반환)
         */
        StringBuffer update = new StringBuffer();
        update.append("UPDATE member SET member_name = ?, gender = ?, major = ? ");
        update.append("WHERE member_id = ?");

        jdbcUtil.setSqlAndParameters(update.toString(),
                new Object[] {user.getName(), user.getGender(), user.getMajor(), user.getUserId()});

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

    public int changePassword(int userId, String newPassword) {
        JDBCUtil jdbcUtil = new JDBCUtil();
        /* 비밀번호 변경 (프로필 수정)
         * 수정된 행 수를 반환함 (실패 시 0을 반환)
         */
        String update = "UPDATE member SET password_hash = ? WHERE member_id = ?";
        jdbcUtil.setSqlAndParameters(update, new Object[] {newPassword, userId});

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

    public int withdraw(int userId) {
        JDBCUtil jdbcUtil = new JDBCUtil();
        /* 회원 탈퇴: 행을 삭제하지 않고 status를 'WITHDRAWN'으로 변경 (비활성화)
         * 수정된 행 수를 반환함 (실패 시 0을 반환)
         */
        String update = "UPDATE member SET status = 'W' WHERE member_id = ?";
        jdbcUtil.setSqlAndParameters(update, new Object[] {userId});

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
        JDBCUtil jdbcUtil = new JDBCUtil();
        /* 회원 행 실제 삭제 (테스트 데이터 정리용)
         * 삭제된 행 수를 반환함 (실패 시 0을 반환)
         */
        String delete = "DELETE FROM member WHERE member_id = ?";
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
}
