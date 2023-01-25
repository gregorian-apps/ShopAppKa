package com.shop.list.shopappka.models.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product-items")
@Builder
public class ProductItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "product_type")
    private ProductType productType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shooping_cart_id", nullable = false)
    @JsonIgnore
    private ShoppingCart shoppingCart;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductItem that = (ProductItem) o;
        return productId != null && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
