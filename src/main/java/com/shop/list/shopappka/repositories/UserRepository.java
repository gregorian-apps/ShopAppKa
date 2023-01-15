package com.shop.list.shopappka.repositories;

import com.shop.list.shopappka.models.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email=:email")
    Optional<User> findUserByEmail(@Param("email") String email);

    @Query("SELECT u from User u WHERE u.username=:username")
    Optional<User> findUserByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.username =:username OR u.email=:username")
    Optional<User> findUserByUsernameOrEmail(@Param("username") String username);
}
