package com.shop.list.shopappka.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "Login cannot be blank")
    @Size(min = 4, message = "Login has to at least 4 characters")
    private String login;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password should have at least 4 characters")
    private String password;

    @Email(message = "Provide valid email")
    @NotBlank(message = "Email cannot be blank")
    private String email;
    private String firstName;
}
