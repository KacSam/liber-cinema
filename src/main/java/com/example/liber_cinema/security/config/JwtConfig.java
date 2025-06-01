package com.example.liber_cinema.security.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Value("${liber.cinema.jwtSecret}")
    private String jwtSecret;

    /**
     * Provides a secure key for JWT signing
     * This ensures the key is properly sized for HS512 algorithm
     */
    @Bean
    public Key jwtSigningKey() {
        // Decode the Base64 encoded secret from properties and create a secure HMAC-SHA key
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
    }
}
