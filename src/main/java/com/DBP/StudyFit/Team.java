package com.DBP.StudyFit;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Team {
    private int team_id;
    private String title;
    private RegionCode region_code;
    private Study_Category study_category;
    private int recruit_count;
    private DayOfWeek studyDays;
    private LocalTime preferred_start_time;
    private LocalTime preferred_end_time;
    private LocalDateTime created_at;

    // 생성자


    public Team(int team_id, String title, RegionCode region_code,
                Study_Category study_category, int recruit_count,
                DayOfWeek studyDays,
                LocalTime preferred_start_time, LocalTime preferred_end_time,
                LocalDateTime created_at) {
        this.team_id = team_id;
        this.title = title;
        this.region_code = region_code;
        this.study_category = study_category;
        this.recruit_count = recruit_count;
        this.studyDays = studyDays;
        this.preferred_start_time = preferred_start_time;
        this.preferred_end_time = preferred_end_time;
        this.created_at = created_at;
    }

    public Team() {

    }

    // 기본 생성자


    // Getter 및 Setter
    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RegionCode getRegion_code() {
        return region_code;
    }

    public void setRegion_code(RegionCode regiond_code) {
        this.region_code = regiond_code;
    }

    public Study_Category getStudy_category() {
        return study_category;
    }

    public void setStudy_category(Study_Category study_category) {
        this.study_category = study_category;
    }

    public int getRecruit_count() {
        return recruit_count;
    }

    public void setRecruit_count(int recruit_count) {
        this.recruit_count = recruit_count;
    }

    public DayOfWeek getStudyDays() {
        return studyDays;
    }

    public void setStudyDays(DayOfWeek studyDays) {
        this.studyDays = studyDays;
    }

    public LocalTime getPreferred_start_time() {
        return preferred_start_time;
    }

    public void setPreferred_start_time(LocalTime preferred_start_time) {
        this.preferred_start_time = preferred_start_time;
    }

    public LocalTime getPreferred_end_time() {
        return preferred_end_time;
    }

    public void setPreferred_end_time(LocalTime preferred_end_time) {
        this.preferred_end_time = preferred_end_time;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}
