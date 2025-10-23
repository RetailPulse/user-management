package com.retailpulse.usermanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailpulse.usermanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testGetAllUsers() throws Exception {
        List<ViewUserDTO> mockUsers = List.of(
                new ViewUserDTO(1L, "alice", "alice@email.com", "Alice", List.of("ADMIN"), true)
        );

        when(userService.getAllUsers()).thenReturn(mockUsers);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].username").value("alice"));
    }

    @Test
    public void testGetUserById() throws Exception {
        ViewUserDTO mockUser = new ViewUserDTO(1L, "alice", "alice@email.com", "Alice", List.of("ADMIN"), true);

        when(userService.getUserById(1L)).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/users/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    public void testGetUserByUsername() throws Exception {
        ViewUserDTO mockUser = new ViewUserDTO(1L, "alice", "alice@email.com", "Alice", List.of("ADMIN"), true);

        when(userService.getUserByUsername("alice")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/users/username/alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    public void testGetUserByName() throws Exception {
        ViewUserDTO mockUser = new ViewUserDTO(1L, "alice", "alice@email.com", "Alice", List.of("ADMIN"), true);

        when(userService.getUserByName("Alice")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/users/search?name=Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    public void testPostUser() throws Exception {
        ViewUserDTO mockUser = new ViewUserDTO(1L, "alice", "alice@email.com", "Alice", List.of("ADMIN"), true);
        CreateUserDTO createUserDTO = new CreateUserDTO("alice", "password1", "alice@email.com", "Alice", List.of("ADMIN"));

        when(userService.createUser(createUserDTO)).thenReturn(mockUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("alice"));

    }

    @Test
    public void testPutUser() throws Exception {
        ViewUserDTO mockUser = new ViewUserDTO(1L, "alice", "alice@email.com", "Alice", List.of("ADMIN"), true);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO("Alice", "alice@email.com", List.of("ADMIN"), true);

        when(userService.updateUser(1L, updateUserDTO)).thenReturn(mockUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testChangePassword() throws Exception {
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("oldPassword", "newPassword");

        mockMvc.perform(patch("/api/users/1/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(changePasswordDTO)))
                .andExpect(status().isOk());
    }

}
