package com.retailpulse.usermanagement.controller;

import java.util.List;

public record ViewUserDTO(Long id, String username, String email, String name, List<String> roles, Boolean enabled) {
}
