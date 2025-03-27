package com.retailpulse.usermanagement.service;

import com.retailpulse.usermanagement.domain.exception.InvalidPasswordException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncoder {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String hashedPassword) {

        if (!passwordEncoder.matches(rawPassword, hashedPassword)) {
            throw new InvalidPasswordException("Invalid password");
        }

        return true;
    }
}
