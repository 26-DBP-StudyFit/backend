package com.DBP.StudyFit.dao;

import com.DBP.StudyFit.JDBCUtil;
import com.DBP.StudyFit.domain.Availability;
import com.DBP.StudyFit.domain.TeamMember;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 활동 가능 시간 DAO (Oracle, JDBCUtil 방식).
 *
 * 전제: availability(availability_id, team_member_id, day_of_week, start_minute, end_minute),
 * seq_availability 시퀀스, team_member(team_member_id, team_id, member_id, ...)가 존재해야 한다.
 * day_of_week 는 DayOfWeek.getValue() (1=월 ... 7=일), start_minute/end_minute 은
 * 0시부터의 경과 분(예: 18:30 → 1110)으로 저장한다.
 *
 * 팀 구성원(team_member)은 (team_id, member_id)로 찾는다.
 * JDBCUtil 은 메서드마다 새로 만든다 (싱글톤 DAO 의 필드에 두지 않는다).
 */
@Repository
public class AvailabilityDAO {
    /**
     * 해당 팀에서 해당 회원이 등록한 활동 가능 시간을 조회한다. 정렬은 하지 않는다.
     * 등록 내역이 없으면 빈 리스트, DB 오류가 나면 null 을 반환한다.
     */
    public List<Availability> findByTeamAndUser(long teamId, long userId) {
        JDBCUtil jdbcUtil = new JDBCUtil();
        StringBuffer query = new StringBuffer();
        query.append("SELECT a.availability_id, a.day_of_week, a.start_minute, a.end_minute ");
        query.append("FROM availability a ");
        query.append("JOIN team_member tm ON tm.team_member_id = a.team_member_id ");
        query.append("WHERE tm.team_id = ? AND tm.member_id = ?");
        jdbcUtil.setSqlAndParameters(query.toString(), new Object[] {teamId, userId});

        try {
            ResultSet rs = jdbcUtil.executeQuery();
            if (rs == null) {          // JDBCUtil 은 질의 실패 시 null 을 반환한다
                return null;
            }
            List<Availability> result = new ArrayList<>();
            TeamMember member = new TeamMember((int) teamId, userId, null, null);
            while (rs.next()) {
                result.add(new Availability(
                        rs.getLong("availability_id"),
                        member,
                        DayOfWeek.of(rs.getInt("day_of_week")),
                        toLocalTime(rs.getInt("start_minute")),
                        toLocalTime(rs.getInt("end_minute"))));
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }
        return null;
    }

    public List<Availability> findByTeam(long teamId) {
        JDBCUtil jdbcUtil = new JDBCUtil();
        StringBuffer query = new StringBuffer();
        query.append("SELECT a.availability_id, tm.member_id, a.day_of_week, a.start_minute, a.end_minute ");
        query.append("FROM availability a ");
        query.append("JOIN team_member tm ON tm.team_member_id = a.team_member_id ");
        query.append("WHERE tm.team_id = ? ");
        query.append("ORDER BY tm.member_id, a.day_of_week, a.start_minute");
        jdbcUtil.setSqlAndParameters(query.toString(), new Object[] {teamId});

        try {
            ResultSet rs = jdbcUtil.executeQuery();
            if (rs == null) {          // JDBCUtil 은 질의 실패 시 null 을 반환한다
                return null;
            }
            List<Availability> result = new ArrayList<>();
            while (rs.next()) {
                TeamMember member = new TeamMember((int) teamId, rs.getLong("member_id"), null, null);
                result.add(new Availability(
                        rs.getLong("availability_id"),
                        member,
                        DayOfWeek.of(rs.getInt("day_of_week")),
                        toLocalTime(rs.getInt("start_minute")),
                        toLocalTime(rs.getInt("end_minute"))));
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }
        return null;
    }

    /**
     * 기존 등록분을 모두 지우고 slots 로 교체한다. 삭제와 삽입은 하나의 트랜잭션이다.
     * slots 가 비어 있으면 기존 등록분만 삭제된다.
     * 성공하면 true, 실패하면 rollback 후 false 를 반환한다.
     * 해당 팀의 구성원이 아니면 team_member_id 가 NULL 이 되어 삽입이 실패하고 rollback 된다.
     */
    public boolean replaceByTeamAndUser(long teamId, long userId, List<Availability> slots) {
        JDBCUtil jdbcUtil = new JDBCUtil();

        try {
            StringBuffer delete = new StringBuffer();
            delete.append("DELETE FROM availability WHERE team_member_id IN ");
            delete.append("(SELECT team_member_id FROM team_member WHERE team_id = ? AND member_id = ?)");
            jdbcUtil.setSqlAndParameters(delete.toString(), new Object[] {teamId, userId});
            jdbcUtil.executeUpdate();

            StringBuffer insert = new StringBuffer();
            insert.append("INSERT INTO availability (availability_id, team_member_id, day_of_week, start_minute, end_minute) ");
            insert.append("VALUES (seq_availability.NEXTVAL, ");
            insert.append("(SELECT team_member_id FROM team_member WHERE team_id = ? AND member_id = ?), ?, ?, ?)");

            for (Availability slot : slots) {
                jdbcUtil.setSqlAndParameters(insert.toString(), new Object[] {
                        teamId, userId,
                        slot.getDayOfWeek().getValue(),
                        toMinute(slot.getStartTime()),
                        toMinute(slot.getEndTime())});
                if (jdbcUtil.executeUpdate() != 1) {
                    throw new IllegalStateException("활동 가능 시간 삽입 결과가 올바르지 않습니다.");
                }
            }

            jdbcUtil.commit();
            return true;

        } catch (Exception ex) {
            jdbcUtil.rollback();    // 트랜잭션 rollback 실행
            ex.printStackTrace();
        } finally {
            jdbcUtil.close();
        }
        return false;
    }

    private static int toMinute(LocalTime time) {
        return time.getHour() * 60 + time.getMinute();
    }

    private static LocalTime toLocalTime(int minute) {
        return LocalTime.of(minute / 60, minute % 60);
    }
}