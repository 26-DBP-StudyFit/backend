package com.DBP.StudyFit.exception;

public class PasswordMismatchException extends Exception {
    private static final long serialVersionUID = 1L;

    public PasswordMismatchException() {
        super();
    }

    public PasswordMismatchException(String message) {
        super(message);
    }
}