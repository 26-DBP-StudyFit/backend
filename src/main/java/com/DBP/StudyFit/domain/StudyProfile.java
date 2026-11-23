package com.DBP.StudyFit.domain;

import java.util.Date;

/**
 * 회원의 학습 성향 설문 결과 (study_profile 테이블)
 * User : StudyProfile = 1 : 0..1  (userId가 PK이자 FK)
 *
 * 유형 계산에 쓰이는 항목: studyTime, planningStyle, studyMethod
 * 나머지(activityMode, studyIntensity, communicationStyle)는 매칭 등에 활용
 */
public class StudyProfile {
    // 설문 항목은 DB에서 CHAR(1) → 한 글자 코드로 저장
    private int userId;                  // DB 컬럼: member_id
    private String studyTime;            // 학습 시간대   M(아침) / N(밤)
    private String activityMode;         // 대면 여부     예) O(대면) / X(비대면)
    private String planningStyle;        // 계획 성향     P(계획형) / F(유연형)
    private String studyIntensity;       // 학습 강도     예) L / M / H
    private String communicationStyle;   // 소통 빈도     예) L / M / H
    private String studyMethod;          // 학습 방식     T(함께) / A(독립)
    private StudyType studyType;         // 최종 학습 유형 (DB에는 type_code로 저장)
    private Date updatedAt;              // 최초 검사 또는 재검사 일시

    public StudyProfile() { }

    public StudyProfile(int userId, String studyTime, String activityMode, String planningStyle,
                        String studyIntensity, String communicationStyle, String studyMethod) {
        this.userId = userId;
        this.studyTime = studyTime;
        this.activityMode = activityMode;
        this.planningStyle = planningStyle;
        this.studyIntensity = studyIntensity;
        this.communicationStyle = communicationStyle;
        this.studyMethod = studyMethod;
    }

    /** 재검사 시 프로필 값 갱신 + 유형 재계산  */
    public void updateProfile(String studyTime, String activityMode, String planningStyle,
                              String studyIntensity, String communicationStyle, String studyMethod) {
        this.studyTime = studyTime;
        this.activityMode = activityMode;
        this.planningStyle = planningStyle;
        this.studyIntensity = studyIntensity;
        this.communicationStyle = communicationStyle;
        this.studyMethod = studyMethod;

        StudyType newType = new StudyType();
        newType.calculateType(this);   // 미입력 항목이 있으면 예외
        this.studyType = newType;
    }

    /** 설문 미입력 항목이 있는지 검사  */
    public boolean isComplete() {
        return notBlank(studyTime) && notBlank(activityMode) && notBlank(planningStyle)
                && notBlank(studyIntensity) && notBlank(communicationStyle) && notBlank(studyMethod);
    }

    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getStudyTime() { return studyTime; }
    public void setStudyTime(String studyTime) { this.studyTime = studyTime; }

    public String getActivityMode() { return activityMode; }
    public void setActivityMode(String activityMode) { this.activityMode = activityMode; }

    public String getPlanningStyle() { return planningStyle; }
    public void setPlanningStyle(String planningStyle) { this.planningStyle = planningStyle; }

    public String getStudyIntensity() { return studyIntensity; }
    public void setStudyIntensity(String studyIntensity) { this.studyIntensity = studyIntensity; }

    public String getCommunicationStyle() { return communicationStyle; }
    public void setCommunicationStyle(String communicationStyle) { this.communicationStyle = communicationStyle; }

    public String getStudyMethod() { return studyMethod; }
    public void setStudyMethod(String studyMethod) { this.studyMethod = studyMethod; }

    public StudyType getStudyType() { return studyType; }
    public void setStudyType(StudyType studyType) { this.studyType = studyType; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "StudyProfile [userId=" + userId + ", studyTime=" + studyTime
                + ", activityMode=" + activityMode + ", planningStyle=" + planningStyle
                + ", studyIntensity=" + studyIntensity + ", communicationStyle=" + communicationStyle
                + ", studyMethod=" + studyMethod + ", studyType=" + studyType
                + ", updatedAt=" + updatedAt + "]";
    }
}
