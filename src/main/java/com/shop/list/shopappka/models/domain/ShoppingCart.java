package com.shop.list.shopappka.models.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@Table(name = "shopping-carts")
@Builder
@NoArgsConstructor
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "product-items", columnDefinition = "TEXT")
    private String productItemsJSON;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ShoppingCart that = (ShoppingCart) o;
        return uuid != null && Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
