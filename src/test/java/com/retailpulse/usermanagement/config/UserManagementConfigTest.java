package com.retailpulse.usermanagement.config;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserManagementConfigTest {
    /** Tiny stub controllers so requests are mapped (no 404s). */
    @TestConfiguration
    @EnableWebMvc
    static class TestControllers {
        @RestController
        static class ActuatorStub {
            @GetMapping(value = "/actuator/health", produces = MediaType.TEXT_PLAIN_VALUE)
            public String health() { return "OK"; }
        }
        @RestController
        @RequestMapping("/api")
        static class ApiStub {
            @GetMapping("/secure") public String secureGet() { return "secured-get"; }
            @PostMapping("/secure") public String securePost() { return "secured-post"; }
        }
    }

    @Nested
    @SpringBootTest(
            classes = { UserManagementConfig.class, TestControllers.class },
            webEnvironment = SpringBootTest.WebEnvironment.MOCK,
            properties = {
                    "auth.enabled=true",
                    "auth.jwt.key.set.uri=https://example.invalid/jwks",
                    "auth.origin=http://test-origin.example",
                    // keep real Actuator off to avoid conflicts with our stub
                    "management.endpoints.enabled-by-default=false"
            }
    )
    @AutoConfigureMockMvc
    class WhenAuthEnabled {

        @Autowired
        MockMvc mvc;

        @Test
        void actuatorHealth_isPermitted() throws Exception {
            mvc.perform(get("/actuator/health"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                    .andExpect(content().string("OK"));
        }

        @Test
        void cors_preflight_allows_configured_origin() throws Exception {
            mvc.perform(options("/anything")
                            .header("Origin", "http://test-origin.example")
                            .header("Access-Control-Request-Method", "GET"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Origin", "http://test-origin.example"))
                    .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
        }

        @Test
        void api_requires_auth_for_get_and_post() throws Exception {
            mvc.perform(get("/api/secure")).andExpect(status().isUnauthorized());
            mvc.perform(post("/api/secure")).andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @SpringBootTest(
            classes = { UserManagementConfig.class, TestControllers.class },
            webEnvironment = SpringBootTest.WebEnvironment.MOCK,
            properties = {
                    "auth.enabled=false",
                    "auth.jwt.key.set.uri=https://example.invalid/jwks",
                    "auth.origin=http://test-origin.example",
                    "management.endpoints.enabled-by-default=false"
            }
    )
    @AutoConfigureMockMvc
    class WhenAuthDisabled {

        @Autowired
        MockMvc mvc;

        @Test
        void everything_is_permitted() throws Exception {
            mvc.perform(get("/actuator/health"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("OK"));

            mvc.perform(get("/api/secure"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("secured-get"));

            mvc.perform(post("/api/secure"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("secured-post"));
        }

        @Test
        void cors_preflight_allows_configured_origin() throws Exception {
            mvc.perform(options("/api/secure")
                            .header("Origin", "http://test-origin.example")
                            .header("Access-Control-Request-Method", "POST"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Origin", "http://test-origin.example"))
                    .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
        }
    }
}
