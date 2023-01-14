package com.shop.list.shopappka.payload;

import lombok.Data;

import java.io.Serializable;

@Data
public final class JwtTokenResponse implements Serializable {
    private static final long serialVersion = 1L;
    private final String token;
}
