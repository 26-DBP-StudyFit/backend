package com.DBP.StudyFit.manager;

import com.DBP.StudyFit.dao.TeamDAO;
import com.DBP.StudyFit.dao.TeamMemberDAO;
import com.DBP.StudyFit.domain.Team;
import com.DBP.StudyFit.domain.TeamMember;
import com.DBP.StudyFit.exception.NotTeamMemberException;
import org.springframework.stereotype.Service;

@Service
public class TeamManager {
    private final TeamDAO teamDAO;
    private final TeamMemberDAO teamMemberDAO;

    public TeamManager(TeamDAO teamDAO, TeamMemberDAO teamMemberDAO) {

        this.teamDAO = teamDAO;
        this.teamMemberDAO = teamMemberDAO;
    }

    public int createTeam(Team team) {
        if (team == null) {
            return -1;
        }
        return teamDAO.createTeam(team);
    }

    /** 해당 팀의 구성원이면 소속 정보를 돌려주고, 아니면 NotTeamMemberException 을 던진다. */
    public TeamMember validateMember(Integer teamId, long userId) {
        return teamMemberDAO.findByTeamIdAndMemberId(teamId, userId)
                .orElseThrow(() -> new NotTeamMemberException("해당 팀의 구성원만 이용할 수 있습니다."));
    }
}
