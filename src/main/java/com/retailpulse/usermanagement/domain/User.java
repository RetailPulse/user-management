package com.retailpulse.usermanagement.domain;

import com.retailpulse.usermanagement.domain.exception.MalformedEmailException;
import lombok.Getter;

import java.util.Collections;
import java.util.Set;

@Getter
public class User {

    private Long id;
    private final String username;
    private String password;
    private String name;
    private String email;
    private Set<Authorities> authorities;
    private Boolean enabled;

    private User(Builder builder) {
        this.username = builder.username;
        this.id = builder.id;
        this.password = builder.password;
        this.name = builder.name;

        if (builder.email != null) {
            validateEmailPattern(builder.email);
        }
        this.email = builder.email;

        this.authorities = builder.authorities == null ? Collections.emptySet() : Collections.unmodifiableSet(builder.authorities);
        this.enabled = builder.enabled == null ? true : builder.enabled;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateRoles(Set<Authorities> newAuthorities) {
        this.authorities = newAuthorities == null ? Collections.emptySet() : Collections.unmodifiableSet(newAuthorities);
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    public void updateEmail(String newEmail) {
        if (newEmail != null) {
            validateEmailPattern(newEmail);
        }
        this.email = newEmail;
    }

    public void updateEnabled(Boolean enabled) {
        this.enabled = enabled == null || enabled;
    }

    public static class Builder {
        private Long id;
        private final String username;
        private String password;
        private String name;
        private String email;
        private Set<Authorities> authorities;
        private Boolean enabled;

        public Builder(String username) {
            this.username = username;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder authorities(Set<Authorities> authorities) {
            this.authorities = authorities;
            return this;
        }

        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public User build() {
            return new User(this);
        }

    }

    private void validateEmailPattern(String email) {
        if (!email.matches("^(.+)@(.+)$")) {
            throw new MalformedEmailException("Invalid email format");
        }
    }
}
