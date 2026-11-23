package com.DBP.StudyFit;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Team {
    private int teamId;
    private String title;
    private RegionCode regionCode;
    private Study_Category studyCategory;
    private int recruitCount;
    private DayOfWeek studyDay;
    private LocalTime preferredStartTime;
    private LocalTime preferredEndTime;
    private LocalDateTime createdAt;

    // 기본 생성자
    public Team() {
    }

    // 전체 필드를 받는 생성자
    public Team(int teamId, String title, RegionCode regionCode,
                Study_Category studyCategory, int recruitCount,
                DayOfWeek studyDay, LocalTime preferredStartTime,
                LocalTime preferredEndTime, LocalDateTime createdAt) {
        this.teamId = teamId;
        this.title = title;
        this.regionCode = regionCode;
        this.studyCategory = studyCategory;
        this.recruitCount = recruitCount;
        this.studyDay = studyDay;
        this.preferredStartTime = preferredStartTime;
        this.preferredEndTime = preferredEndTime;
        this.createdAt = createdAt;
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

    public RegionCode getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(RegionCode regionCode) {
        this.regionCode = regionCode;
    }

    public Study_Category getStudyCategory() {
        return studyCategory;
    }

    public void setStudyCategory(Study_Category studyCategory) {
        this.studyCategory = studyCategory;
    }

    public int getRecruitCount() {
        return recruitCount;
    }

    public void setRecruitCount(int recruitCount) {
        this.recruitCount = recruitCount;
    }

    public DayOfWeek getStudyDay() {
        return studyDay;
    }

    public void setStudyDay(DayOfWeek studyDay) {
        this.studyDay = studyDay;
    }

    public LocalTime getPreferredStartTime() {
        return preferredStartTime;
    }

    public void setPreferredStartTime(LocalTime preferredStartTime) {
        this.preferredStartTime = preferredStartTime;
    }

    public LocalTime getPreferredEndTime() {
        return preferredEndTime;
    }

    public void setPreferredEndTime(LocalTime preferredEndTime) {
        this.preferredEndTime = preferredEndTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
