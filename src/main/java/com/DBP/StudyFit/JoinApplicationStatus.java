package com.DBP.StudyFit;

public enum JoinApplicationStatus {
    PENDING("P"),  // 처리 대기
    APPROVED("A"), // 승인
    REJECTED("R"); // 거절

    private final String code;

    JoinApplicationStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static JoinApplicationStatus fromCode(String code) {
        for (JoinApplicationStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("잘못된 신청 상태 코드: " + code);
    }
}

