package com.shop.list.shopappka.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUser {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 4, message = "Username has to at least 4 characters")
    private String username;

    @Email(message = "Provide valid email")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    private String firstName;
}
