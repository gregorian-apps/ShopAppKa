package com.shop.list.shopappka.repositories;

import com.shop.list.shopappka.models.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("SELECT g from Group g WHERE g.id=:id")
    Optional<Group> getGroupById(@Param("id") Long id);

    @Query("SELECT g from Group g WHERE g.name=:name")
    Optional<Group> getGroupByName(@Param("name") String name);
}
