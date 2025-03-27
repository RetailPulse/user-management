package com.retailpulse.usermanagement.controller;

import com.retailpulse.usermanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<ViewUserDTO> getAllUsers() {
        logger.info("Fetching all users");
        return userService.getAllUsers();
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ViewUserDTO> getUserById(@PathVariable Long id) {
        logger.info("Fetching user with id: " + id);
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ViewUserDTO> getUserByUsername(@PathVariable String username) {
        logger.info("Fetching user with username: " + username);
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<ViewUserDTO> getUserByName(@RequestParam String name) {
        logger.info("Fetching user with name: " + name);
        return userService.getUserByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ViewUserDTO> createUser(@RequestBody CreateUserDTO createUserDTO) {
        logger.info("Received request to create user: " + createUserDTO);
        ViewUserDTO viewUserDTO = userService.createUser(createUserDTO);

        return ResponseEntity.created(URI.create("/api/users/" + viewUserDTO.id())).body(viewUserDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViewUserDTO> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO updateUserDTO) {
        logger.info("Received request to update user with id: " + id);
        ViewUserDTO viewUserDTO = userService.updateUser(id, updateUserDTO);

        return ResponseEntity.ok(viewUserDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Received request to delete user with id: " + id);
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordDTO request) {

        userService.changePassword(id, request.oldPassword(), request.newPassword());
        return ResponseEntity.ok("Password changed successfully");
    }

}
