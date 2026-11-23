package com.DBP.StudyFit;

import java.time.LocalDateTime;

public class TeamMember {
    private int teamMemberId;
    private Role role;
    private LocalDateTime joinedAt;
    private int teamId;
    private int userId;

    // 생성자
    public TeamMember(int teamMemberId, Role role, LocalDateTime joinedAt,
                      int teamId, int userId) {
        this.teamMemberId = teamMemberId;
        this.role = role;
        this.joinedAt = joinedAt;
        this.teamId = teamId;
        this.userId = userId;
    }

    // Getter 및 Setter
    public int getTeamMemberId() {
        return teamMemberId;
    }

    public void setTeamMemberId(int teamMemberId) {
        this.teamMemberId = teamMemberId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
