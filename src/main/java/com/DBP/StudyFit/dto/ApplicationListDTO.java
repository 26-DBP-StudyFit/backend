package com.DBP.StudyFit.dto;

import java.time.LocalDateTime;

public class ApplicationListDTO {
    private int applicationId;
    private int teamId;
    private int userId;
    private String memberName;     // 신청자 이름
    private String studyTypeName;  // 학습 유형 이름 (검사 안 했으면 null)
    private LocalDateTime createdAt;

    public int getApplicationId() {
        return applicationId;
    }
    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }
    public int getTeamId() {
        return teamId;
    }
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getMemberName() {
        return memberName;
    }
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
    public String getStudyTypeName() {
        return studyTypeName;
    }
    public void setStudyTypeName(String studyTypeName) {
        this.studyTypeName = studyTypeName;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
