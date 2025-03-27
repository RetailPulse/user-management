package com.retailpulse.usermanagement.domain.exception;

public class MalformedPasswordException extends RuntimeException {
    public MalformedPasswordException(String message) {
        super(message);
    }
}
