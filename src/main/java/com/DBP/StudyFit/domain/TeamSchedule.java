package com.DBP.StudyFit.domain;

import java.time.LocalDateTime;

public class TeamSchedule {

    private Long id;
    private Team team;
    private String title;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String location;
    private ScheduleType scheduleType;
    private String recurrenceRule;   // 선택값 (반복 없으면 null)
    private ScheduleStatus status = ScheduleStatus.SCHEDULED;
    private LocalDateTime createdAt; // 등록 시각 (DB 기본값 SYSTIMESTAMP, 등록 전에는 null)


    // MyBatis / JDBC 매핑용 기본 생성자
    public TeamSchedule() {
    }

    public TeamSchedule(Team team, String title, LocalDateTime startAt, LocalDateTime endAt,
                    String location, ScheduleType scheduleType, String recurrenceRule) {
        this.team = team;
        this.title = title;
        this.startAt = startAt;
        this.endAt = endAt;
        this.location = location;
        this.scheduleType = scheduleType;
        this.recurrenceRule = recurrenceRule;
        this.status = ScheduleStatus.SCHEDULED;
    }

    // 시작/종료 시각이 모두 있고 종료가 시작보다 뒤인지 확인
    public boolean isValidPeriod() {
        return startAt != null && endAt != null && endAt.isAfter(startAt);
    }

    // 반복 규칙이 지정된 일정인지 확인
    public boolean isRecurring() {
        return recurrenceRule != null && !recurrenceRule.isBlank();
    }


     // 일정 유형과 반복 규칙이 서로 맞는지 확인
    public boolean isValidRecurrence() {
        if (scheduleType == null) {
            return false;
        }
        switch (scheduleType) { // 정기(REGULAR): 반복 규칙 필수
            case REGULAR:
                return isRecurring();
            case AD_HOC:  // 수시(AD_HOC): 반복 규칙 없어야 함
                return !isRecurring();
            default: // 자습/기타: 반복 규칙 선택
                return true;
        }
    }

     // 현재 시각 기준으로 상태 갱신
     // 시작 전: 예정 / 시작~종료 사이: 진행중 / 종료 이후: 종료
    public void refreshStatus(LocalDateTime now) {
        if (startAt == null || endAt == null || now == null) {
            return;
        }
        if (now.isBefore(startAt)) {
            this.status = ScheduleStatus.SCHEDULED;
        } else if (now.isBefore(endAt)) {
            this.status = ScheduleStatus.IN_PROGRESS;
        } else {
            this.status = ScheduleStatus.ENDED;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public void setRecurrenceRule(String recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
    }

    public ScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Schedule{id=" + id + ", title='" + title + "', startAt=" + startAt
                + ", endAt=" + endAt + ", location='" + location + "', scheduleType=" + scheduleType
                + ", recurrenceRule='" + recurrenceRule + "', status=" + status + "}";
    }
}