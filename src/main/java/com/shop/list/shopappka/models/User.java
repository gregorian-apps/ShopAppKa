package com.shop.list.shopappka.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String login;
    private String password;
    private String name;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

}
