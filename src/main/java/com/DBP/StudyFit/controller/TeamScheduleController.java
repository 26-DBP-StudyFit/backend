package com.DBP.StudyFit.controller;

import com.DBP.StudyFit.domain.Schedule;
import com.DBP.StudyFit.domain.ScheduleType;
import com.DBP.StudyFit.domain.Team;
import com.DBP.StudyFit.domain.User;
import com.DBP.StudyFit.manager.ScheduleManager;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/schedule")
public class TeamScheduleController {

    // 로그인 시 세션에 저장하는 User 객체의 이름 / 로그인 화면 주소
    // Todo: 실제 로그인 컨트롤러와 맞출 것
    private static final String SESSION_USER = "loginUser";
    private static final String LOGIN_REDIRECT = "redirect:/login";

    private final ScheduleManager scheduleManager;

    public TeamScheduleController(ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;
    }

   // GET  /schedule/list?teamId=   팀 일정 목록 (팀원)
    @GetMapping("/list")
    public String list(@RequestParam int teamId, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SESSION_USER);
        if (user == null) {
            return LOGIN_REDIRECT;
        }

        model.addAttribute("teamId", teamId);
        try {
            model.addAttribute("schedules", scheduleManager.getSchedules(teamId, user.getUserId()));
            model.addAttribute("leader", scheduleManager.isLeader(teamId, user.getUserId()));
        } catch (SecurityException e) {
            model.addAttribute("schedules", List.of());
            model.addAttribute("leader", false);
            model.addAttribute("error", e.getMessage());
        }
        return "schedule/list";
    }

    // GET  /schedule/create?teamId= 일정 등록 화면 (팀장)
    @GetMapping("/create")
    public String createForm(@RequestParam int teamId, HttpSession session,
                             Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(SESSION_USER);
        if (user == null) {
            return LOGIN_REDIRECT;
        }
        if (!scheduleManager.isLeader(teamId, user.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "팀장만 일정을 등록할 수 있습니다.");
            return "redirect:/schedule/list?teamId=" + teamId;
        }

        model.addAttribute("teamId", teamId);
        model.addAttribute("schedule", new Schedule());
        model.addAttribute("scheduleTypes", ScheduleType.values());
        return "schedule/form";
    }

    // POST /schedule/create         일정 등록 후 목록으로 redirect (팀장)
    @PostMapping("/create")
    public String create(@RequestParam int teamId,
                         @RequestParam(required = false) String title,
                         @RequestParam(required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
                         @RequestParam(required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endAt,
                         @RequestParam(required = false) String location,
                         @RequestParam(required = false) ScheduleType scheduleType,
                         @RequestParam(required = false) String recurrenceRule,
                         HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(SESSION_USER);
        if (user == null) {
            return LOGIN_REDIRECT;
        }

        Team team = new Team();
        team.setTeamId(teamId);
        Schedule schedule = new Schedule(team, title, startAt, endAt, location, scheduleType, recurrenceRule);

        try {
            scheduleManager.createSchedule(schedule, user.getUserId());
        } catch (IllegalArgumentException e) {
            // 입력 오류: 입력값을 유지한 채 폼으로 돌아간다.
            model.addAttribute("error", e.getMessage());
            model.addAttribute("teamId", teamId);
            model.addAttribute("schedule", schedule);
            model.addAttribute("scheduleTypes", ScheduleType.values());
            return "schedule/form";
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/schedule/list?teamId=" + teamId;
        }

        return "redirect:/schedule/list?teamId=" + teamId;
    }
}