package com.DBP.StudyFit.domain;

public enum ScheduleStatus {
    SCHEDULED("예정"),
    IN_PROGRESS("진행중"),
    ENDED("종료");

    private final String label;

    ScheduleStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
