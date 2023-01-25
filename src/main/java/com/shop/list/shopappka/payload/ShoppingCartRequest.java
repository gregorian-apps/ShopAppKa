package com.shop.list.shopappka.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ShoppingCartRequest {

    @Size(min = 3, message = "Name of shopping cart should have at least 3 characters")
    @NotBlank(message = "Name of shopping cart cannot be blank")
    private String name;

    private List<ProductItemRequest> productItems;
}
