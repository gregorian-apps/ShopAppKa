package com.shop.list.shopappka.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UserRequest {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 4, message = "Username has to at least 4 characters")
    private String username;

    @Email(message = "Provide valid email")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    private String firstName;
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password should have at least 6 characters")
    private String password;
}
