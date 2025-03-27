package com.retailpulse.usermanagement.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "authorities")
public class AuthoritiesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    private UserEntity userEntity; // ✅ Enforces ownership

    private String authority;

    // required by JPA
    protected AuthoritiesEntity() {
    }

    // ✅ Package-private constructor (only accessible inside `persistence` package)
    AuthoritiesEntity(UserEntity userEntity, String authority) {
        this.userEntity = userEntity;
        this.authority = authority;
    }
}
