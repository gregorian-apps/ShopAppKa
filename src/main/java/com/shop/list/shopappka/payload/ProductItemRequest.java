package com.shop.list.shopappka.payload;

import com.shop.list.shopappka.models.domain.ProductType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductItemRequest {

    @NotBlank(message = "Name of product cannot be blank")
    private String productName;

    @NotBlank(message = "Amount of product cannot be blank")
    private int amount;

    @NotBlank(message = "Product type cannot be blank")
    private ProductType productType;
}
