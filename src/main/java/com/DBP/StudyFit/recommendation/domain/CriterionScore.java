package com.DBP.StudyFit.recommendation.domain;

import java.math.BigDecimal;

/**
 * 매칭 결과의 항목별 점수를 저장하는 도메인 클래스.
 * criterion_score 테이블과 대응한다.
 */
public class CriterionScore {
    private String criterionCode;
    private int recommendationId;
    private int teamId;
    private BigDecimal score;

    public CriterionScore() {
    }

    public CriterionScore(String criterionCode, int recommendationId,
                          int teamId, BigDecimal score) {
        this.criterionCode = criterionCode;
        this.recommendationId = recommendationId;
        this.teamId = teamId;
        this.score = score;
    }

    public String getCriterionCode() {
        return criterionCode;
    }

    public void setCriterionCode(String criterionCode) {
        this.criterionCode = criterionCode;
    }

    public int getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(int recommendationId) {
        this.recommendationId = recommendationId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "CriterionScore{" +
                "criterionCode='" + criterionCode + '\'' +
                ", recommendationId=" + recommendationId +
                ", teamId=" + teamId +
                ", score=" + score +
                '}';
    }
}
