package com.DBP.StudyFit.domain;

public enum ScheduleType {
    REGULAR("정기"),
    AD_HOC("수시"),
    SELF_STUDY("자습"),
    ETC("기타");

    private final String label;

    ScheduleType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
