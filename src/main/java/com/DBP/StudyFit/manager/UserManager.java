package com.DBP.StudyFit.manager;

import java.sql.SQLException;

import org.springframework.stereotype.Service;

import com.DBP.StudyFit.dao.UserDAO;
import com.DBP.StudyFit.domain.User;
import com.DBP.StudyFit.exception.*;
/**
 * 회원 관련 기능
 * 담당 유스케이스: 회원가입, 로그인, 마이페이지 조회(회원 정보), 프로필 수정, 회원 탈퇴
 */
@Service
public class UserManager {
    private final UserDAO userDAO;

    public UserManager(UserDAO userDAO) {  // Spring이 UserDAO를 넣어 줌
        this.userDAO = userDAO;
    }

    /* =========================== 회원가입 =========================== */

    public int create(User user) throws SQLException, ExistingUserException {
        /* 이메일·학번 중복 검사 후 회원 저장
         * 생성된 회원번호를 반환함
         */
        if (userDAO.existingEmail(user.getEmail())) {
            throw new ExistingUserException("이미 가입된 이메일입니다.");
        }
        if (userDAO.existingStudentId(user.getStudentId())) {
            throw new ExistingUserException("이미 가입된 학번입니다.");
        }

        int newId = userDAO.create(user);
        if (newId == -1) {
            throw new SQLException("회원가입 처리 중 오류가 발생했습니다.");
        }
        return newId;
    }

    public boolean existingEmail(String email) {
        /* 이메일 중복 확인 (가입 폼에서 따로 확인할 때) */
        return userDAO.existingEmail(email);
    }

    /* =========================== 로그인 =========================== */

    public User login(String email, String password)
            throws UserNotFoundException, PasswordMismatchException {
        /* 이메일로 회원을 찾고 탈퇴 여부와 비밀번호를 확인함
         * 성공하면 User 객체를 반환함 (Controller가 세션에 저장)
         */
        User user = userDAO.findUserByEmail(email);
        if (user == null || isWithdrawn(user)) {
            throw new UserNotFoundException("존재하지 않는 회원입니다.");
        }
        if (!user.getPassword().equals(password)) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

    /* =========================== 조회 =========================== */

    public User findUser(int userId) throws UserNotFoundException {
        /* 회원번호로 회원 조회 (마이페이지, 프로필 수정 폼)
         * 없거나 탈퇴한 회원이면 예외 발생
         */
        User user = userDAO.findUserById(userId);
        if (user == null || isWithdrawn(user)) {
            throw new UserNotFoundException("존재하지 않는 회원입니다.");
        }
        return user;
    }

    /* =========================== 프로필 수정 =========================== */

    public int update(User user) throws SQLException {
        /* 이름, 성별, 전공 수정
         * 수정된 행 수를 반환함
         */
        int result = userDAO.update(user);
        if (result == 0) {
            throw new SQLException("회원 정보 수정 중 오류가 발생했습니다.");
        }
        return result;
    }

    public int changePassword(int userId, String currentPassword, String newPassword)
            throws SQLException, UserNotFoundException, PasswordMismatchException {
        /* 현재 비밀번호 확인 후 새 비밀번호로 변경 */
        checkPassword(userId, currentPassword);

        int result = userDAO.changePassword(userId, newPassword);
        if (result == 0) {
            throw new SQLException("비밀번호 변경 중 오류가 발생했습니다.");
        }
        return result;
    }

    /* =========================== 회원 탈퇴 =========================== */

    public int withdraw(int userId, String password)
            throws SQLException, UserNotFoundException, PasswordMismatchException {
        /* 비밀번호 확인 후 status를 'W'로 변경 (비활성화) */
        checkPassword(userId, password);

        int result = userDAO.withdraw(userId);
        if (result == 0) {
            throw new SQLException("회원 탈퇴 처리 중 오류가 발생했습니다.");
        }
        return result;
    }

    /* =========================== 내부 메서드 =========================== */

    private void checkPassword(int userId, String password)
            throws UserNotFoundException, PasswordMismatchException {
        /* 회원가입시
        회원번호의 비밀번호와 입력한 비밀번호가 같은지 확인 */
        User user = findUser(userId);
        if (!user.getPassword().equals(password)) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }
    }

    /* 탈퇴한 회원인지 판단하는 메서드 */
    private boolean isWithdrawn(User user) {
        return "W".equals(user.getStatus());
    }
}
