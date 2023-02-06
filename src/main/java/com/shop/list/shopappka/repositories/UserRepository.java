package com.shop.list.shopappka.repositories;

import com.shop.list.shopappka.models.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u FROM Users u WHERE u.email=:email")
    Optional<UserEntity> findUserByEmail(@Param("email") String email);

    @Query("SELECT u from Users u WHERE u.username=:username")
    Optional<UserEntity> findUserByUsername(@Param("username") String username);

    @Query("SELECT u FROM Users u WHERE u.username =:username OR u.email=:username")
    Optional<UserEntity> findUserByUsernameOrEmail(@Param("username") String username);

    boolean existsByUserId(Long userId);
}
