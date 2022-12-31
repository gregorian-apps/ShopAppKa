package com.shop.list.shopappka.repositories;

import com.shop.list.shopappka.models.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
}
