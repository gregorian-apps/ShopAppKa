package com.shop.list.shopappka.controllers;

import com.shop.list.shopappka.exceptions.UserException;
import com.shop.list.shopappka.models.domain.User;
import com.shop.list.shopappka.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/data/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("signUp")
    public ResponseEntity<User> signUpUser(@RequestBody @Valid User user) {
        if (user == null) {
            log.error("User is null");
            throw new UserException("User cannot be null");
        }

        User user1 = userService.signUpNewUser(user);

        log.info("User with email {} has signed up successfully", user.getEmail());
        return new ResponseEntity<>(user1, HttpStatus.OK);
    }
}
