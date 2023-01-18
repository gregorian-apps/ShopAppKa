package com.shop.list.shopappka.services;

import com.shop.list.shopappka.exceptions.UserException;
import com.shop.list.shopappka.exceptions.UserExistsException;
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
        Optional<UserEntity> userByEmailExists = userRepository.findUserByEmail(userRequest.getEmail());
        Optional<UserEntity> userByLoginExists = userRepository.findUserByUsername(userRequest.getUsername());

        if(userByEmailExists.isPresent()){
            log.warn("User with email {} exists in the system", userByEmailExists.get().getEmail());
            throw new UserExistsException("User with email: " + userByEmailExists.get().getEmail() + " exists in the system");
        }

        if(userByLoginExists.isPresent()){
            log.warn("User with email {} exists in the system", userByLoginExists.get().getUsername());
            throw new UserExistsException("User with login: " + userByLoginExists.get().getUsername() + " exists in the system");
        }

        UserEntity user = UserEntity.builder()
                .username(userRequest.getUsername())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .firstName(userRequest.getFirstName())
                .email(userRequest.getEmail())
                .role(Role.ROLE_USER.name())
                .build();
        return userRepository.save(user);
    }

    public UserEntity updateUserData(@NonNull UpdateUser updateUser, Long id) throws UserException {
        Optional<UserEntity> existingUser = userRepository.findById(id);

        if(existingUser.isPresent()) {
            UserEntity user = existingUser.get();
            user.setEmail(updateUser.getEmail());
            user.setUsername(updateUser.getUsername());
            user.setFirstName(updateUser.getFirstName());
            return userRepository.save(user);
        } else {
            log.error("User with id {} doesn't exist in the system", id);
            throw new UserNotFoundException("User doesn't exist in the system with id: " + id);
        }
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
}
