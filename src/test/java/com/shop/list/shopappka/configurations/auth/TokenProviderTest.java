package com.shop.list.shopappka.configurations.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.mockito.Mockito.when;

class TokenProviderTest {

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private TokenProvider tokenProvider;

    private static final String SECRET = "mySecret";
    private static final Long EXPIRATION_TIME = 10000L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenProvider = new TokenProvider();
        tokenProvider.jwtSecret = SECRET;
        tokenProvider.jwtExpirationMs = EXPIRATION_TIME;
    }

    @Test
    void testGenerateToken() {
        String username = "testuser";
        when(userDetails.getUsername()).thenReturn(username);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);
        String token = tokenProvider.generateToken(auth);
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        Assertions.assertEquals(username, claims.getSubject());
    }

    @Test
    void testValidateTokenWithValidToken() {
        String username = "testuser";
        when(userDetails.getUsername()).thenReturn(username);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);
        String token = tokenProvider.generateToken(auth);
        boolean result = tokenProvider.validateToken(token, userDetails);
        Assertions.assertTrue(result);
    }

    @Test
    void testGetUsernameFromToken() {
        String username = "testuser";
        when(userDetails.getUsername()).thenReturn(username);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);
        String token = tokenProvider.generateToken(auth);
        String result = tokenProvider.getUsernameFromToken(token);
        Assertions.assertEquals(username, result);
    }
}
