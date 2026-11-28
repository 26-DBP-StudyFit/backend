package com.DBP.StudyFit.controller;

import java.sql.SQLException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.DBP.StudyFit.domain.User;
import com.DBP.StudyFit.exception.ExistingUserException;
import com.DBP.StudyFit.exception.PasswordMismatchException;
import com.DBP.StudyFit.exception.UserNotFoundException;
import com.DBP.StudyFit.manager.StudyProfileManager;
import com.DBP.StudyFit.manager.UserManager;

import jakarta.servlet.http.HttpSession;

/**
 * 회원 관련 요청 처리
 *
 *  GET  /user/join       회원가입 폼            → user/joinForm.jsp
 *  POST /user/join       회원가입 → 자동 로그인  → /profile/survey 로 redirect
 *  GET  /user/login      로그인 폼              → user/loginForm.jsp
 *  POST /user/login      로그인                 → 설문 전이면 /profile/survey, 아니면 / 로 redirect
 *  GET  /user/logout     로그아웃               → /user/login 으로 redirect
 *  POST /user/update     내 정보 수정(이름, 전공) → /mypage 로 redirect
 *  POST /user/password   비밀번호 변경           → /mypage 로 redirect
 *  POST /user/withdraw   회원 탈퇴              → /user/login 으로 redirect
 *
 * 로그인하면 세션 "loginUser"에 User 객체(비밀번호 제외)를 저장한다.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    public static final String SESSION_USER = "loginUser";
    private static final String LOGIN_REDIRECT = "redirect:/user/login";
    private static final String MYPAGE_REDIRECT = "redirect:/mypage";

    private static final String JOIN_VIEW = "user/joinForm";
    private static final String LOGIN_VIEW = "user/loginForm";

    private final UserManager userManager;
    private final StudyProfileManager profileManager;

    public UserController(UserManager userManager, StudyProfileManager profileManager) {
        this.userManager = userManager;
        this.profileManager = profileManager;
    }

    /* =========================== 회원가입 =========================== */

    @GetMapping("/join")
    public String joinForm() {
        return JOIN_VIEW;
    }

    @PostMapping("/join")
    public String join(@RequestParam(required = false) String email,
                       @RequestParam(required = false) String password,
                       @RequestParam(required = false) String passwordConfirm,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String studentId,
                       @RequestParam(required = false) String gender,
                       @RequestParam(required = false) String major,
                       HttpSession session, Model model) {
        User user = new User(trim(email), password, trim(name), blankToNull(gender),
                             blankToNull(major), trim(studentId));

        // 입력값 검사 (필수 항목, 비밀번호 확인)
        String error = null;
        if (isBlank(email) || isBlank(password) || isBlank(name) || isBlank(studentId)) {
            error = "이메일, 비밀번호, 이름, 학번은 필수 입력 항목입니다.";
        } else if (!password.equals(passwordConfirm)) {
            error = "비밀번호와 비밀번호 확인이 일치하지 않습니다.";
        }
        if (error != null) {
            return backToJoinForm(model, user, error);
        }

        try {
            userManager.create(user);           // 성공하면 user에 회원번호가 채워짐
        } catch (ExistingUserException | SQLException e) {
            return backToJoinForm(model, user, e.getMessage());
        }

        // 가입 후 바로 로그인 상태로 만들고 설문 화면으로
        saveLoginUser(session, user);
        return "redirect:/profile/survey";
    }

    /* =========================== 로그인 / 로그아웃 =========================== */

    @GetMapping("/login")
    public String loginForm() {
        return LOGIN_VIEW;
    }

    @PostMapping("/login")
    public String login(@RequestParam(required = false) String email,
                        @RequestParam(required = false) String password,
                        HttpSession session, Model model) {
        if (isBlank(email) || isBlank(password)) {
            return backToLoginForm(model, email, "이메일과 비밀번호를 입력해 주세요.");
        }

        User user;
        try {
            user = userManager.login(trim(email), password);
        } catch (UserNotFoundException | PasswordMismatchException e) {
            // 어느 쪽이 틀렸는지 알려주지 않음 (가입된 이메일 노출 방지)
            return backToLoginForm(model, email, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        saveLoginUser(session, user);

        // 설문을 아직 안 했으면 설문 화면으로
        if (!profileManager.hasProfile(user.getUserId())) {
            return "redirect:/profile/survey";
        }
        // TODO: "/" 매핑(메인 화면)이 아직 없어 로그인 후 404 발생. 홈화면 주소 맞추기
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return LOGIN_REDIRECT;
    }

    /* =========================== 내 정보 수정 =========================== */

    @PostMapping("/update")
    public String update(@SessionAttribute(name = SESSION_USER, required = false) User loginUser,
                         @RequestParam(required = false) String name,
                         @RequestParam(required = false) String gender,   // 폼에 없으면 기존 값 유지
                         @RequestParam(required = false) String major,
                         HttpSession session, RedirectAttributes ra) {
        if (loginUser == null) {
            return LOGIN_REDIRECT;
        }
        if (isBlank(name)) {
            ra.addFlashAttribute("error", "이름을 입력해 주세요.");
            return MYPAGE_REDIRECT;
        }

        try {
            User user = userManager.findUser(loginUser.getUserId());   // 기존 정보 불러오기
            user.setName(trim(name));
            if (gender != null) {
                user.setGender(blankToNull(gender));
            }
            user.setMajor(blankToNull(major));
            userManager.update(user);

            saveLoginUser(session, user);       // 세션의 이름·전공도 새 값으로
            ra.addFlashAttribute("message", "내 정보가 수정되었습니다.");
        } catch (UserNotFoundException e) {
            session.invalidate();
            return LOGIN_REDIRECT;
        } catch (SQLException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return MYPAGE_REDIRECT;
    }

    /* =========================== 비밀번호 변경 =========================== */

    @PostMapping("/password")
    public String changePassword(@SessionAttribute(name = SESSION_USER, required = false) User loginUser,
                                 @RequestParam(required = false) String currentPassword,
                                 @RequestParam(required = false) String newPassword,
                                 @RequestParam(required = false) String newPasswordConfirm,
                                 HttpSession session, RedirectAttributes ra) {
        if (loginUser == null) {
            return LOGIN_REDIRECT;
        }
        if (isBlank(currentPassword) || isBlank(newPassword)) {
            ra.addFlashAttribute("error", "기존 비밀번호와 새 비밀번호를 입력해 주세요.");
            return MYPAGE_REDIRECT;
        }
        if (!newPassword.equals(newPasswordConfirm)) {
            ra.addFlashAttribute("error", "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            return MYPAGE_REDIRECT;
        }

        try {
            userManager.changePassword(loginUser.getUserId(), currentPassword, newPassword);
            ra.addFlashAttribute("message", "비밀번호가 변경되었습니다.");
        } catch (PasswordMismatchException e) {
            ra.addFlashAttribute("error", "기존 비밀번호가 일치하지 않습니다.");
        } catch (UserNotFoundException e) {
            session.invalidate();
            return LOGIN_REDIRECT;
        } catch (SQLException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return MYPAGE_REDIRECT;
    }

    /* =========================== 회원 탈퇴 =========================== */

    @PostMapping("/withdraw")
    public String withdraw(@SessionAttribute(name = SESSION_USER, required = false) User loginUser,
                           @RequestParam(required = false) String password,
                           HttpSession session, RedirectAttributes ra) {
        if (loginUser == null) {
            return LOGIN_REDIRECT;
        }
        if (isBlank(password)) {
            ra.addFlashAttribute("error", "비밀번호를 입력해 주세요.");
            return MYPAGE_REDIRECT;
        }

        try {
            userManager.withdraw(loginUser.getUserId(), password);
        } catch (PasswordMismatchException e) {
            ra.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return MYPAGE_REDIRECT;
        } catch (UserNotFoundException e) {
            session.invalidate();
            return LOGIN_REDIRECT;
        } catch (SQLException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return MYPAGE_REDIRECT;
        }

        session.invalidate();               // 탈퇴하면 로그아웃
        ra.addFlashAttribute("message", "회원 탈퇴가 완료되었습니다.");
        return LOGIN_REDIRECT;
    }

    /* =========================== 내부 메서드 =========================== */

    // 비밀번호를 지운 뒤 세션에 저장 (화면에 비밀번호가 나가지 않도록)
    private void saveLoginUser(HttpSession session, User user) {
        user.setPassword(null);
        session.setAttribute(SESSION_USER, user);
    }

    private String backToJoinForm(Model model, User input, String error) {
        input.setPassword(null);            // 비밀번호는 다시 입력받음
        model.addAttribute("user", input);
        model.addAttribute("error", error);
        return JOIN_VIEW;
    }

    private String backToLoginForm(Model model, String email, String error) {
        model.addAttribute("email", email);
        model.addAttribute("error", error);
        return LOGIN_VIEW;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    private static String blankToNull(String s) {
        return isBlank(s) ? null : s.trim();
    }
}
