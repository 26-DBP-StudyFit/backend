package com.DBP.StudyFit.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 한 사용자에게 생성된 팀 추천 결과 묶음을 저장하는 도메인 클래스.
 * recommendation 테이블과 대응한다.
 */
public class Recommendation {
    private int recommendationId;
    private LocalDateTime createdAt;
    private long memberId;
    private List<MatchResult> matchResults;

    public Recommendation() {
        matchResults = new ArrayList<MatchResult>();
    }

    public Recommendation(int recommendationId, LocalDateTime createdAt,
                          long memberId) {
        this.recommendationId = recommendationId;
        this.createdAt = createdAt;
        this.memberId = memberId;
        this.matchResults = new ArrayList<MatchResult>();
    }

    public int getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(int recommendationId) {
        this.recommendationId = recommendationId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public List<MatchResult> getMatchResults() {
        return matchResults;
    }

    public void setMatchResults(List<MatchResult> matchResults) {
        if (matchResults == null) {
            this.matchResults = new ArrayList<MatchResult>();
        } else {
            this.matchResults = matchResults;
        }
    }

    @Override
    public String toString() {
        return "Recommendation{" +
                "recommendationId=" + recommendationId +
                ", createdAt=" + createdAt +
                ", memberId=" + memberId +
                ", matchResults=" + matchResults +
                '}';
    }
}
