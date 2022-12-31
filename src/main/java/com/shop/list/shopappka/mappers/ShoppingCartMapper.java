package com.shop.list.shopappka.mappers;

import com.shop.list.shopappka.mappers.converters.ProductItemsConverter;
import com.shop.list.shopappka.models.domain.ShoppingCart;
import com.shop.list.shopappka.models.domain.dto.ShoppingCartDTO;

import java.util.UUID;

public class ShoppingCartMapper {

    private final ProductItemsConverter converter;

    public ShoppingCartMapper(ProductItemsConverter converter) {
        this.converter = converter;
    }

    public ShoppingCart fromDTO(ShoppingCartDTO dto) {
        return ShoppingCart.builder()
                .uuid(UUID.fromString(dto.getId()))
                .name(dto.getName())
                .productItemsJSON(converter.convertToDatabaseColumn(dto.getProductItems()))
                .build();
    }

    public ShoppingCartDTO toDTO(ShoppingCart domain) {
        return ShoppingCartDTO.builder()
                .id(domain.getUuid().toString())
                .name(domain.getName())
                .productItems(converter.convertToEntityAttribute(domain.getProductItemsJSON()))
                .build();
    }
}
