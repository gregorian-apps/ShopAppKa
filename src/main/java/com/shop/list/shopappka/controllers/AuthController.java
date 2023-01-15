package com.shop.list.shopappka.controllers;

import com.shop.list.shopappka.configurations.auth.TokenProvider;
import com.shop.list.shopappka.models.domain.User;
import com.shop.list.shopappka.payload.JwtTokenResponse;
import com.shop.list.shopappka.payload.UserRequest;
import com.shop.list.shopappka.services.MapValidationErrorService;
import com.shop.list.shopappka.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Profile("prod")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final TokenProvider tokenProvider;

    private final UserService userService;

    private final MapValidationErrorService mapValidationErrorService;

    public AuthController(AuthenticationManager authenticationManager, TokenProvider tokenProvider,
                          UserService userService, MapValidationErrorService mapValidationErrorService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PostMapping("signUp")
    public ResponseEntity<?> signUpUser(@RequestBody @Valid UserRequest user, BindingResult result) {
        if (Objects.isNull(user)) {
            log.error("UserRequest is null");
            return new ResponseEntity<>("Request body with user is null", HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationError(result);
        if (errorMap != null) {
            return errorMap;
        }

        User user1 = userService.signUpNewUser(user);

        log.info("User with email {} has signed up successfully", user.getEmail());
        return new ResponseEntity<>(user1, HttpStatus.CREATED);
    }

    @PostMapping("signin")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        String token = tokenProvider.generateToken(auth);
        return new ResponseEntity<>(new JwtTokenResponse(token), HttpStatus.OK);
    }

    @Data
    private static class LoginRequest {

        @NotBlank(message = "Provide username")
        @Size(min = 4, message = "Username has at least 4 characters")
        private String username;

        @NotBlank(message = "Provide password")
        @Size(min = 6, message = "Password has at least 6 characters")
        private String password;
    }

}


