package com.shop.list.shopappka.services;

import com.shop.list.shopappka.exceptions.UserExistsException;
import com.shop.list.shopappka.exceptions.UserNotFoundException;
import com.shop.list.shopappka.models.domain.Role;
import com.shop.list.shopappka.models.domain.UserEntity;
import com.shop.list.shopappka.payload.UpdateUser;
import com.shop.list.shopappka.payload.UserRequest;
import com.shop.list.shopappka.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(value = MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRequest userRequest;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        userRequest = UserRequest.builder()
                .username("dummy")
                .firstName("Greg")
                .email("dummy@dummy.com")
                .password("dummyPass")
                .build();

        user = UserEntity.builder()
                .userId(1L)
                .username("dummy")
                .password("gadsdhgaj#8asd9u1sh")
                .email("dummy@dummy.com")
                .firstName("Greg")
                .role(Role.ROLE_USER.name())
                .build();
    }

    @Nested
    @DisplayName("Test cases for signUpUser() method")
    class signUpUser {
        @Test
        void shouldReturnSignedUpUserObjectWhenObjectExists() {
            when(userRepository.findUserByEmail(userRequest.getEmail())).thenReturn(Optional.empty());
            when(userRepository.findUserByUsername(userRequest.getUsername())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("gadsdhgaj#8asd9u1sh");
            when(userRepository.save(any(UserEntity.class))).thenReturn(user);

            UserEntity savedUser = userService.signUpNewUser(userRequest);

            assertAll(
                    () -> assertNotNull(savedUser),
                    () -> assertEquals(user.getUsername(), savedUser.getUsername()),
                    () -> assertEquals(user.getEmail(), savedUser.getEmail()),
                    () -> assertEquals(user.getRole(), savedUser.getRole()),
                    () -> assertNotNull(savedUser.getUserId())
            );
        }

        @Test
        void shouldThrownUserExistsExceptionWhenUserEmailExistsInTheSystem() {
            when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
            assertThrows(UserExistsException.class, () -> userService.signUpNewUser(userRequest));
        }


        @Test
        void shouldThrownUserExistsExceptionWhenUserUsernameExistsInTheSystem() {
            when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(user));
            assertThrows(UserExistsException.class, () -> userService.signUpNewUser(userRequest));
        }
    }

    @Nested
    @DisplayName("Test cases for updateUserData() method")
    class updateUserData {
        @Test
        void shouldUpdateUserDataWhenUserExistsById() {
            UpdateUser updateUser = UpdateUser.builder()
                    .username("dummy1")
                    .firstName("Peter")
                    .email("email@email.com")
                    .build();
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(userRepository.save(any(UserEntity.class))).thenReturn(user);
            user.setFirstName(updateUser.getFirstName());
            user.setEmail(updateUser.getEmail());
            user.setUsername(updateUser.getUsername());

            UserEntity updatedUser = userService.updateUserData(updateUser, 1L);
            System.out.println(updatedUser);
            assertAll(
                    () -> assertNotNull(updatedUser),
                    () -> assertEquals(updateUser.getEmail(), updatedUser.getEmail()),
                    () -> assertEquals(updateUser.getUsername(), updatedUser.getUsername()),
                    () -> assertEquals(updateUser.getFirstName(), updatedUser.getFirstName())
            );
        }

        @Test
        void shouldThrownUserNotFoundExceptionWhenUserDoesNotExistInTheSystem() {
            UpdateUser updateUser = UpdateUser.builder()
                    .username("dummy1")
                    .firstName("Peter")
                    .email("email@email.com")
                    .build();
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
            assertThrows(UserNotFoundException.class, () -> userService.updateUserData(updateUser, 2L));
        }
    }

    @Nested
    @DisplayName("Test cases for getAllUsers() method")
    class getAllUsers {
        @Test
        void shouldReturnListOfUsersWhoExistsInTheSystem() {
            UserEntity user2 = UserEntity.builder()
                    .userId(2L)
                    .username("dummy2")
                    .firstName("David")
                    .password("DummyPass")
                    .role(Role.ROLE_USER.name())
                    .build();
            List<UserEntity> listOfUsers = new ArrayList<>();
            listOfUsers.add(user);
            listOfUsers.add(user2);

            when(userRepository.findAll()).thenReturn(listOfUsers);

            List<UserEntity> users = userService.getAllUsers();

            assertEquals(2, users.size());
        }

        @Test
        void shouldReturnEmptyListOfUsersWhenUsersNotExistInTheSystem() {
            when(userRepository.findAll()).thenReturn(new ArrayList<>());

            List<UserEntity> users = userService.getAllUsers();

            assertTrue(users.isEmpty());
        }
    }

    @Nested
    @DisplayName("Test cases for getUserById() method")
    class getUserById {
        @Test
        void shouldReturnConcreteUserWhenUserExistsById() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

            UserEntity foundUser = userService.getUserById(1L);

            assertAll(
                    () -> assertNotNull(foundUser),
                    () -> assertEquals(user.getEmail(), foundUser.getEmail()),
                    () -> assertEquals(user.getUsername(), foundUser.getUsername()),
                    () -> assertEquals(user.getFirstName(), foundUser.getFirstName())
            );
        }

        @Test
        void shouldThrownAUserNotFoundExceptionWhenUserDoesNotExist() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
            assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
        }
    }

}