package com.shop.list.shopappka.repositories;

import com.shop.list.shopappka.models.domain.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, UUID> {

    @Query("SELECT p FROM ProductItem p WHERE p.shoppingCart=:id")
    List<ProductItem> findAllByShoppingCart(@Param("id") Long shoppingCartId);
}
