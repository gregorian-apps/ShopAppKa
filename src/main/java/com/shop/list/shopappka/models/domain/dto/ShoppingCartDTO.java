package com.shop.list.shopappka.models.domain.dto;

import com.shop.list.shopappka.mappers.converters.ProductItemsConverter;
import com.shop.list.shopappka.models.domain.ShopListItem;
import jakarta.persistence.Convert;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class ShoppingCartDTO {

    private String id;
    private String name;
    @Convert(converter = ProductItemsConverter.class)
    private Map<String, ShopListItem> productItems;
}
