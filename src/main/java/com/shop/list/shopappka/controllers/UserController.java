package com.shop.list.shopappka.controllers;

import com.shop.list.shopappka.models.domain.User;
import com.shop.list.shopappka.payload.UpdateUser;
import com.shop.list.shopappka.services.MapValidationErrorService;
import com.shop.list.shopappka.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/data/users")
@Profile("prod")
public class UserController {

    private final UserService userService;

    private final MapValidationErrorService mapValidationErrorService;

    public UserController(UserService userService, MapValidationErrorService mapValidationErrorService) {
        this.userService = userService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateUserData(@PathVariable("id") Long id, @Valid @RequestBody UpdateUser updateUser, BindingResult result) {
        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationError(result);
        if (Objects.isNull(updateUser)) {
            log.error("UpdateUserData is null");
            return new ResponseEntity<>("Request body with data for updating user is null", HttpStatus.BAD_REQUEST);
        }

        if (errorMap != null) {
            return errorMap;
        }

        User updatedUser = userService.updateUserData(updateUser, id);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
