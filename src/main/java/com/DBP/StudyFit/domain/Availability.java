package com.DBP.StudyFit.domain;

import com.DBP.StudyFit.domain.TeamMember;

import java.time.DayOfWeek;
import java.time.LocalTime;

/** 도메인 클래스 설계: Availability (id, teamMember, dayOfWeek, startTime, endTime, changeTime()) */
public class Availability {

    private Long id;
    private TeamMember teamMember;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public Availability() {
    }

    public Availability(Long id, TeamMember teamMember, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.teamMember = teamMember;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /** 요일·시간 변경. 시작은 종료보다 빨라야 한다. */
    public void changeTime(DayOfWeek day, LocalTime start, LocalTime end) {
        if (day == null || start == null || end == null) {
            throw new IllegalArgumentException("요일과 시간은 필수입니다.");
        }
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 빨라야 합니다.");
        }
        this.dayOfWeek = day;
        this.startTime = start;
        this.endTime = end;
    }

    public Long getId() {
        return id;
    }

    public TeamMember getTeamMember() {
        return teamMember;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
