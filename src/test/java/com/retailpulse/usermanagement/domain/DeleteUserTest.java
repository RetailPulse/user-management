package com.retailpulse.usermanagement.domain;

import com.retailpulse.usermanagement.infrastructure.persistence.UserEntity;
import com.retailpulse.usermanagement.infrastructure.persistence.UserMapper;
import com.retailpulse.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeleteUserTest {

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
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
    public void deleteUser() {
        // Given
        UserEntity savedUserEntity = userRepository.getById(1L);
        User savedUser = UserMapper.toDomain(savedUserEntity);
        UserEntity userEntity = UserMapper.toEntity(savedUser);

        userRepository.delete(userEntity);

        // Then
        verify(userRepository, times(1)).delete(userEntity);
    }
}
