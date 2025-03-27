package com.retailpulse.usermanagement.infrastructure.persistence;

import com.retailpulse.usermanagement.controller.ViewUserDTO;
import com.retailpulse.usermanagement.domain.Authorities;
import com.retailpulse.usermanagement.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public static UserEntity toEntity(User user, UserEntity entity) {
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setEnabled(user.getEnabled());
        entity.addRoles(user.getAuthorities().stream()
                .map(Authorities::getAuthority)
                .toList());
        return entity;
    }

    public static UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity(user.getUsername(), user.getPassword(), user.getName(), user.getEmail(), user.getEnabled());
        entity.addRoles(user.getAuthorities().stream()
                .map(Authorities::getAuthority)
                .toList());
        return entity;
    }

    public static User toDomain(UserEntity userEntity) {
        Set<Authorities> authorities = userEntity.getAuthorities().stream()
                .map(role -> Authorities.valueOf(role.getAuthority()))
                .collect(Collectors.toSet());

        return new User.Builder(userEntity.getUsername())
                .id(userEntity.getId())
                .password(userEntity.getPassword())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .authorities(authorities)
                .enabled(userEntity.isEnabled())
                .build();
    }

    public static ViewUserDTO toDTO(User user) {
        List<String> authorities = user.getAuthorities().stream()
                .map(Authorities::getAuthority)
                .toList();
        return new ViewUserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getName(), authorities, user.getEnabled());
    }

}
