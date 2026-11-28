package com.DBP.StudyFit.dto;

import com.DBP.StudyFit.domain.ScheduleType;

import java.time.LocalDateTime;

// 상태(예정/진행중/종료)는 저장하지 않고 Schedule.refreshStatus()로 계산한다.
public class ScheduleDTO {

    private Integer teamScheduleId;
    private int teamId;
    private String title;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String location;
    private ScheduleType scheduleType;
    private String recurrenceRule;
    private LocalDateTime createdAt;   // DB 기본값(SYSTIMESTAMP)으로 채워짐, 조회 시에만 사용

    public ScheduleDTO() {
    }
    public Integer getTeamScheduleId() {
        return teamScheduleId;
    }

    public void setTeamScheduleId(Integer teamScheduleId) {
        this.teamScheduleId = teamScheduleId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}