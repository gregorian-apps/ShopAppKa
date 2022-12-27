package com.shop.list.shopappka.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "groups", schema="shoplist")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;

    @OneToMany(mappedBy = "group")
    private Set<User> users;

    @OneToMany(mappedBy = "group")
    private Set<ShopListItem> items;
}
