package com.retailpulse.usermanagement.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Setter
    private String password;

    @Setter
    private String name;

    @Setter
    private String email;

    @Setter
    private boolean enabled;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<AuthoritiesEntity> authorities = new ArrayList<>();

    // required by JPA
    protected UserEntity() {
    }

    public UserEntity(String username, String password, String name, String email, Boolean enabled) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.enabled = enabled == null ? true : enabled;
    }

    // ✅ Enforces Aggregation: Authorities can only be added via the Aggregate Root
    private void addRole(String role) {
        // Check if the role already exists in the authorities collection
        boolean roleExists = this.authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals(role));

        // If the role doesn't exist, add it
        if (!roleExists) {
            this.authorities.add(new AuthoritiesEntity(this, role));
        }
    }

    public void addRoles(List<String> roles) {
        roles.forEach(this::addRole);
    }

    // ✅ Prevent direct modifications to `authorities`
    public List<AuthoritiesEntity> getAuthorities() {
        return Collections.unmodifiableList(authorities);
    }

}
