package com.DBP.StudyFit.manager;

import com.DBP.StudyFit.dao.TeamDAO;
import com.DBP.StudyFit.domain.Team;
import org.springframework.stereotype.Service;

@Service
public class TeamManager {
    private final TeamDAO teamDAO;

    public TeamManager(TeamDAO teamDAO) {
        this.teamDAO = teamDAO;
    }

    public int createTeam(Team team) {
        if (team == null) {
            return -1;
        }
        return teamDAO.createTeam(team);
    }
}
