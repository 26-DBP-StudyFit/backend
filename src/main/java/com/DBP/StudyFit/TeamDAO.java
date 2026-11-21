package com.DBP.StudyFit;

import java.sql.*;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.sql.ResultSet;

public class TeamDAO {
    private JDBCUtil jdbcUtil = null;

    public TeamDAO() {
        jdbcUtil = new JDBCUtil();
    }

    private Team map(ResultSet rs) throws SQLException {
        Team team = new Team();
        team.setTeam_id(rs.getInt("team_id"));
        team.setTitle(rs.getString("title"));
        team.setStudy_category(Study_Category.valueOf(rs.getString("study_category")));
        team.setRecruit_count(rs.getInt("recruit_count"));
        team.setRegion_code(RegionCode.valueOf(rs.getString("region_code")));
        team.setStudyDays(DayOfWeek.valueOf(rs.getString("preferred_days")));
        team.setPreferred_start_time(rs.getTime("preferred_start_time") != null ? rs.getTime("preferred_start_time").toLocalTime() : null);
        team.setPreferred_end_time(rs.getTime("preferred_end_time") != null ? rs.getTime("preferred_end_time").toLocalTime() : null);

        Timestamp createdAt = rs.getTimestamp("created_at");
        team.setCreated_at(
                createdAt == null ? null : createdAt.toLocalDateTime()
        );
        return team;
    }

    //팀 생성
    public int createTeam(Team team) {
        int newTeamId = -1;

        try {
            // 오라클 시퀀스를 이용해 다음 PK 값을 미리 조회 (시퀀스 이름은 프로젝트 설정에 맞게 수정하세요, 예: seq_team)
            String seqQuery = "SELECT seq_team.NEXTVAL FROM dual";
            jdbcUtil.setSqlAndParameters(seqQuery, null);

            ResultSet rs = jdbcUtil.executeQuery();
            if (rs.next()) {
                newTeamId = rs.getInt(1);
            }

            // 가져온 PK와 팀의 전체 필드 정보를 포함한 INSERT 쿼리 작성
            StringBuffer insert = new StringBuffer();
            insert.append("INSERT INTO team ");
            insert.append("(team_id, title, study_category, recruit_count, region_code, preferred_days, preferred_start_time, preferred_end_time, created_at) ");
            insert.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, SYSDATE)");

            // Object 배열로 파라미터값 저장해두기
            Object[] parameters = new Object[]{
                    newTeamId,
                    team.getTitle(),
                    team.getStudy_category().name(),
                    team.getRecruit_count(),
                    team.getRegion_code().name(),
                    team.getStudyDays().name(),
                    team.getPreferred_start_time(),
                    team.getPreferred_end_time()
            };

            jdbcUtil.setSqlAndParameters(insert.toString(), parameters);
            jdbcUtil.executeUpdate(); // 쿼리 실행

            // 성공했을 때 커밋
            jdbcUtil.commit();

            // 자바 객체에도 생성된 ID 동기화
            team.setTeam_id(newTeamId);

            return newTeamId; // 생성된 팀 ID 반환

        } catch (Exception ex) {
            // 에러 발생 시 롤백
            jdbcUtil.rollback();
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }

        return -1;
    }


        //id로 조회
        public Team findTeamById ( int teamId){
            StringBuffer query = new StringBuffer();
            query.append("SELECT team_id, title, study_category, recruit_count, region_code, ");
            query.append("preferred_days, preferred_start_time, preferred_end_time, created_at ");
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

        //Team 전체 조회
        public List<Team> findAllTeams () {
            StringBuffer query = new StringBuffer();
            query.append("SELECT team_id, title, study_category, recruit_count, region_code, ");
            query.append("preferred_days, preferred_start_time, preferred_end_time, created_at ");
            query.append("FROM team");

            jdbcUtil.setSqlAndParameters(query.toString(), null);

            try {
                ResultSet rs = jdbcUtil.executeQuery();
                List<Team> teamList = new ArrayList<>();

                while (rs.next()) {
                    teamList.add(map(rs)); // 반복문 안에서도 map 메서드 재사용
                }

                return teamList;

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                jdbcUtil.close();
            }
            return null;
        }

        //수정
        public int updateTeam (Team team){
            Team existingTeam = findTeamById(team.getTeam_id());

            if (existingTeam == null) {
                return 0; // 수정할 팀이 존재하지 않음
            }

            // 제목 검증
            if (team.getTitle() == null || team.getTitle().trim().isEmpty()) {
                team.setTitle(existingTeam.getTitle());
            }

            // 스터디 카테고리 검증
            if (team.getStudy_category() == null) {
                team.setStudy_category(existingTeam.getStudy_category());
            }

            // 모집 인원 검증 (TeamDAO에서 처리하기..)
            if (team.getRecruit_count() <= 0) { // 인원이 0이하로 잘못 들어온 경우
                team.setRecruit_count(existingTeam.getRecruit_count());
            }

            // 지역 코드 검증
            if (team.getRegion_code() == null) {
                team.setRegion_code(existingTeam.getRegion_code());
            }

            // 선호 요일 검증
            if (team.getStudyDays() == null) {
                team.setStudyDays(existingTeam.getStudyDays());
            }

            // 선호 시작 시간 검증
            if (team.getPreferred_start_time() == null) {
                team.setPreferred_start_time(existingTeam.getPreferred_start_time());
            }

            // 선호 종료 시간 검증
            if (team.getPreferred_end_time() == null) {
                team.setPreferred_end_time(existingTeam.getPreferred_end_time());
            }

            StringBuffer query = new StringBuffer();
            query.append("UPDATE team SET ");
            query.append("title = ?, ");
            query.append("study_category = ?, ");
            query.append("recruit_count = ?, ");
            query.append("region_code = ?, ");
            query.append("preferred_days = ?, ");
            query.append("preferred_start_time = ?, ");
            query.append("preferred_end_time = ? ");
            query.append("WHERE team_id = ?");

            Object[] parameters = new Object[]{
                    team.getTitle(),
                    team.getStudy_category().name(),
                    team.getRecruit_count(),
                    team.getRegion_code().name(),
                    team.getStudyDays().name(),
                    team.getPreferred_start_time(),
                    team.getPreferred_end_time(),
                    team.getTeam_id()
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


        //삭제
        public int deleteTeam (Long teamId){
            StringBuffer query = new StringBuffer();
            query.append("DELETE FROM team ");
            query.append("WHERE team_id = ?"); // 삭제할 대상 지정

            Object[] parameters = new Object[]{teamId};

            jdbcUtil.setSqlAndParameters(query.toString(), parameters);

            try {
                // 데이터에 변화를 주는 쿼리이므로 executeUpdate() 사용
                return jdbcUtil.executeUpdate(); // 정상 삭제 시 삭제된 행의 개수(통상 1) 반환
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                jdbcUtil.close(); // 자원 반환 필수
            }
            return 0; // 삭제 실패 시 0 반환
        }
    }
