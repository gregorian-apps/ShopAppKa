package com.shop.list.shopappka.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Provide username")
    @Size(min = 4, message = "Username has at least 4 characters")
    private String username;

    @NotBlank(message = "Provide password")
    @Size(min = 6, message = "Password has at least 6 characters")
    private String password;
}

