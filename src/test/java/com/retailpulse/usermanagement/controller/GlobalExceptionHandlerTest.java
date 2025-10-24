package com.retailpulse.usermanagement.controller;

import com.retailpulse.usermanagement.service.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturnBadRequestForBusinessException() {
        BusinessException ex = new BusinessException("CODE", "Test message");

        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(ex);

        // assert
        assertThat(HttpStatus.BAD_REQUEST).isEqualTo(response.getStatusCode());
        assertThat("CODE").isEqualTo(Objects.requireNonNull(response.getBody()).getCode());
        assertThat("Test message").isEqualTo(response.getBody().getMessage());

    }
}
