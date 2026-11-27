package com.DBP.StudyFit.manager;

import com.DBP.StudyFit.dao.ScheduleDAO;
import com.DBP.StudyFit.dao.TeamMemberDAO;
import com.DBP.StudyFit.domain.Schedule;
import com.DBP.StudyFit.domain.Team;
import com.DBP.StudyFit.domain.TeamMember;
import com.DBP.StudyFit.dto.ScheduleDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleManager {

    // TEAM_SCHEDULE 컬럼 길이
    private static final int MAX_TITLE = 200;
    private static final int MAX_LOCATION = 200;
    private static final int MAX_RECURRENCE_RULE = 500;

    private final ScheduleDAO scheduleDAO;
    private final TeamMemberDAO teamMemberDAO;

    public ScheduleManager(ScheduleDAO scheduleDAO, TeamMemberDAO teamMemberDAO) {
        this.scheduleDAO = scheduleDAO;
        this.teamMemberDAO = teamMemberDAO;
    }

    public List<Schedule> getSchedules(int teamId, long memberId) {
        requireMember(teamId, memberId);

        LocalDateTime now = LocalDateTime.now();
        List<Schedule> schedules = new ArrayList<>();
        for (ScheduleDTO dto : scheduleDAO.findByTeamId(Long.valueOf(teamId))) {
            Schedule schedule = toDomain(dto);
            schedule.refreshStatus(now);
            schedules.add(schedule);
        }
        return schedules;
    }

    // 해당 팀의 팀장인지 확인 (화면에서 "일정 등록" 버튼 노출 여부 판단용)
    public boolean isLeader(int teamId, long memberId) {
        return teamMemberDAO.findByTeamIdAndMemberId(teamId, memberId)
                .map(TeamMember::isLeader)
                .orElse(false);
    }

    public Long createSchedule(Schedule schedule, long memberId) {
        if (schedule == null || schedule.getTeam() == null) {
            throw new IllegalArgumentException("팀 정보가 없습니다.");
        }

        TeamMember member = requireMember(schedule.getTeam().getTeamId(), memberId);
        if (!member.isLeader()) {
            throw new SecurityException("팀장만 일정을 등록할 수 있습니다.");
        }

        normalize(schedule);
        validate(schedule);

        ScheduleDTO dto = toDTO(schedule);
        scheduleDAO.insert(dto);
        return dto.getScheduleId();
    }

    private TeamMember requireMember(int teamId, long memberId) {
        // 주의: TeamMemberDAO의 이 메서드는 DB 오류가 나도 Optional.empty()를 반환한다.
        return teamMemberDAO.findByTeamIdAndMemberId(teamId, memberId)
                .orElseThrow(() -> new SecurityException("해당 팀의 팀원이 아닙니다."));
    }

    // 앞뒤 공백 제거, 빈 문자열은 null로 통일
    private void normalize(Schedule schedule) {
        schedule.setTitle(blankToNull(schedule.getTitle()));
        schedule.setLocation(blankToNull(schedule.getLocation()));
        schedule.setRecurrenceRule(blankToNull(schedule.getRecurrenceRule()));
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void validate(Schedule schedule) {
        if (schedule.getTitle() == null) {
            throw new IllegalArgumentException("일정 제목을 입력해 주세요.");
        }
        if (schedule.getTitle().length() > MAX_TITLE) {
            throw new IllegalArgumentException("일정 제목은 " + MAX_TITLE + "자 이내로 입력해 주세요.");
        }
        if (schedule.getScheduleType() == null) {
            throw new IllegalArgumentException("일정 유형을 선택해 주세요.");
        }
        if (schedule.getStartAt() == null || schedule.getEndAt() == null) {
            throw new IllegalArgumentException("시작 시각과 종료 시각을 입력해 주세요.");
        }
        if (!schedule.isValidPeriod()) {
            throw new IllegalArgumentException("종료 시각은 시작 시각보다 늦어야 합니다.");
        }
        if (schedule.getLocation() != null && schedule.getLocation().length() > MAX_LOCATION) {
            throw new IllegalArgumentException("장소는 " + MAX_LOCATION + "자 이내로 입력해 주세요.");
        }
        if (schedule.getRecurrenceRule() != null
                && schedule.getRecurrenceRule().length() > MAX_RECURRENCE_RULE) {
            throw new IllegalArgumentException("반복 규칙은 " + MAX_RECURRENCE_RULE + "자 이내로 입력해 주세요.");
        }
        if (!schedule.isValidRecurrence()) {
            switch (schedule.getScheduleType()) {
                case REGULAR:
                    throw new IllegalArgumentException("정기 일정은 반복 규칙을 입력해야 합니다.");
                case AD_HOC:
                    throw new IllegalArgumentException("수시 일정에는 반복 규칙을 입력할 수 없습니다.");
                default:
                    throw new IllegalArgumentException("일정 유형과 반복 규칙이 맞지 않습니다.");
            }
        }
    }

    // DTO → 도메인. Team은 조회 비용을 줄이기 위해 teamId만 채운다.
    private Schedule toDomain(ScheduleDTO dto) {
        Team team = new Team();
        team.setTeamId(dto.getTeamId().intValue());

        Schedule schedule = new Schedule(team, dto.getTitle(), dto.getStartAt(), dto.getEndAt(),
                dto.getLocation(), dto.getScheduleType(), dto.getRecurrenceRule());
        schedule.setId(dto.getScheduleId());
        schedule.setCreatedAt(dto.getCreatedAt());
        return schedule;
    }

    // 도메인 → DTO (등록용). scheduleId는 insert 후 채워지고, createdAt은 DB 기본값을 쓴다.
    private ScheduleDTO toDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setTeamId(Long.valueOf(schedule.getTeam().getTeamId()));
        dto.setTitle(schedule.getTitle());
        dto.setStartAt(schedule.getStartAt());
        dto.setEndAt(schedule.getEndAt());
        dto.setLocation(schedule.getLocation());
        dto.setScheduleType(schedule.getScheduleType());
        dto.setRecurrenceRule(schedule.getRecurrenceRule());
        return dto;
    }
}