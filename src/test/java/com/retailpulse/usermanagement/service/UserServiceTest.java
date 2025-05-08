package com.retailpulse.usermanagement.service;

import com.retailpulse.usermanagement.controller.CreateUserDTO;
import com.retailpulse.usermanagement.controller.UpdateUserDTO;
import com.retailpulse.usermanagement.controller.ViewUserDTO;
import com.retailpulse.usermanagement.domain.Authorities;
import com.retailpulse.usermanagement.domain.User;
import com.retailpulse.usermanagement.domain.exception.InvalidPasswordException;
import com.retailpulse.usermanagement.infrastructure.persistence.UserEntity;
import com.retailpulse.usermanagement.infrastructure.persistence.UserMapper;
import com.retailpulse.usermanagement.repository.UserRepository;
import com.retailpulse.usermanagement.service.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetAllUsers() {
        // Arrange
        UserEntity userEntity1 = new UserEntity("john", "password", "John Doe", "johndoe@mail.com", true);
        UserEntity userEntity2 = new UserEntity("jane", "password", "Jane", "jane@mail.com", true);

        when(userRepository.findAll()).thenReturn(List.of(userEntity1, userEntity2));

        // Act
        List<ViewUserDTO> result = userService.getAllUsers();

        // Assert
        assertThat(result.size() == 2);
        assertThat("john").isEqualTo(result.get(0).username());
        assertThat("jane").isEqualTo(result.get(1).username());
    }

    @Test
    void testGetUserById_UserExists() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        UserEntity userEntity1 = new UserEntity("john", "password", "John Doe", "johndoe@mail.com", true);
        Field idField = UserEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(userEntity1, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity1));

        // Act
        Optional<ViewUserDTO> result = userService.getUserById(1L);

        // Assert
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().username()).isEqualTo("john");
        assertThat(result.get().name()).isEqualTo("John Doe");
    }

    @Test
    void testGetUserByUsername_UserExists() {
        // Arrange
        UserEntity userEntity1 = new UserEntity("john", "password", "John Doe", "johndoe@mail.com", true);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(userEntity1));

        // Act
        Optional<ViewUserDTO> result = userService.getUserByUsername("john");

        // Assert
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().username()).isEqualTo("john");
        assertThat(result.get().name()).isEqualTo("John Doe");
    }

    @Test
    void testGetUserByName_UserExists() {
        // Arrange
        UserEntity userEntity1 = new UserEntity("john", "password", "John Doe", "johndoe@mail.com", true);

        when(userRepository.findByNameContaining("John")).thenReturn(Optional.of(userEntity1));

        // Act
        Optional<ViewUserDTO> result = userService.getUserByName("John");

        // Assert
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().username()).isEqualTo("john");
        assertThat(result.get().name()).isEqualTo("John Doe");
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        CreateUserDTO dto = new CreateUserDTO("john", "StrongPass1!", "john@example.com", "John Doe", List.of("ADMIN"));

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(passwordEncoder.hashPassword("StrongPass1!")).thenReturn("hashedPwd");

        User user = new User.Builder("john")
                .password("hashedPwd")
                .name("John Doe")
                .email("john@example.com")
                .authorities(Set.of(Authorities.ADMIN))
                .build();

        UserEntity userEntity = UserMapper.toEntity(user);

        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // Act
        ViewUserDTO result = userService.createUser(dto);

        // Assert
        assertThat(result.username()).isEqualTo("john");
        assertThat(result.name()).isEqualTo("John Doe");
    }

    @Test
    void testCreateUser_UsernameAlreadyExists_ThrowsException() {
        // Arrange
        CreateUserDTO dto = new CreateUserDTO("john", "StrongPass1!", "john@example.com", "John Doe", List.of("ADMIN"));
        when(userRepository.existsByUsername("john")).thenReturn(true);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> userService.createUser(dto));
        assertThat(ex.getMessage()).isEqualTo("Username already exist. Failed to create user.");

    }

    @Test
    void testCreateUser_InvalidPassword_ThrowsException() {
        // Arrange
        CreateUserDTO dto = new CreateUserDTO("john", "password", "john@example.com", "John Doe", List.of("ADMIN"));
        when(userRepository.existsByUsername("john")).thenReturn(false);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> userService.createUser(dto));
        assertThat(ex.getErrorCode()).isEqualTo("INVALID_FORMAT");
    }

    @Test
    void testCreateUser_InvalidEmail_ThrowsException() {
        // Arrange
        CreateUserDTO dto = new CreateUserDTO("john", "StrongPass1!", "johnexample.com", "John Doe", List.of("ADMIN"));
        when(userRepository.existsByUsername("john")).thenReturn(false);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> userService.createUser(dto));
        assertThat(ex.getErrorCode()).isEqualTo("INVALID_FORMAT");
    }

    @Test
    void testUpdateUser_Success() {
        // Arrange
        Long id = 1L;
        UpdateUserDTO dto = new UpdateUserDTO("Updated Name", "john@example.com", List.of("ADMIN"), true);

        User user = new User.Builder("john")
                .password("hashedPwd")
                .name("Updated Name")
                .email("john@example.com")
                .authorities(Set.of(Authorities.ADMIN))
                .build();

        UserEntity userEntity = UserMapper.toEntity(user);

        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // Act
        ViewUserDTO result = userService.updateUser(id, dto);

        // Assert
        assertThat(result.username()).isEqualTo("john");
        assertThat(result.name()).isEqualTo("Updated Name");
    }

    @Test
    void testUpdateUser_UserNotFound_ThrowsException() {
        // Arrange
        Long id = 1L;
        UpdateUserDTO dto = new UpdateUserDTO("Updated Name", "john@example.com", List.of("ADMIN"), true);

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> userService.updateUser(id, dto));
        assertThat(ex.getErrorCode()).isEqualTo("USER_NOT_FOUND");
    }

    @Test
    void testUpdateUser_InvalidEmail_ThrowsException() {
        // Arrange
        Long id = 1L;
        UpdateUserDTO dto = new UpdateUserDTO("Updated Name", "johnexample.com", List.of("ADMIN"), true);

        User user = new User.Builder("john")
                .password("hashedPwd")
                .name("Updated Name")
                .email("john@example.com")
                .authorities(Set.of(Authorities.ADMIN))
                .build();

        UserEntity userEntity = UserMapper.toEntity(user);

        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> userService.updateUser(id, dto));
        assertThat(ex.getErrorCode()).isEqualTo("INVALID_FORMAT");
    }

    @Test
    void testDeleteUser_Success() {
        Long id = 1L;

        when(userRepository.existsById(id)).thenReturn(true);

        // Act
        userService.deleteUser(id);

        // Assert
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteUser_UserNotFound_ThrowsException() {
        Long id = 1L;

        when(userRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> userService.deleteUser(id));
        assertThat(ex.getErrorCode()).isEqualTo("USER_NOT_FOUND");
    }

    @Test
    void testChangePassword_Success() {
        // Arrange
        Long id = 1L;
        String oldPassword = "OldPass1!";
        String newPassword = "NewStrongPass1!";

        UserEntity userEntity = new UserEntity("john", oldPassword, "John Doe", "johndoe@example.com", true);
        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

        when(passwordEncoder.matches(oldPassword, userEntity.getPassword())).thenReturn(true);
        when(passwordEncoder.hashPassword(newPassword)).thenReturn("hashedNewPwd");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // Act
        userService.changePassword(id, oldPassword, newPassword);

        // Assert
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testChangePassword_UserNotFound_ThrowsException() {
        // Arrange
        Long id = 1L;
        String oldPassword = "OldPass1!";
        String newPassword = "NewStrongPass1!";

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> userService.changePassword(id, oldPassword, newPassword));
        assertThat(ex.getErrorCode()).isEqualTo("USER_NOT_FOUND");
    }

    @Test
    void testChangePassword_InvalidOldPassword_ThrowsException() {
        // Arrange
        Long id = 1L;
        String oldPassword = "OldPass1!";
        String newPassword = "NewStrongPass1!";

        UserEntity userEntity = new UserEntity("john", newPassword, "John Doe", "johndoe@example.com", true);
        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

        when(passwordEncoder.matches(oldPassword, userEntity.getPassword())).thenThrow(InvalidPasswordException.class);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> userService.changePassword(id, oldPassword, newPassword));
        assertThat(ex.getErrorCode()).isEqualTo("INVALID_OLD_PASSWORD");
    }
}
