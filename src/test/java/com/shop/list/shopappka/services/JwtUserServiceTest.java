package com.shop.list.shopappka.services;

import com.shop.list.shopappka.exceptions.UserNotFoundException;
import com.shop.list.shopappka.models.domain.UserEntity;
import com.shop.list.shopappka.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JwtUserService jwtUserService;

    @Test
    void shouldLoadUserByUsernameWhenUserExists() {
        UserEntity userEntity = UserEntity.builder()
                .userId(1L)
                .username("username")
                .email("username@username.com")
                .firstName("firstName")
                .groups(null)
                .password("Dummy123")
                .role("ROLE_USER")
                .build();
        when(userRepository.findUserByUsernameOrEmail(anyString())).thenReturn(Optional.of(userEntity));

        UserDetails user = jwtUserService.loadUserByUsername("username");

        assertAll(
                () -> assertEquals(userEntity.getUsername(), user.getUsername()),
                () -> assertEquals(userEntity.getPassword(), user.getPassword()),
                () -> assertEquals(1, user.getAuthorities().size())
        );
    }

    @Test
    void shouldThrownUserNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findUserByUsernameOrEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> jwtUserService.loadUserByUsername(anyString()));
    }
}