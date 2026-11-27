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

import com.DBP.StudyFit.domain.StudyProfile;
import com.DBP.StudyFit.domain.User;
import com.DBP.StudyFit.manager.StudyProfileManager;

/**
 * 학습 성향 설문 / 재검사 요청 처리
 *
 *  GET  /profile/survey          설문 폼          → profile/survey.jsp
 *  POST /profile/survey          첫 설문 저장      → /profile/survey/result 로 redirect
 *  GET  /profile/survey/result   설문 결과 화면    → profile/surveyResult.jsp
 *  GET  /profile/survey/retake   재검사 폼         → profile/survey.jsp (mode=retake)
 *  POST /profile/survey/retake   재검사 저장       → /mypage 로 redirect
 *
 * survey.jsp 하나를 첫 설문과 재검사가 같이 쓰고, "mode" 값으로 구분함.
 */
@Controller
@RequestMapping("/profile/survey")
public class StudyProfileController {

    // 로그인 시 세션에 User 객체를 저장하는 이름 / 로그인 화면 주소 (팀과 맞출 것)
    private static final String SESSION_USER = "loginUser";
    private static final String LOGIN_REDIRECT = "redirect:/user/login";

    private static final String SURVEY_VIEW = "profile/survey";
    private static final String RESULT_VIEW = "profile/surveyResult";

    private final StudyProfileManager profileManager = StudyProfileManager.getInstance();

    /* =========================== 학습 성향 설문 =========================== */

    @GetMapping
    public String surveyForm(@SessionAttribute(name = SESSION_USER, required = false) User user,
                             Model model) {
        if (user == null) {
            return LOGIN_REDIRECT;
        }
        // 이미 설문을 했으면 재검사 화면으로 보냄
        if (profileManager.hasProfile(user.getUserId())) {
            return "redirect:/profile/survey/retake";
        }
        model.addAttribute("mode", "create");
        return SURVEY_VIEW;
    }

    @PostMapping
    public String submitSurvey(@SessionAttribute(name = SESSION_USER, required = false) User user,
                               @RequestParam(required = false) String studyTime,
                               @RequestParam(required = false) String activityMode,
                               @RequestParam(required = false) String planningStyle,
                               @RequestParam(required = false) String studyIntensity,
                               @RequestParam(required = false) String communicationStyle,
                               @RequestParam(required = false) String studyMethod,
                               Model model, RedirectAttributes redirectAttributes) {
        if (user == null) {
            return LOGIN_REDIRECT;
        }

        StudyProfile profile = new StudyProfile(user.getUserId(), studyTime, activityMode,
                planningStyle, studyIntensity, communicationStyle, studyMethod);
        try {
            profileManager.createProfile(profile);
        } catch (IllegalStateException e) {		// 이미 설문함 → 재검사로
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/profile/survey/retake";
        } catch (IllegalArgumentException e) {		// 미응답 항목 → 입력값 유지한 채 폼으로
            return backToForm(model, "create", profile, e.getMessage());
        } catch (SQLException e) {
            return backToForm(model, "create", profile, e.getMessage());
        }
        // 새로고침으로 중복 제출되지 않도록 redirect 후 결과 화면에서 다시 조회
        return "redirect:/profile/survey/result";
    }

    @GetMapping("/result")
    public String surveyResult(@SessionAttribute(name = SESSION_USER, required = false) User user,
                               Model model) {
        if (user == null) {
            return LOGIN_REDIRECT;
        }
        StudyProfile profile = profileManager.findProfile(user.getUserId());
        if (profile == null) {			// 설문 전이면 설문 화면으로
            return "redirect:/profile/survey";
        }
        model.addAttribute("studyProfile", profile);
        model.addAttribute("studyType", profile.getStudyType());
        return RESULT_VIEW;
    }

    /* =========================== 설문 재검사 =========================== */

    @GetMapping("/retake")
    public String retakeForm(@SessionAttribute(name = SESSION_USER, required = false) User user,
                             Model model) {
        if (user == null) {
            return LOGIN_REDIRECT;
        }
        StudyProfile profile = profileManager.findProfile(user.getUserId());
        if (profile == null) {			// 처음 설문을 안 했으면 첫 설문으로
            return "redirect:/profile/survey";
        }
        model.addAttribute("mode", "retake");
        model.addAttribute("studyProfile", profile);	// 기존 응답 (폼에 미리 체크할 때 사용, 선택)
        return SURVEY_VIEW;
    }

    @PostMapping("/retake")
    public String submitRetake(@SessionAttribute(name = SESSION_USER, required = false) User user,
                               @RequestParam(required = false) String studyTime,
                               @RequestParam(required = false) String activityMode,
                               @RequestParam(required = false) String planningStyle,
                               @RequestParam(required = false) String studyIntensity,
                               @RequestParam(required = false) String communicationStyle,
                               @RequestParam(required = false) String studyMethod,
                               Model model, RedirectAttributes redirectAttributes) {
        if (user == null) {
            return LOGIN_REDIRECT;
        }

        try {
            profileManager.retakeSurvey(user.getUserId(), studyTime, activityMode, planningStyle,
                    studyIntensity, communicationStyle, studyMethod);
        } catch (IllegalStateException e) {		// 처음 설문을 안 함 → 첫 설문으로
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/profile/survey";
        } catch (IllegalArgumentException | SQLException e) {
            StudyProfile input = new StudyProfile(user.getUserId(), studyTime, activityMode,
                    planningStyle, studyIntensity, communicationStyle, studyMethod);
            return backToForm(model, "retake", input, e.getMessage());
        }

        redirectAttributes.addFlashAttribute("message", "재검사 결과가 반영되었습니다.");
        return "redirect:/mypage";
    }

    /* =========================== 내부 메서드 =========================== */

    // 오류 메시지와 입력값을 담아 설문 폼으로 되돌림
    private String backToForm(Model model, String mode, StudyProfile input, String message) {
        model.addAttribute("mode", mode);
        model.addAttribute("studyProfile", input);
        model.addAttribute("error", message);
        return SURVEY_VIEW;
    }
}
