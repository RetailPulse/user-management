package com.retailpulse.usermanagement.controller;

import java.util.List;

public record CreateUserDTO(String username, String password, String email, String name, List<String> roles) {

}
