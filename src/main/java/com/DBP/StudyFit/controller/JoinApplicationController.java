package com.DBP.StudyFit.controller;

import com.DBP.StudyFit.domain.JoinApplication;
import com.DBP.StudyFit.manager.JoinApplicationManager;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JoinApplicationController {

    private final JoinApplicationManager joinApplicationManager;

    public JoinApplicationController(JoinApplicationManager joinApplicationManager) {
        this.joinApplicationManager = joinApplicationManager;
    }

    @PostMapping("/teams/{teamId}/applications")
    public String createApplication(@PathVariable int teamId, @ModelAttribute JoinApplication joinApplication) {
        joinApplication.setTeamId(teamId); // 우선 URL의 teamId 사용, 추후 로그인 구현되면 세션에서 가져옴

        int result = joinApplicationManager.createApplication(joinApplication);

        if (result == -2) {
            return "이미 가입된 신청입니다.";
        }
        if (result < 0) {
            return "팀 가입 신청 실패";
        }
        return "팀 가입 신청 성공";
    }
}
