package com.retailpulse.usermanagement.domain;

import com.retailpulse.usermanagement.infrastructure.persistence.UserEntity;
import com.retailpulse.usermanagement.infrastructure.persistence.UserMapper;
import com.retailpulse.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ViewUserTest {

    @Mock
    private UserRepository userRepository;

    @Test
    public void viewAllUsers() {
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
        when(userRepository.findAll()).thenReturn(List.of(userEntity));

        // Then
        List<UserEntity> userEntities = userRepository.findAll();
        List<User> users = userEntities.stream().map(UserMapper::toDomain).toList();

        assertThat(users).isNotEmpty();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getUsername()).isEqualTo("username");
        assertThat(bCryptPasswordEncoder.matches("password", users.get(0).getPassword())).isTrue();
    }

    @Test
    public void viewUserDetail() {
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
        Optional<User> savedUser = userRepository.findById(1L).map(UserMapper::toDomain);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.get().getUsername()).isEqualTo("username");
        assertThat(bCryptPasswordEncoder.matches("password", savedUser.get().getPassword())).isTrue();

    }

}
