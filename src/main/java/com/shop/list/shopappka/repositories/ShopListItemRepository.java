package com.shop.list.shopappka.repositories;

import com.shop.list.shopappka.models.ShopListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShopListItemRepository extends JpaRepository<ShopListItem, UUID> {
}
