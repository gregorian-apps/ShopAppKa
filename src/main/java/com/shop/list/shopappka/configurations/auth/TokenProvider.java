package com.shop.list.shopappka.configurations.auth;

import com.shop.list.shopappka.models.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenProvider implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Value("${shopappka.app.jwtSecret}")
    private String jwtSecret;

    @Value("${shopappka.app.jwtExpirationMs}")
    private Long jwtExpirationMs;

    public String generateToken(Authentication auth) {
        User user = (User) auth.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("email", user.getEmail());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date((System.currentTimeMillis()) + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails user) {
        String username = getUsernameFromToken(token);
        final Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        boolean isTokenExpired = claims.getExpiration().before(new Date());
        return (username.equals(user.getUsername()) && !isTokenExpired);

    }

    public String getUsernameFromToken(String token) {
        final Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }


}
