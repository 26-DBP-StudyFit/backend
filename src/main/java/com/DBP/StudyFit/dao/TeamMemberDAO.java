package com.DBP.StudyFit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.DBP.StudyFit.domain.Team;
import com.DBP.StudyFit.domain.TeamMember;
import com.DBP.StudyFit.domain.TeamMemberRole;
import com.DBP.StudyFit.domain.User;
import org.springframework.stereotype.Repository;

/**
 * Oracle 기준 팀 소속 정보 DAO.
 *
 * 전제: team_member(team_member_id, team_id, member_id, role, joined_at),
 * member(member_id, member_name), team(team_id, title),
 * seq_team_member 시퀀스가 존재해야 한다.
 * team_member에는 (team_id, member_id) UNIQUE 및 부모 테이블 FK가 필요하다.
 * role은 LEADER 또는 MEMBER를 사용한다.
 *
 * Connection은 Service에서 전달한다. 이 클래스는 Connection을 닫거나
 * commit/rollback하지 않는다. PreparedStatement와 ResultSet만 닫는다.
 * 팀 생성/팀장 등록, 가입 승인/팀원 등록은 각각 동일 트랜잭션으로 실행한다.
 * 권한, 정원, 신청 상태 검증 및 동시성 제어는 Service에서 수행한다.
 * 실제 Oracle 환경에서는 실행하지 않았으므로 적용 전 SQL 검증이 필요하다.
 *
 * 모든 조회는 domain.TeamMember를 반환한다. JOIN으로 얻은 회원 이름은
 * TeamMember.user(User.name)에, 팀 제목은 TeamMember.team(Team.title)에 채운다.
 */
@Repository
public class TeamMemberDAO {
    private static final String INSERT_SQL =
            "INSERT INTO team_member " +
                    "(team_member_id, team_id, member_id, role, joined_at) " +
                    "VALUES (seq_team_member.NEXTVAL, ?, ?, ?, SYSTIMESTAMP)";

    /** 팀 생성 직후 생성자를 팀장으로 등록한다. 반환값은 삽입 행 수이다. */
    public int insertLeader(Connection conn, long teamId, long memberId)
            throws SQLException {
        return insert(conn, teamId, memberId, TeamMemberRole.LEADER);
    }

    /** 가입 승인 처리 중 신청자를 일반 팀원으로 등록한다. */
    public int insertMember(Connection conn, long teamId, long memberId)
            throws SQLException {
        return insert(conn, teamId, memberId, TeamMemberRole.MEMBER);
    }

    private int insert(Connection conn, long teamId, long memberId, TeamMemberRole role)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setLong(1, teamId);
            ps.setLong(2, memberId);
            ps.setString(3, role.name());
            return ps.executeUpdate();
        }
    }

    /** 특정 팀에서 회원의 소속 정보를 조회한다. 없으면 Optional.empty(). */
    public Optional<TeamMember> findByTeamIdAndMemberId(
            Connection conn, long teamId, long memberId) throws SQLException {
        String sql = "SELECT team_id, member_id, role, joined_at " +
                "FROM team_member WHERE team_id = ? AND member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, teamId);
            ps.setLong(2, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapTeamMember(rs)) : Optional.empty();
            }
        }
    }

    /** 팀원 목록(회원 이름 포함)을 팀장 우선, 가입 일시순으로 조회한다. user.name 이 채워진다. */
    public List<TeamMember> findMembersByTeamId(Connection conn, long teamId)
            throws SQLException {
        String sql =
                "SELECT tm.team_id, tm.member_id, tm.role, tm.joined_at, m.member_name " +
                        "FROM team_member tm JOIN member m ON m.member_id = tm.member_id " +
                        "WHERE tm.team_id = ? " +
                        "ORDER BY CASE WHEN tm.role = 'LEADER' THEN 0 ELSE 1 END, " +
                        "tm.joined_at, tm.member_id";
        List<TeamMember> members = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TeamMember member = mapTeamMember(rs);
                    User user = new User();
                    user.setName(rs.getString("member_name"));
                    member.setUser(user);
                    members.add(member);
                }
            }
        }
        return members;
    }

    /** 회원이 소속된 팀(팀 제목 포함)을 최근 가입순으로 조회한다. team.title 이 채워진다. */
    public List<TeamMember> findTeamsByMemberId(Connection conn, long memberId)
            throws SQLException {
        String sql =
                "SELECT tm.team_id, tm.member_id, tm.role, tm.joined_at, t.title AS team_title " +
                        "FROM team_member tm JOIN team t ON t.team_id = tm.team_id " +
                        "WHERE tm.member_id = ? " +
                        "ORDER BY tm.joined_at DESC, tm.team_id DESC";
        List<TeamMember> teams = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TeamMember member = mapTeamMember(rs);
                    Team team = new Team();
                    team.setTitle(rs.getString("team_title"));
                    member.setTeam(team);
                    teams.add(member);
                }
            }
        }
        return teams;
    }

    /** 가입 여부 사전 확인용. 중복 등록 최종 방지는 DB UNIQUE 제약이 담당한다. */
    public boolean existsByTeamIdAndMemberId(Connection conn, long teamId, long memberId)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM team_member " +
                "WHERE team_id = ? AND member_id = ?";
        return countMembership(conn, sql, teamId, memberId) > 0;
    }

    /** 해당 회원이 지정한 팀의 팀장인지 확인한다. */
    public boolean isLeader(Connection conn, long teamId, long memberId)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM team_member " +
                "WHERE team_id = ? AND member_id = ? AND role = 'LEADER'";
        return countMembership(conn, sql, teamId, memberId) > 0;
    }

    /** 팀장을 포함한 현재 팀 인원수를 반환한다. */
    public int countByTeamId(Connection conn, long teamId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM team_member WHERE team_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("팀 인원수 조회 결과가 없습니다.");
                }
                return rs.getInt(1);
            }
        }
    }

    private int countMembership(Connection conn, String sql, long teamId, long memberId)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, teamId);
            ps.setLong(2, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("팀 소속 개수 조회 결과가 없습니다.");
                }
                return rs.getInt(1);
            }
        }
    }

    /** domain.TeamMember 필드 타입(Integer teamId, Long memberId, TeamMemberRole role)에 맞춰 매핑한다. */
    private TeamMember mapTeamMember(ResultSet rs) throws SQLException {
        return new TeamMember(
                rs.getInt("team_id"),
                rs.getLong("member_id"),
                TeamMemberRole.valueOf(rs.getString("role")),
                readDateTime(rs, "joined_at"));
    }

    private LocalDateTime readDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp value = rs.getTimestamp(column);
        return value == null ? null : value.toLocalDateTime();
    }
}