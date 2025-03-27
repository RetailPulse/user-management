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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NewUserTest {

    @Mock
    private UserRepository userRepository;

    @Test
    public void registerUser() {
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
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        // Then
        assertThat(userRepository.save(userEntity)).isEqualTo(userEntity);
        UserEntity savedUserEntity = userRepository.save(userEntity);
        User savedUser = UserMapper.toDomain(savedUserEntity);
        assertThat(bCryptPasswordEncoder.matches("password", savedUser.getPassword())).isTrue();
        assertThat(savedUser.getAuthorities()).isEqualTo(authorities);
        assertThat(savedUser.getEnabled()).isTrue();

    }

}
