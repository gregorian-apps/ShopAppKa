package com.shop.list.shopappka.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.list.shopappka.configurations.auth.TokenProvider;
import com.shop.list.shopappka.exceptions.UserNotFoundException;
import com.shop.list.shopappka.models.domain.UserEntity;
import com.shop.list.shopappka.payload.UpdateUser;
import com.shop.list.shopappka.services.JwtUserService;
import com.shop.list.shopappka.services.MapValidationErrorService;
import com.shop.list.shopappka.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    private final static String API_URL = "/api/data/users";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private MapValidationErrorService mapValidationErrorService;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private JwtUserService jwtUserService;

    private UserEntity user;
    private UserEntity user1;
    private List<UserEntity> users;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        user = UserEntity.builder()
                .userId(1L)
                .firstName("dummy name")
                .password("dummy password")
                .email("email@email.com")
                .role("ROLE_USER")
                .username("dummy username")
                .build();
        user1 = UserEntity.builder()
                .userId(2L)
                .firstName("dummy name1")
                .password("dummy password11")
                .email("email1@email1.com")
                .role("ROLE_USER")
                .username("dummy username1")
                .build();
        users = List.of(user, user1);
    }

    @Test
    void shouldReturnUserByIdWithStatusCode200() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(user);
        mvc.perform(MockMvcRequestBuilders
                        .get(API_URL + "/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("dummy username"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("dummy password"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("email@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("dummy name"));
    }

    @Test
    void shouldReturnInfoThatUserNotFoundWithStatusCode400() throws Exception {
        when(userService.getUserById(anyLong())).thenThrow(UserNotFoundException.class);
        mvc.perform(MockMvcRequestBuilders.get(API_URL + "/1").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnListOfUsersWithStatusCode200() throws Exception {
        when(userService.getAllUsers()).thenReturn(users);
        mvc.perform(MockMvcRequestBuilders.get(API_URL).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].userId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].userId").value(2));
    }

    @Test
    void shouldUpdateUserDataWhenUpdateDataIsValidThenReturnUpdatedUserWithStatusCode200() throws Exception {
        UpdateUser updateUser = UpdateUser.builder().username("Test username").firstName("Test fistName").email("email@email.com1").build();
        user.setUsername(updateUser.getUsername());
        user.setEmail(updateUser.getEmail());
        user.setFirstName(updateUser.getFirstName());
        when(userService.updateUserData(updateUser, 1L)).thenReturn(user);
        when(userService.existsUserByUserId(anyLong())).thenReturn(true);
        mvc.perform(MockMvcRequestBuilders.put(API_URL + "/update/{id}", 1L)
                        .content(mapper.writeValueAsString(updateUser)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(updateUser.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(updateUser.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(updateUser.getFirstName()));
    }

    @Test
    void shouldThrownUSerNtFoundExceptionDuringUpdateUserDataWhenUserNotFoundThenReturnInformationAboutThatWithStatus404() throws Exception {
        UpdateUser updateUser = UpdateUser.builder().username("Test username").firstName("Test fistName").email("email@email.com1").build();
        when(userService.existsUserByUserId(anyLong())).thenReturn(false);
        when(userService.updateUserData(updateUser, 1L)).thenReturn(user);
        mvc.perform(MockMvcRequestBuilders.put(API_URL + "/update/{id}", 1L)
                        .content(mapper.writeValueAsString(updateUser)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("User doesn't exist in the system with id: " + user.getUserId()));
    }
}
