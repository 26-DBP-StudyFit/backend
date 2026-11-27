package com.DBP.StudyFit.controller;

import com.DBP.StudyFit.domain.JoinApplication;
import com.DBP.StudyFit.manager.JoinApplicationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class JoinApplicationController {

    private final JoinApplicationManager joinApplicationManager;

    public JoinApplicationController(JoinApplicationManager joinApplicationManager) {
        this.joinApplicationManager = joinApplicationManager;
    }

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
}
