package com.DBP.StudyFit;

import java.time.LocalDateTime;

public class JoinApplication {
    private int application_id;
    private JoinApplicationStatus status;
    private LocalDateTime created_at;
    private LocalDateTime processed_at;
    private int team_id;
    private int user_id;

    // 생성자
    public JoinApplication(int application_id, JoinApplicationStatus status,
                           LocalDateTime created_at, LocalDateTime processed_at,
                           int team_id, int user_id) {
        this.application_id = application_id;
        this.status = status;
        this.created_at = created_at;
        this.processed_at = processed_at;
        this.team_id = team_id;
        this.user_id = user_id;
    }

    // Getter 및 Setter 정의
    public int getApplication_id() {
        return application_id;
    }

    public void setApplication_id(int application_id) {
        this.application_id = application_id;
    }

    public JoinApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(JoinApplicationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getProcessed_at() {
        return processed_at;
    }

    public void setProcessed_at(LocalDateTime processed_at) {
        this.processed_at = processed_at;
    }

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
