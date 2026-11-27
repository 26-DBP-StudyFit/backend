package com.DBP.StudyFit.controller;

import com.DBP.StudyFit.domain.User;
import com.DBP.StudyFit.exception.NotTeamLeaderException;
import com.DBP.StudyFit.manager.JoinApplicationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class JoinApplicationController {

    private final JoinApplicationManager joinApplicationManager;

    private static final String SESSION_USER = "user";

    public JoinApplicationController(JoinApplicationManager joinApplicationManager) {
        this.joinApplicationManager = joinApplicationManager;
    }

    @ResponseBody
    @PostMapping("/teams/{teamId}/applications")
    public ResponseEntity<String> createApplication(@PathVariable int teamId, @RequestParam int userId) {
        try {
            joinApplicationManager.createApplication(teamId, userId);
            return ResponseEntity.ok("가입 신청 완료");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


    // 팀 가입 승인
    @PostMapping("/teams/{teamId}/applications/{applicationId}/approve")
    public String approveApplication(@PathVariable int teamId, @PathVariable int applicationId,
                                     @SessionAttribute(name = SESSION_USER, required = false) User user,
                                     RedirectAttributes ra) {

        if (user == null) {
            return "redirect:/user/loginForm";
        }

        try {
            joinApplicationManager.approveApplication(teamId, applicationId, user.getUserId());
            ra.addFlashAttribute("message", "가입 요청을 승인했습니다.");
            return "redirect:/teams/" + teamId; // 성공하면 팀 홈으로 돌아감
        } catch (NotTeamLeaderException e) {
            ra.addFlashAttribute("errorMessage", "팀장만 승인할 수 있습니다.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", "처리 중 오류가 발생했습니다.");
        }

        return "redirect:/teams/" + teamId + "/applications/" + applicationId;
    }

    // 신청 상세 조회 (팀장이 신청을 확인할 때)
    @GetMapping("/teams/{teamId}/applications/{applicationId}")
    public String showApplicationDetail(@PathVariable int teamId, @PathVariable int applicationId,
                                        @SessionAttribute(name = SESSION_USER, required = false) User user,
                                        Model model) {
        if (user == null) return "redirect:/user/loginForm";

        try {
            model.addAttribute("application",
                    joinApplicationManager.getApplicationDetail(teamId, applicationId, user.getUserId()));
        } catch (IllegalStateException e) {
            return "redirect:/teams/" + teamId;
        }

        return "team/applicationDetail";
    }
}
