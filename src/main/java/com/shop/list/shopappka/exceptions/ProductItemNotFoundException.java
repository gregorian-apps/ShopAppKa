package com.shop.list.shopappka.exceptions;

public class ProductItemNotFoundException extends RuntimeException {
    public ProductItemNotFoundException(String message) {
        super(message);
    }
}
