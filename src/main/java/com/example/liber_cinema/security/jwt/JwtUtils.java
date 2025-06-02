package com.example.liber_cinema.security.jwt;

import com.example.liber_cinema.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${liber.cinema.jwtSecret}")
    private String jwtSecret;

    @Value("${liber.cinema.jwtExpirationMs}")
    private int jwtExpirationMs = 45 * 60 * 1000; // 45 minut

    @Value("${liber.cinema.refreshTokenExpirationMs}")
    private int refreshTokenExpirationMs = 7 * 24 * 60 * 60 * 1000; // 7 dni

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + refreshTokenExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    private Key key() {
        // Using the secure key generation method for HS512
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }    public boolean validateJwtToken(String authToken) {
        if (authToken == null || authToken.isBlank()) {
            logger.error("JWT token is null or blank");
            throw new IllegalArgumentException("JWT token cannot be null or blank");
        }

        try {
            System.out.println("Validating JWT token: " + authToken.substring(0, Math.min(10, authToken.length())) + "...");
            Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(authToken);
            System.out.println("JWT token validated successfully");
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token format");
            throw new JwtException("Invalid token format");
        } catch (ExpiredJwtException e) {
            logger.error("JWT token has expired");
            throw new JwtException("Token has expired");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token");
            throw new JwtException("Unsupported token type");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty");
            throw new JwtException("Token claims are empty");
        } catch (Exception e) {
            logger.error("JWT validation error: {}", e.getMessage());
            throw new JwtException("Token validation failed: " + e.getMessage());
        }
    }
}
