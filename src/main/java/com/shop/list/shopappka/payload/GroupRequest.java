package com.shop.list.shopappka.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupRequest {


    @NotBlank(message = "Name of group cannot be null")
    @Size(min = 3, message = "Name of group should have at least 3 characters")
    private String name;

    @JsonCreator
    public GroupRequest(String name) {
        this.name = name;
    }
}
