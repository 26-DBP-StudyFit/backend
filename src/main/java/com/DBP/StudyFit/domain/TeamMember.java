package com.DBP.StudyFit.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class TeamMember {
    // ===== 컬럼 =====
    private Integer teamId;          // team_id
    private Long memberId;           // member_id (DDL상 member_id2, NUMBER(10))
    private TeamMemberRole role;     // role
    private LocalDateTime joinedAt;  // joined_at

    // ===== JOIN 결과로 채워지는 연관 객체 =====
    private User user;               // 사용자 정보
    private Team team;               // 스터디그룹 정보

    public TeamMember(long teamMemberId, long teamId, long memberId, String role, LocalDateTime joinedAt) {
    }

    public TeamMember(Integer teamId, Long memberId, TeamMemberRole role, LocalDateTime joinedAt) {
        this.teamId = teamId;
        this.memberId = memberId;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public TeamMemberRole getRole() {
        return role;
    }

    public void setRole(TeamMemberRole role) {
        this.role = role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public boolean isLeader() {
        return role == TeamMemberRole.LEADER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamMember)) return false;
        TeamMember that = (TeamMember) o;
        return Objects.equals(teamId, that.teamId) && Objects.equals(memberId, that.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId, memberId);
    }

    @Override
    public String toString() {
        return "TeamMember{teamId=" + teamId
                + ", memberId=" + memberId
                + ", role=" + role
                + ", joinedAt=" + joinedAt + "}";
    }

}
