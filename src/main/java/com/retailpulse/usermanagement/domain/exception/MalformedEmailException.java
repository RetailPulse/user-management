package com.retailpulse.usermanagement.domain.exception;

public class MalformedEmailException extends RuntimeException {
    public MalformedEmailException(String message) {
        super(message);
    }
}
