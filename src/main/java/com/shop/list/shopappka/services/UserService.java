package com.shop.list.shopappka.services;

import com.shop.list.shopappka.exceptions.UserNotFoundException;
import com.shop.list.shopappka.models.domain.Role;
import com.shop.list.shopappka.models.domain.UserEntity;
import com.shop.list.shopappka.payload.UpdateUser;
import com.shop.list.shopappka.payload.UserRequest;
import com.shop.list.shopappka.repositories.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity signUpNewUser(@NotNull UserRequest userRequest) {
        UserEntity user = UserEntity.builder()
                .username(userRequest.getUsername())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .firstName(userRequest.getFirstName())
                .email(userRequest.getEmail())
                .role(Role.ROLE_USER.name())
                .build();
        return userRepository.save(user);
    }

    public UserEntity updateUserData(@NonNull UpdateUser updateUser, Long id) {
        UserEntity user = getUserById(id);
        user.setEmail(updateUser.getEmail());
        user.setUsername(updateUser.getUsername());
        user.setFirstName(updateUser.getFirstName());
        return userRepository.save(user);
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public UserEntity getUserById(Long id) {
        Optional<UserEntity> optionalUser = userRepository.findById(id);

        if(optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            log.error("User with id {} not found", id);
            throw new UserNotFoundException("User with id " + id + " not found");
        }
    }

    public boolean existsUserByUserId(Long userId) {
        return userRepository.existsByUserId(userId);
    }
    
    public boolean existsUserByEmail(String email) {
        Optional<UserEntity> optionalUser = userRepository.findUserByEmail(email);
        return optionalUser.isPresent();
    }

    public boolean existsUserByUsername(String username) {
        Optional<UserEntity> optionalUser = userRepository.findUserByUsername(username);
        return optionalUser.isPresent();
    }
}
