package com.DBP.StudyFit.controller;

import com.DBP.StudyFit.domain.Team;
import com.DBP.StudyFit.manager.TeamManager;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
public class TeamController {

    private final TeamManager teamManager;

    public TeamController(TeamManager teamManager) {
        this.teamManager = teamManager;
    }


    // 팀 생성 컨트롤러
    @PostMapping("/teams")
    public String createTeam(@ModelAttribute Team team) {
        int createdId = teamManager.createTeam(team);

        if (createdId < 0) {
            return "팀 생성 실패";
        }

        return "팀 생성 완료";
    }

    // 팀 상세 조회 요청
//    @GetMapping("/teams/{teamId}")
//    public String showTeamDetail(@PathVariable int teamId, Model model) {
//        model.addAttribute("team", teamManager.findTeam(teamId));
//        return "team/detail";
//    }
}