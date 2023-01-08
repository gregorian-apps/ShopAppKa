package com.shop.list.shopappka.controllers;

import com.shop.list.shopappka.models.domain.User;
import com.shop.list.shopappka.payload.UserRequest;
import com.shop.list.shopappka.services.MapValidationErrorService;
import com.shop.list.shopappka.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/data/users")
public class UserController {

    private final UserService userService;

    private final MapValidationErrorService mapValidationErrorService;

    public UserController(UserService userService, MapValidationErrorService mapValidationErrorService) {
        this.userService = userService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PostMapping("signUp")
    public ResponseEntity<?> signUpUser(@RequestBody @Valid UserRequest user, BindingResult result) {
        if (user == null) {
            log.error("UserRequest is null");
            return new ResponseEntity<>("Request body with user is null", HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationError(result);
        if (errorMap != null) {
            return errorMap;
        }

        User user1 = userService.signUpNewUser(user);

        log.info("User with email {} has signed up successfully", user.getEmail());
        return new ResponseEntity<>(user1, HttpStatus.OK);

    }
}