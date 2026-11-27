package com.DBP.StudyFit.dto;

/**
 * 활동 가능 시간 폼의 한 줄(요일·시작·종료).
 * 잘못된 입력도 화면에 그대로 되돌려 보여줘야 하므로 모두 String 으로 받고,
 * 검증과 변환은 AvailabilityManager 에서 한다.
 */
public class AvailabilityInput {
    private String dayOfWeek;   // MONDAY ~ SUNDAY
    private String startTime;   // HH:mm
    private String endTime;     // HH:mm

    public AvailabilityInput() {
    }

    public AvailabilityInput(String dayOfWeek, String startTime, String endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /** 세 칸이 모두 비어 있으면 빈 줄로 보고 저장 대상에서 제외한다. */
    public boolean isBlank() {
        return isEmpty(dayOfWeek) && isEmpty(startTime) && isEmpty(endTime);
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
