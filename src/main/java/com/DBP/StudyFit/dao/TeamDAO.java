package com.DBP.StudyFit.dao;

import com.DBP.StudyFit.JDBCUtil;
import com.DBP.StudyFit.domain.RegionCode;
import com.DBP.StudyFit.domain.Study_Category;
import com.DBP.StudyFit.domain.Team;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.sql.ResultSet;

@Component
@Repository
public class TeamDAO {
    private JDBCUtil jdbcUtil = null;

    public TeamDAO() {
        jdbcUtil = new JDBCUtil();
    }

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // LocalTime -> "HH:mm" 문자열 (null이면 null)
    private String toTimeString(LocalTime time) {
        return time != null ? time.format(TIME_FMT) : null;
    }

    // "HH:mm" 문자열 -> LocalTime (null이면 null)
    private LocalTime toLocalTime(String time) {
        return time != null ? LocalTime.parse(time, TIME_FMT) : null;
    }

    private Team map(ResultSet rs) throws SQLException {
        Team team = new Team();
        team.setTeamId(rs.getInt("team_id"));
        team.setTitle(rs.getString("title"));
        team.setStudyCategory(Study_Category.valueOf(rs.getString("study_category")));
        team.setRecruitCount(rs.getInt("recruit_count"));
        team.setRegionCode(RegionCode.valueOf(rs.getString("region_code")));
        team.setStudyDay(DayOfWeek.valueOf(rs.getString("preferred_days")));
        team.setPreferredStartTime(toLocalTime(rs.getString("preferred_start_time")));
        team.setPreferredEndTime(toLocalTime(rs.getString("preferred_end_time")));

        Timestamp createdAt = rs.getTimestamp("created_at");
        team.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());
        team.setDescription(rs.getString("description"));
        team.setTemperature(rs.getLong("temperature"));

        return team;
    }

    // 팀 생성
    public int createTeam(Team team) {
        int newTeamId = -1;

        try {
            // 오라클 시퀀스에서 다음 팀 ID 조회
            String seqQuery = "SELECT seq_team.NEXTVAL FROM dual";
            jdbcUtil.setSqlAndParameters(seqQuery, null);

            ResultSet rs = jdbcUtil.executeQuery();
            if (rs.next()) {
                newTeamId = rs.getInt(1);
            }

            StringBuffer insert = new StringBuffer();
            insert.append("INSERT INTO team ");
            insert.append("(team_id, title, study_category, recruit_count, region_code, preferred_days, preferred_start_time, preferred_end_time, created_at, description, temperature) ");
            insert.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, ?, ?)");

//            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
//
//            String startTimeStr = team.getPreferredStartTime() != null
//                    ? team.getPreferredStartTime().format(timeFormatter)
//                    : null;
//            String endTimeStr = team.getPreferredEndTime() != null
//                    ? team.getPreferredEndTime().format(timeFormatter)
//                    : null;

            Object[] parameters = new Object[]{
                    newTeamId,
                    team.getTitle(),
                    team.getStudyCategory().name(),
                    team.getRecruitCount(),
                    team.getRegionCode().name(),
                    team.getStudyDay().name(),
                    toTimeString(team.getPreferredStartTime()),
                    toTimeString(team.getPreferredEndTime()),
                    team.getDescription(),
                    team.getTemperature()
            };

            jdbcUtil.setSqlAndParameters(insert.toString(), parameters);
            jdbcUtil.executeUpdate();
            jdbcUtil.commit();

            team.setTeamId(newTeamId);
            return newTeamId;

        } catch (Exception ex) {
            jdbcUtil.rollback();
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }

        return -1;
    }

    // ID로 조회
    public Team findTeamById(int teamId) {
        StringBuffer query = new StringBuffer();
        query.append("SELECT team_id, title, study_category, recruit_count, region_code, ");
        query.append("preferred_days, preferred_start_time, preferred_end_time, created_at, description, temperature ");
        query.append("FROM team WHERE team_id = ?");

        jdbcUtil.setSqlAndParameters(query.toString(), new Object[]{teamId});

        try {
            ResultSet rs = jdbcUtil.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }
        return null;
    }

    // 팀 전체 조회
    public List<Team> findAllTeams() {
        StringBuffer query = new StringBuffer();
        query.append("SELECT team_id, title, study_category, recruit_count, region_code, ");
        query.append("preferred_days, preferred_start_time, preferred_end_time, created_at, description, temperature ");
        query.append("FROM team");

        jdbcUtil.setSqlAndParameters(query.toString(), null);

        try {
            ResultSet rs = jdbcUtil.executeQuery();
            List<Team> teamList = new ArrayList<>();

            while (rs.next()) {
                teamList.add(map(rs));
            }

            return teamList;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }
        return null;
    }

    // 수정
    public int updateTeam(Team team) {
        Team existingTeam = findTeamById(team.getTeamId());

        if (existingTeam == null) {
            return 0;
        }

        if (team.getTitle() == null || team.getTitle().trim().isEmpty()) {
            team.setTitle(existingTeam.getTitle());
        }

        if (team.getStudyCategory() == null) {
            team.setStudyCategory(existingTeam.getStudyCategory());
        }

        if (team.getRecruitCount() <= 0) {
            team.setRecruitCount(existingTeam.getRecruitCount());
        }

        if (team.getRegionCode() == null) {
            team.setRegionCode(existingTeam.getRegionCode());
        }

        if (team.getStudyDay() == null) {
            team.setStudyDay(existingTeam.getStudyDay());
        }

        if (team.getPreferredStartTime() == null) {
            team.setPreferredStartTime(existingTeam.getPreferredStartTime());
        }

        if (team.getPreferredEndTime() == null) {
            team.setPreferredEndTime(existingTeam.getPreferredEndTime());
        }

        if (team.getDescription() == null) {
            team.setDescription(existingTeam.getDescription());
        }

        if (team.getTemperature() == null || team.getTemperature() < 0) {
            team.setTemperature(existingTeam.getTemperature());
        }

        StringBuffer query = new StringBuffer();
        query.append("UPDATE team SET ");
        query.append("title = ?, ");
        query.append("study_category = ?, ");
        query.append("recruit_count = ?, ");
        query.append("region_code = ?, ");
        query.append("preferred_days = ?, ");
        query.append("preferred_start_time = ?, ");
        query.append("preferred_end_time = ?, ");
        query.append("description = ?, ");
        query.append("temperature = ?, ");
        query.append("WHERE team_id = ?");

        Object[] parameters = new Object[]{
                team.getTitle(),
                team.getStudyCategory().name(),
                team.getRecruitCount(),
                team.getRegionCode().name(),
                team.getStudyDay().name(),
                toTimeString(team.getPreferredStartTime()),
                toTimeString(team.getPreferredEndTime()),
                team.getDescription(),
                team.getTemperature(),
                team.getTeamId()
        };

        jdbcUtil.setSqlAndParameters(query.toString(), parameters);

        try {
            return jdbcUtil.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }
        return 0;
    }

    // 삭제
    public int deleteTeam(Long teamId) {
        StringBuffer query = new StringBuffer();
        query.append("DELETE FROM team ");
        query.append("WHERE team_id = ?");

        Object[] parameters = new Object[]{teamId};

        jdbcUtil.setSqlAndParameters(query.toString(), parameters);

        try {
            return jdbcUtil.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }
        return 0;
    }
}
