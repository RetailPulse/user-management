package com.retailpulse.usermanagement.domain;

import com.retailpulse.usermanagement.domain.exception.MalformedEmailException;
import com.retailpulse.usermanagement.infrastructure.persistence.UserEntity;
import com.retailpulse.usermanagement.infrastructure.persistence.UserMapper;
import com.retailpulse.usermanagement.repository.UserRepository;
import com.retailpulse.usermanagement.service.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UpdateUserTest {

    @Mock
    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setup() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Set<Authorities> authorities = new HashSet<>();
        authorities.add(Authorities.ADMIN);

        User user = new User.Builder("username")
                .password(bCryptPasswordEncoder.encode("password"))
                .name("name")
                .email("email@email")
                .authorities(authorities).build();

        UserEntity userEntity = UserMapper.toEntity(user);

        when(userRepository.getById(any())).thenReturn(userEntity);
    }

    @Test
    public void updatePassword() {
        // Given
        UserEntity savedUserEntity = userRepository.getById(1L);
        User savedUser = UserMapper.toDomain(savedUserEntity);

        // Given
        String newPassword = "newP@ssw0rd";
        if (!PasswordValidator.isValid(newPassword)) {
            throw new IllegalArgumentException("Password must be at least 8 characters long, " +
                    "contain at least one uppercase letter, " +
                    "one lowercase letter, " +
                    "one number and one special character");
        }
        savedUser.changePassword(bCryptPasswordEncoder.encode(newPassword));

        assertThat(bCryptPasswordEncoder.matches(newPassword, savedUser.getPassword())).isTrue();

        UserEntity updatedUserEntity = UserMapper.toEntity(savedUser);
        when(userRepository.save(updatedUserEntity)).thenReturn(updatedUserEntity);
        UserEntity userEntity = userRepository.save(updatedUserEntity);

        assertThat(bCryptPasswordEncoder.matches(newPassword, userEntity.getPassword())).isTrue();
    }

    @Test
    public void updateRoles() {
        // Given
        UserEntity savedUserEntity = userRepository.getById(1L);
        User savedUser = UserMapper.toDomain(savedUserEntity);

        // Given
        Set<Authorities> newAuthorities = new HashSet<>();
        newAuthorities.add(Authorities.CASHIER);

        savedUser.updateRoles(newAuthorities);

        assertThat(savedUser.getAuthorities()).isEqualTo(newAuthorities);

        UserEntity updatedUserEntity = UserMapper.toEntity(savedUser);
        when(userRepository.save(updatedUserEntity)).thenReturn(updatedUserEntity);
        UserEntity userEntity = userRepository.save(updatedUserEntity);

        assertThat(userEntity.getAuthorities().stream().map(
                role -> Authorities.valueOf(role.getAuthority())
        ).collect(Collectors.toSet())).isEqualTo(newAuthorities);
    }

    @Test
    public void updateName() {
        // Given
        UserEntity savedUserEntity = userRepository.getById(1L);
        User savedUser = UserMapper.toDomain(savedUserEntity);

        // Given
        String newName = "newName";

        savedUser.updateName(newName);

        assertThat(savedUser.getName()).isEqualTo(newName);

        UserEntity updatedUserEntity = UserMapper.toEntity(savedUser);
        when(userRepository.save(updatedUserEntity)).thenReturn(updatedUserEntity);
        UserEntity userEntity = userRepository.save(updatedUserEntity);

        assertThat(userEntity.getName()).isEqualTo(newName);
    }

    @Test
    public void updateWrongFormatEmail() {
        // Given
        UserEntity savedUserEntity = userRepository.getById(1L);
        User savedUser = UserMapper.toDomain(savedUserEntity);

        // Given
        String newEmail = "newEmail";

        assertThrows(MalformedEmailException.class,
                () -> {
                    savedUser.updateEmail(newEmail);
                }, "Email must be in the correct format");
    }

    @Test
    public void updateEmail() {
        // Given
        UserEntity savedUserEntity = userRepository.getById(1L);
        User savedUser = UserMapper.toDomain(savedUserEntity);

        // Given
        String newEmail = "test@test.com";
        savedUser.updateEmail(newEmail);

        assertThat(savedUser.getEmail()).isEqualTo(newEmail);

        UserEntity updatedUserEntity = UserMapper.toEntity(savedUser);
        when(userRepository.save(updatedUserEntity)).thenReturn(updatedUserEntity);
        UserEntity userEntity = userRepository.save(updatedUserEntity);

        assertThat(userEntity.getEmail()).isEqualTo(newEmail);
    }

    @Test
    public void updateEnabled() {
        // Given
        UserEntity savedUserEntity = userRepository.getById(1L);
        User savedUser = UserMapper.toDomain(savedUserEntity);

        // Given
        Boolean newEnabled = false;
        savedUser.updateEnabled(newEnabled);

        assertThat(savedUser.getEnabled()).isEqualTo(newEnabled);

        UserEntity updatedUserEntity = UserMapper.toEntity(savedUser);
        when(userRepository.save(updatedUserEntity)).thenReturn(updatedUserEntity);
        UserEntity userEntity = userRepository.save(updatedUserEntity);

        assertThat(userEntity.isEnabled()).isEqualTo(newEnabled);
    }

}
