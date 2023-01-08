package com.shop.list.shopappka.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserRequest extends UpdateUser{
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password should have at least 4 characters")
    private String password;
}
