package com.retailpulse.usermanagement.repository;

import com.retailpulse.usermanagement.infrastructure.persistence.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByNameContaining(String name);

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
