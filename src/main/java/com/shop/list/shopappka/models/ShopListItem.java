package com.shop.list.shopappka.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "shoplist-items", schema="shoplist")
public class ShopListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private int amount;
    private Category category;

    @ManyToOne
    private Group group;
}
