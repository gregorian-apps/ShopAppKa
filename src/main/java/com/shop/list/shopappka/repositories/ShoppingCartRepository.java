package com.shop.list.shopappka.repositories;

import com.shop.list.shopappka.models.domain.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, UUID> {

    @Query("SELECT sc FROM ShoppingCart sc WHERE sc.shoppingCartName=:name")
    Optional<ShoppingCart> findShoppingCartByName(@Param("name") String name);

    @Query("SELECT sc FROM ShoppingCart sc WHERE sc.shoppingCartId=:id")
    Optional<ShoppingCart> findShoppingCartByShoppingCartId(@Param("id") Long id);

    @Query("SELECT sc FROM ShoppingCart sc WHERE sc.group.groupId=:id")
    List<ShoppingCart> findAllShoppingCartsByGroupId(@Param("id") Long groupId);
}
