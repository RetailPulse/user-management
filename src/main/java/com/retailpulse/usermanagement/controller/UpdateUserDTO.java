package com.retailpulse.usermanagement.controller;

import java.util.List;

public record UpdateUserDTO(String name, String email, List<String> roles, Boolean isEnabled) {
}
