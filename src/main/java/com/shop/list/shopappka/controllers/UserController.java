package com.shop.list.shopappka.controllers;

import com.shop.list.shopappka.exceptions.UserNotFoundException;
import com.shop.list.shopappka.models.domain.UserEntity;
import com.shop.list.shopappka.payload.UpdateUser;
import com.shop.list.shopappka.services.MapValidationErrorService;
import com.shop.list.shopappka.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateUserData(@PathVariable("id") Long id, @Valid @RequestBody UpdateUser updateUser, BindingResult result) {
        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationError(result);
        if (!userService.existsUserByUserId(id)) {
            log.error("User with id {} doesn't exist in the system", id);
            throw new UserNotFoundException("User doesn't exist in the system with id: " + id);
        }

        if (errorMap != null) {
            return errorMap;
        }

        UserEntity updatedUser = userService.updateUserData(updateUser, id);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getUsers() {
        List<UserEntity> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Long id) {
        UserEntity user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
