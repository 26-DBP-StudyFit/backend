package com.DBP.StudyFit.domain;

/**
 * team_member.role 컬럼 값.
 * DB에는 문자열(name())로 저장한다. (JPA @Enumerated 사용 불가 → JDBC/MyBatis에서 직접 변환)
 */
public enum TeamMemberRole {
    LEADER,   // 팀장
    MEMBER;   // 팀원

    /** DB 문자열 → enum 변환 (JDBC ResultSet에서 읽을 때 사용) */
    public static TeamMemberRole from(String value) {
        return value == null ? null : TeamMemberRole.valueOf(value.trim().toUpperCase());
    }
}