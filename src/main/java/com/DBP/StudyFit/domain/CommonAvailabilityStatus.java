package com.DBP.StudyFit.domain;


 // 계산 결과로만 쓰이며 DB에 저장하지 않는다.
public enum CommonAvailabilityStatus {
    AVAILABLE("공통 가능 시간 있음"),     // 전체 팀원의 교집합이 존재
    NO_COMMON_TIME("공통 가능 시간 없음"), // 전원 등록했지만 교집합이 없음
    INCOMPLETE("가능 시간 미등록 팀원 있음"); // 미등록 팀원이 있어 공통 시간을 확정하지 않음

    private final String label;

    CommonAvailabilityStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}