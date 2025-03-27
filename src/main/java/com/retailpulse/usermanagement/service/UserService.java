package com.retailpulse.usermanagement.service;

import com.retailpulse.usermanagement.controller.CreateUserDTO;
import com.retailpulse.usermanagement.controller.UpdateUserDTO;
import com.retailpulse.usermanagement.controller.ViewUserDTO;
import com.retailpulse.usermanagement.domain.Authorities;
import com.retailpulse.usermanagement.domain.User;
import com.retailpulse.usermanagement.domain.exception.InvalidPasswordException;
import com.retailpulse.usermanagement.domain.exception.MalformedEmailException;
import com.retailpulse.usermanagement.domain.exception.MalformedPasswordException;
import com.retailpulse.usermanagement.infrastructure.persistence.UserEntity;
import com.retailpulse.usermanagement.infrastructure.persistence.UserMapper;
import com.retailpulse.usermanagement.repository.UserRepository;
import com.retailpulse.usermanagement.service.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    private static final String USERNAME_EXIST = "USERNAME_EXIST";
    private static final String INVALID_FORMAT = "INVALID_FORMAT";
    private static final String INVALID_OLD_PASSWORD = "INVALID_OLD_PASSWORD";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public List<ViewUserDTO> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        List<User> users = userEntities.stream().map(UserMapper::toDomain).toList();
        return users.stream().map(UserMapper::toDTO).toList();
    }

    public Optional<ViewUserDTO> getUserById(Long id) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(id);
        return userEntityOptional.map(UserMapper::toDomain).map(UserMapper::toDTO);
    }

    public Optional<ViewUserDTO> getUserByUsername(String username) {
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);
        return userEntityOptional.map(UserMapper::toDomain).map(UserMapper::toDTO);
    }

    public Optional<ViewUserDTO> getUserByName(String name) {
        Optional<UserEntity> userEntityOptional = userRepository.findByNameContaining(name);
        return userEntityOptional.map(UserMapper::toDomain).map(UserMapper::toDTO);
    }

    public ViewUserDTO createUser(CreateUserDTO createUserDTO) {

        if (userRepository.existsByUsername(createUserDTO.username())) {
            throw new BusinessException(USERNAME_EXIST, "Username already exist. Failed to create user.");
        }

        try {
            PasswordValidator.isValid(createUserDTO.password());
        } catch (MalformedPasswordException e) {
          throw new BusinessException(INVALID_FORMAT, e.getMessage() + " Failed to create user.");
        }

        User user;
        try {
            user = new User.Builder(createUserDTO.username())
                    .password(passwordEncoder.hashPassword(createUserDTO.password()))
                    .name(createUserDTO.name())
                    .email(createUserDTO.email())
                    .authorities(createUserDTO.roles().stream().map(Authorities::valueOf).collect(Collectors.toSet())).build();
        } catch (MalformedEmailException e) {
            throw new BusinessException(INVALID_FORMAT, e.getMessage() + " Failed to create user.");
        }
        
        UserEntity userEntity = UserMapper.toEntity(user);

        UserEntity savedUserEntity = userRepository.save(userEntity);

        return UserMapper.toDTO(UserMapper.toDomain(savedUserEntity));
    }

    public ViewUserDTO updateUser(Long id, UpdateUserDTO updateUserDTO) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(id);

        if (userEntityOptional.isEmpty()) {
            throw new BusinessException(USER_NOT_FOUND, "User not found. Failed to update user.");
        }

        User user = UserMapper.toDomain(userEntityOptional.get());

        user.updateName(updateUserDTO.name());

        try {
            user.updateEmail(updateUserDTO.email());
        } catch (MalformedEmailException e) {
            throw new BusinessException(INVALID_FORMAT, e.getMessage() + " Failed to update user.");
        }

        user.updateRoles(updateUserDTO.roles().stream().map(Authorities::valueOf).collect(Collectors.toSet()));

        user.updateEnabled(updateUserDTO.isEnabled());

        UserEntity updatedUserEntity = userRepository.save(UserMapper.toEntity(user, userEntityOptional.get()));

        return UserMapper.toDTO(UserMapper.toDomain(updatedUserEntity));
    }

    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new BusinessException(USER_NOT_FOUND, "User not found. Failed to delete user..");
        }
    }

    public void changePassword(Long id, String oldPassword, String newPassword) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(id);

        if (userEntityOptional.isEmpty()) {
            throw new BusinessException(USER_NOT_FOUND, "User not found. Failed to change password.");
        }

        UserEntity userEntity = userEntityOptional.get();

        User user = UserMapper.toDomain(userEntity);

        try {
            passwordEncoder.matches(oldPassword, user.getPassword());
        } catch (InvalidPasswordException e) {
            throw new BusinessException(INVALID_OLD_PASSWORD, "Wrong Old Password. Failed to change password.");
        }

        try {
            PasswordValidator.isValid(newPassword);
        } catch (MalformedPasswordException e) {
            throw new BusinessException(INVALID_FORMAT, e.getMessage() + "Failed to change password.");
        }

        user.changePassword(passwordEncoder.hashPassword(newPassword));

        userEntity.setPassword(user.getPassword());

        userRepository.save(userEntity);

    }
}
