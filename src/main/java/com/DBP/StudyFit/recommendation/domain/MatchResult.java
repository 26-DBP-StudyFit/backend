package com.DBP.StudyFit.recommendation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 한 추천 결과에 포함된 팀별 매칭 결과를 저장하는 도메인 클래스.
 * match_result 테이블과 대응한다.
 */
public class MatchResult {
    private int recommendationId;
    private int teamId;
    private BigDecimal totalScore;
    private int matchRank;
    private List<CriterionScore> criterionScores;

    public MatchResult() {
        criterionScores = new ArrayList<CriterionScore>();
    }

    public MatchResult(int recommendationId, int teamId,
                       BigDecimal totalScore, int matchRank) {
        this.recommendationId = recommendationId;
        this.teamId = teamId;
        this.totalScore = totalScore;
        this.matchRank = matchRank;
        this.criterionScores = new ArrayList<CriterionScore>();
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

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public int getMatchRank() {
        return matchRank;
    }

    public void setMatchRank(int matchRank) {
        this.matchRank = matchRank;
    }

    public List<CriterionScore> getCriterionScores() {
        return criterionScores;
    }

    public void setCriterionScores(List<CriterionScore> criterionScores) {
        if (criterionScores == null) {
            this.criterionScores = new ArrayList<CriterionScore>();
        } else {
            this.criterionScores = criterionScores;
        }
    }

    @Override
    public String toString() {
        return "MatchResult{" +
                "recommendationId=" + recommendationId +
                ", teamId=" + teamId +
                ", totalScore=" + totalScore +
                ", matchRank=" + matchRank +
                ", criterionScores=" + criterionScores +
                '}';
    }
}
