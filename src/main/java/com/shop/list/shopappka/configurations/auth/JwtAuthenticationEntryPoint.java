package com.shop.list.shopappka.configurations.auth;

import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException{
        String jsonLoginResponse = new Gson().toJson(new InvalidLoginResponse());
        log.error("Unauthorized error with message - {}", authException.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(401);
        response.getWriter().println(jsonLoginResponse);
    }

    private static class InvalidLoginResponse {
        private final String username;
        private final String password;

        InvalidLoginResponse() {
            this.username = "Wrong username";
            this.password = "Wrong password";
        }
    }
}
