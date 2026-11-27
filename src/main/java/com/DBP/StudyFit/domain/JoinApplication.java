package com.DBP.StudyFit.domain;

import java.time.LocalDateTime;

public class JoinApplication {
    private int applicationId;
    private JoinApplicationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private int teamId;
    private int userId;

    // 생성자
    public JoinApplication(int applicationId, JoinApplicationStatus status,
                           LocalDateTime createdAt, LocalDateTime processedAt,
                           int teamId, int userId) {
        this.applicationId = applicationId;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
        this.teamId = teamId;
        this.userId = userId;
    }

    public JoinApplication() {
    }

    // Getter 및 Setter
    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public JoinApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(JoinApplicationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
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
}
