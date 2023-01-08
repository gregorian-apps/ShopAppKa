package com.shop.list.shopappka.services;

import com.shop.list.shopappka.exceptions.UserException;
import com.shop.list.shopappka.models.domain.User;
import com.shop.list.shopappka.payload.UserRequest;
import com.shop.list.shopappka.repositories.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User signUpNewUser(@NotNull UserRequest userRequest) {
        Optional<User> userByEmailExists = userRepository.findUserByEmail(userRequest.getEmail());
        Optional<User> userByLoginExists = userRepository.findUserByLogin(userRequest.getLogin());

        if(userByEmailExists.isPresent()){
            log.warn("User with email {} exists in the system", userByEmailExists.get().getEmail());
            throw new UserException("User with email: " + userByEmailExists.get().getEmail() + " exists in the system");
        }

        if(userByLoginExists.isPresent()){
            log.warn("User with email {} exists in the system", userByLoginExists.get().getLogin());
            throw new UserException("User with login: " + userByLoginExists.get().getLogin() + " exists in the system");
        }

        User user = User.builder()
                .id(UUID.randomUUID())
                .login(userRequest.getLogin())
                .password(userRequest.getPassword())
                .firstName(userRequest.getFirstName())
                .email(userRequest.getEmail())
                .build();
        return userRepository.save(user);
    }
}
