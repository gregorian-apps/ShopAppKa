package com.shop.list.shopappka.mappers.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.list.shopappka.models.domain.ShopListItem;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


@Slf4j
@Converter
public class ProductItemsConverter implements AttributeConverter<Map<String, ShopListItem>, String> {

    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(Map<String, ShopListItem> attribute) {
        String productItemsJson = null;
        try {
            productItemsJson = mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Parsing map to json with product items error {}", attribute);
        }

        return productItemsJson;
    }

    @Override
    public Map<String, ShopListItem> convertToEntityAttribute(String productItemsJson) {
        Map<String, ShopListItem> productItemsMap = null;
        try {
            productItemsMap = mapper.readValue(productItemsJson, new TypeReference<Map<String, ShopListItem>>(){});
        } catch (JsonProcessingException e) {
            log.error("Problem with mapping json to map product items {}", productItemsJson);
        }

        return productItemsMap;
    }
}
