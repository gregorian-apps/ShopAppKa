package com.shop.list.shopappka.services;

import com.shop.list.shopappka.exceptions.UserException;
import com.shop.list.shopappka.models.domain.User;
import com.shop.list.shopappka.repositories.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User signUpNewUser(@NotNull User user) {
        Optional<User> userExists = userRepository.findUserByEmail(user.getEmail());

        if(userExists.isPresent()){
            log.warn("User with email {} exists in the system", userExists.get().getEmail());
            throw new UserException("User with email:" + userExists.get().getEmail() + " exists in the system");
        }
        return userRepository.save(user);
    }
}
