package com.shop.list.shopappka.services;

import com.shop.list.shopappka.exceptions.UserException;
import com.shop.list.shopappka.models.domain.Role;
import com.shop.list.shopappka.models.domain.User;
import com.shop.list.shopappka.payload.UpdateUser;
import com.shop.list.shopappka.payload.UserRequest;
import com.shop.list.shopappka.repositories.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
                .login(userRequest.getLogin())
                .password(userRequest.getPassword())
                .firstName(userRequest.getFirstName())
                .email(userRequest.getEmail())
                .role(Role.ROLE_USER.name())
                .build();
        return userRepository.save(user);
    }

    public void updateUserData(@NonNull UpdateUser updateUser, Long id) {
        Optional<User> existingUser = userRepository.findById(id);

        if(existingUser.isPresent()) {
            User user = existingUser.get();
            user.setEmail(updateUser.getEmail());
            user.setLogin(updateUser.getLogin());
            user.setFirstName(updateUser.getFirstName());
            userRepository.save(user);
        } else {
            log.warn("User with id {} doesn't exist in the system", id);
            throw new UserException("User doesn't exist in the system with id: " + id);
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if(optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            log.error("User with id {} not found", id);
            throw new UserException("User with id " + id + " not found");
        }
    }
}
