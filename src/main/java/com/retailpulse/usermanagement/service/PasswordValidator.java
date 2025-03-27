package com.retailpulse.usermanagement.service;

import com.retailpulse.usermanagement.domain.exception.MalformedPasswordException;

import java.util.regex.Pattern;

public class PasswordValidator {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$");

    public static boolean isValid(String password) {

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new MalformedPasswordException("Password must contain at least 8 characters, one letter and one number");
        }

        return true;
    }
}
