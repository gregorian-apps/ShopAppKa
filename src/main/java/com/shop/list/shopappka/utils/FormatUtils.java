package com.shop.list.shopappka.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
public class FormatUtils {
    public static UUID getUUIDFromString(String id) {
        String regex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        Pattern pattern = Pattern.compile(regex);

        if(pattern.matcher(id).matches()) {
            return UUID.fromString(id);
        } else {
            log.error("String ID {} is not in UUID format", id);
            return null;
        }
    }
}
