package com.shop.list.shopappka.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.list.shopappka.configurations.auth.TokenProvider;
import com.shop.list.shopappka.models.domain.UserEntity;
import com.shop.list.shopappka.payload.LoginRequest;
import com.shop.list.shopappka.payload.UserRequest;
import com.shop.list.shopappka.services.JwtUserService;
import com.shop.list.shopappka.services.MapValidationErrorService;
import com.shop.list.shopappka.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AuthController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    private final static String API_URL = "/api/auth/";
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private JwtUserService jwtUserService;

    @MockBean
    private UserService userService;

    @MockBean
    private MapValidationErrorService mapValidationErrorService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldRegisterNewUserThenReturnNewUserWithStatus201() throws Exception {
        UserRequest userRequest = UserRequest.builder().firstName("New FirstName").username("New Username").password("Dummy password").email("email@email.com").build();
        UserEntity user = UserEntity.builder().userId(1L).firstName("New FirstName").username("New Username").password("Dummy password").email("email@email.com").build();
        when(mapValidationErrorService.mapValidationError(any())).thenReturn(null);
        when(userService.signUpNewUser(userRequest)).thenReturn(user);
        when(userService.existsUserByUsername(anyString())).thenReturn(false);
        when(userService.existsUserByEmail(anyString())).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.post(API_URL + "signUp")
                        .content(mapper.writeValueAsString(userRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.firstName").value(userRequest.getFirstName()))
                .andExpect(jsonPath("$.username").value(userRequest.getUsername()))
                .andExpect(jsonPath("$.password").value(userRequest.getPassword()))
                .andExpect(jsonPath("$.email").value(userRequest.getEmail()));
    }

    @Test
    void shouldThrownUserExistsExceptionWhenUserExistsByEmailThenReturnInformationAboutExistenceWithStatusCode400() throws Exception {
        UserRequest userRequest = UserRequest.builder().firstName("New FirstName").username("New Username").password("Dummy password").email("email@email.com").build();
        UserEntity user = UserEntity.builder().userId(1L).firstName("New FirstName").username("New Username").password("Dummy password").email("email@email.com").build();
        when(mapValidationErrorService.mapValidationError(any())).thenReturn(null);
        when(userService.signUpNewUser(userRequest)).thenReturn(user);
        when(userService.existsUserByUsername(anyString())).thenReturn(false);
        when(userService.existsUserByEmail(anyString())).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.post(API_URL + "signUp")
                        .content(mapper.writeValueAsString(userRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("User with email: " + userRequest.getEmail() +  " exists in the system"));
    }

    @Test
    void shouldThrownUserExistsExceptionWhenUserExistsByUsernameThenReturnInformationAboutExistenceWithStatusCode400() throws Exception {
        UserRequest userRequest = UserRequest.builder().firstName("New FirstName").username("New Username").password("Dummy password").email("email@email.com").build();
        UserEntity user = UserEntity.builder().userId(1L).firstName("New FirstName").username("New Username").password("Dummy password").email("email@email.com").build();
        when(mapValidationErrorService.mapValidationError(any())).thenReturn(null);
        when(userService.signUpNewUser(userRequest)).thenReturn(user);
        when(userService.existsUserByUsername(anyString())).thenReturn(true);
        when(userService.existsUserByEmail(anyString())).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.post(API_URL + "signUp")
                        .content(mapper.writeValueAsString(userRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("User with username: " + userRequest.getUsername() + " exists in the system"));
    }

    @Test
    void shouldLoginWithValidCredentialsThenReturnTokenWithStatus200() throws Exception {
        LoginRequest loginRequest = new LoginRequest("Username", "Password");
        String token = "Bearer skdaoskdsaodkas.adasd.asdas";
        Authentication authentication = mock(Authentication.class);
        when(mapValidationErrorService.mapValidationError(any())).thenReturn(null);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(any(Authentication.class))).thenReturn(token);
        mvc.perform(MockMvcRequestBuilders.post(API_URL + "signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
}