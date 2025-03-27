package com.retailpulse.usermanagement.domain;

import com.retailpulse.usermanagement.domain.exception.InvalidPasswordException;
import com.retailpulse.usermanagement.domain.exception.MalformedPasswordException;
import com.retailpulse.usermanagement.infrastructure.persistence.UserEntity;
import com.retailpulse.usermanagement.infrastructure.persistence.UserMapper;
import com.retailpulse.usermanagement.repository.UserRepository;
import com.retailpulse.usermanagement.service.PasswordEncoder;
import com.retailpulse.usermanagement.service.PasswordValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChangePasswordTest {

    @Mock
    private UserRepository userRepository;

    @Test
    public void changePassword() {
        // Given
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Set<Authorities> authorities = new HashSet<>();
        authorities.add(Authorities.ADMIN);

        User user = new User.Builder("username")
                .password(bCryptPasswordEncoder.encode("password"))
                .name("name")
                .email("email@email")
                .authorities(authorities).build();

        UserEntity userEntity = UserMapper.toEntity(user);

        // When
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        // Then
        Optional<UserEntity> userEntityOptional = userRepository.findById(1L);
        User savedUser = UserMapper.toDomain(userEntityOptional.get());

        String oldPassword = "password";
        String newPassword = "newP@ssw0rd";

        assertThat(bCryptPasswordEncoder.matches(oldPassword, savedUser.getPassword())).isTrue();

        assertThat(PasswordValidator.isValid(newPassword)).isTrue();

        savedUser.changePassword(bCryptPasswordEncoder.encode(newPassword));

        UserEntity updatedUserEntity = UserMapper.toEntity(savedUser);

        // When
        when(userRepository.save(updatedUserEntity)).thenReturn(updatedUserEntity);

        // Then
        UserEntity savedUserEntity = userRepository.save(updatedUserEntity);
        assertThat(bCryptPasswordEncoder.matches(newPassword, savedUserEntity.getPassword())).isTrue();

    }

    @Test
    public void incorrectFormatPassword() {
        // Given
        PasswordEncoder passwordEncoder = new PasswordEncoder();
        Set<Authorities> authorities = new HashSet<>();
        authorities.add(Authorities.ADMIN);

        User user = new User.Builder("username")
                .password(passwordEncoder.hashPassword("password"))
                .name("name")
                .email("email@email")
                .authorities(authorities).build();

        UserEntity userEntity = UserMapper.toEntity(user);

        // When
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        // Then
        Optional<UserEntity> userEntityOptional = userRepository.findById(1L);
        User savedUser = UserMapper.toDomain(userEntityOptional.get());

        String newPassword = "newpassword";

        assertThrows(MalformedPasswordException.class,
                () -> PasswordValidator.isValid(newPassword), "Incorrect format password");


    }

    @Test
    public void wrongCurrentPassword() {
        // Given
        PasswordEncoder passwordEncoder = new PasswordEncoder();
        Set<Authorities> authorities = new HashSet<>();
        authorities.add(Authorities.ADMIN);

        User user = new User.Builder("username")
                .password(passwordEncoder.hashPassword("password"))
                .name("name")
                .email("email@email")
                .authorities(authorities).build();

        UserEntity userEntity = UserMapper.toEntity(user);

        // When
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        // Then
        Optional<UserEntity> userEntityOptional = userRepository.findById(1L);
        User savedUser = UserMapper.toDomain(userEntityOptional.get());

        String oldPassword = "wrongPassword";

        assertThrows(InvalidPasswordException.class,
                () -> passwordEncoder.matches(oldPassword, savedUser.getPassword()), "Invalid password");


    }

}
