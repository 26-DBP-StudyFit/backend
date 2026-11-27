package com.DBP.StudyFit.controller;

import com.DBP.StudyFit.domain.JoinApplication;
import com.DBP.StudyFit.domain.User;
import com.DBP.StudyFit.manager.JoinApplicationManager;
import com.DBP.StudyFit.manager.TeamManager;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class JoinApplicationController {

    private final JoinApplicationManager joinApplicationManager;
    private final TeamManager teamManager;

    private static final String SESSION_USER = "user";

    public JoinApplicationController(JoinApplicationManager joinApplicationManager, TeamManager teamManager) {
        this.joinApplicationManager = joinApplicationManager;
        this.teamManager = teamManager;
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
            return "redirect:/main"; // 성공하면 메인 화면으로 돌아감
        } catch (NotTeamLeaderException e) {
            ra.addFlashAttribute("error", "팀장만 승인할 수 있습니다.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", "처리 중 오류가 발생했습니다.");
        }
        return "redirect:/team/applicationDetail";
    }

    @GetMapping("/teams/applicationDetail")
    public String teamDetail(@PathVariable int teamId, Model model) {
        model.addAttribute("team", teamManager.findTeam(teamId));
        return "team/detail";   // → /WEB-INF/views/team/detail.jsp
    }
}
