package com.DBP.StudyFit.exception;

/** 로그인 사용자가 해당 팀의 구성원이 아닐 때 던진다 (Controller 에서 HTTP 403 으로 처리). */
public class NotTeamMemberException extends RuntimeException {

    public NotTeamMemberException(String message) {
        super(message);
    }
}
