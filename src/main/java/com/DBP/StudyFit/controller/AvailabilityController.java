package com.DBP.StudyFit.controller;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.DBP.StudyFit.domain.User;
import com.DBP.StudyFit.dto.AvailabilityForm;
import com.DBP.StudyFit.dto.CommonAvailabilityResult;
import com.DBP.StudyFit.exception.NotTeamMemberException;
import com.DBP.StudyFit.manager.AvailabilityManager;
import com.DBP.StudyFit.manager.TeamManager;

/**
 * UC-D-01 활동 가능 시간 조회·등록·수정 (FR-D-01), UC-D-02 팀 공통 가능 시간 계산·시각화 (FR-D-02).
 *
 *  GET  /availability/edit?teamId=   편집 화면 (기존값 표시, 없으면 빈 폼)
 *  POST /availability/save           저장 → 성공 시 /availability/edit?teamId= 로 redirect
 *  GET  /availability/common?teamId= 팀 공통 가능 시간 계산·시각화 (팀원)
 *
 * [Spring 사용 부분] @Controller, @RequestMapping, @GetMapping, @PostMapping, @ModelAttribute,
 *                    @RequestParam, @SessionAttribute, @ExceptionHandler (기본 MVC 기능만 사용)
 */
@Controller
@RequestMapping("/availability")
public class AvailabilityController {

    /** 로그인 처리(UC-A-03)가 세션에 User 를 저장하는 키 */
    private static final String SESSION_USER = "loginUser";

    private static final Logger log = LoggerFactory.getLogger(AvailabilityController.class);

    private final TeamManager teamManager;
    private final AvailabilityManager availabilityManager;

    public AvailabilityController(TeamManager teamManager, AvailabilityManager availabilityManager) {
        this.teamManager = teamManager;
        this.availabilityManager = availabilityManager;
    }

    /** 요일 선택 목록 (MONDAY → 월). View 가 select 를 만들 때 쓴다. */
    @ModelAttribute("dayOptions")
    public Map<String, String> dayOptions() {
        Map<String, String> options = new LinkedHashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            options.put(day.name(), day.getDisplayName(TextStyle.SHORT, Locale.KOREAN));
        }
        return options;
    }

    // ---------------------------------------------------------------- 편집 화면 조회

    @GetMapping("/edit")
    public String edit(@RequestParam Long teamId,
                       @SessionAttribute(name = SESSION_USER, required = false) User user,
                       Model model) {
        if (user == null) {
            return "redirect:/user/login";
        }
        long userId = user.getUserId();

        teamManager.validateMember(teamId, userId);
        AvailabilityForm form = availabilityManager.getAvailability(teamId, userId);

        model.addAttribute("teamId", teamId);
        model.addAttribute("form", form);
        return "availability/edit";
    }

    // ---------------------------------------------------------------- 저장

    @PostMapping("/save")
    public String save(@RequestParam Long teamId,
                       @ModelAttribute("form") AvailabilityForm form,
                       @SessionAttribute(name = SESSION_USER, required = false) User user,
                       Model model, RedirectAttributes redirectAttributes) {
        if (user == null) {
            return "redirect:/user/login";
        }
        long userId = user.getUserId();

        // POST 에서도 구성원 여부를 다시 확인한다. 저장 대상은 항상 '로그인 사용자 본인'이다.
        teamManager.validateMember(teamId, userId);
        form.setTeamId(teamId);
        model.addAttribute("teamId", teamId);

        Map<String, String> errors;
        try {
            errors = availabilityManager.saveAvailability(teamId, userId, form.getSlots());
        } catch (IllegalStateException e) {
            // 저장 오류: DAO 가 이미 롤백했다. 입력값(form)을 유지한 채 재시도를 안내한다.
            log.error("활동 가능 시간 저장 실패 teamId={}, userId={}", teamId, userId, e);
            model.addAttribute("message", "저장 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
            return "availability/edit";
        }

        if (!errors.isEmpty()) {
            // 입력 오류: 저장 DAO 는 호출되지 않았다. 입력값(form)은 그대로 남아 있다.
            model.addAttribute("errors", errors);
            return "availability/edit";
        }

        redirectAttributes.addAttribute("teamId", teamId);
        redirectAttributes.addFlashAttribute("savedMessage", "활동 가능 시간이 저장되었습니다.");
        return "redirect:/availability/edit";
    }

    // ---------------------------------------------------------------- 공통 가능 시간 (FR-D-02)

    @GetMapping("/common")
    public String common(@RequestParam Long teamId,
                         @SessionAttribute(name = SESSION_USER, required = false) User user,
                         Model model) {
        if (user == null) {
            return "redirect:/user/login";
        }
        long userId = user.getUserId();

        teamManager.validateMember(teamId, userId);
        CommonAvailabilityResult result = availabilityManager.calculateCommonAvailability(teamId);

        // 상태별 화면(AVAILABLE / NO_COMMON_TIME / INCOMPLETE)은 calculationStatus 로 구분한다.
        model.addAttribute("teamId", teamId);
        model.addAttribute("commonTimeSlots", result.getCommonTimeSlots());
        model.addAttribute("missingMemberIds", result.getMissingMemberIds());
        model.addAttribute("calculationStatus", result.getCalculationStatus());
        model.addAttribute("message", result.getMessage());
        return "availability/common";
    }

    // ---------------------------------------------------------------- 예외 처리

    @ExceptionHandler(NotTeamMemberException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleNotTeamMember(NotTeamMemberException e, Model model) {
        model.addAttribute("message", e.getMessage());
        model.addAttribute("backUri", "/main");
        return "error/message";
    }

    /** 조회 중 DB 오류 (저장 오류는 save() 안에서 처리). SQL 상세는 노출하지 않는다. */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDbFailure(IllegalStateException e, Model model) {
        log.error("활동 가능 시간 처리 중 DB 오류", e);
        model.addAttribute("message", "처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
        model.addAttribute("backUri", "/main");
        return "error/message";
    }
}