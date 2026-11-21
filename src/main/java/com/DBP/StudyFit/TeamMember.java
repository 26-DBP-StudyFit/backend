package com.DBP.StudyFit;

import java.time.LocalDateTime;

public class TeamMember {
    private int team_member_id;
    private Role role;
    private LocalDateTime joined_at;
    private int team_id;
    private int user_id;

    // 생성자
    public TeamMember(int team_member_id, Role role, LocalDateTime joined_at,
                      int teamd_id, int user_id) {
        this.team_member_id = team_member_id;
        this.role = role;
        this.joined_at = joined_at;
        this.team_id = teamd_id;
        this.user_id = user_id;
    }

    // Getter 및 Setter
    public int getTeam_member_id() {
        return team_member_id;
    }

    public void setTeam_member_id(int team_member_id) {
        this.team_member_id = team_member_id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getJoined_at() {
        return joined_at;
    }

    public void setJoined_at(LocalDateTime joined_at) {
        this.joined_at = joined_at;
    }

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
