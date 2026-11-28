package com.DBP.StudyFit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.DBP.StudyFit.domain.StudyProfile;
import com.DBP.StudyFit.domain.User;
import com.DBP.StudyFit.exception.UserNotFoundException;
import com.DBP.StudyFit.manager.StudyProfileManager;
import com.DBP.StudyFit.manager.UserManager;

import jakarta.servlet.http.HttpSession;

/**
 * 마이페이지 조회
 *
 *  GET /mypage   회원 정보 + 설문 결과(Study Type) → user/mypage.jsp
 *
 * 화면에 넘기는 값: "user"(User, 비밀번호 제외), "studyProfile"(StudyProfile, 설문 전이면 null),
 *                  "studyType"(StudyType, 설문 전이면 null)
 * 수정·비밀번호 변경·탈퇴는 UserController가 처리하고 다시 /mypage로 돌아온다.
 */
@Controller
public class MyPageController {

    private final UserManager userManager;
    private final StudyProfileManager profileManager;

    public MyPageController(UserManager userManager, StudyProfileManager profileManager) {
        this.userManager = userManager;
        this.profileManager = profileManager;
    }

    @GetMapping("/mypage")
    public String myPage(@SessionAttribute(name = UserController.SESSION_USER, required = false) User loginUser,
                         HttpSession session, Model model) {
        if (loginUser == null) {
            return "redirect:/user/login";
        }

        User user;
        try {
            user = userManager.findUser(loginUser.getUserId());   // 최신 정보로 다시 조회
        } catch (UserNotFoundException e) {
            session.invalidate();           // 탈퇴했거나 없는 회원이면 로그아웃
            return "redirect:/user/login";
        }
        user.setPassword(null);

        StudyProfile profile = profileManager.findProfile(user.getUserId());

        model.addAttribute("user", user);
        model.addAttribute("studyProfile", profile);
        model.addAttribute("studyType", profile == null ? null : profile.getStudyType());
        return "user/mypage";
    }
}
