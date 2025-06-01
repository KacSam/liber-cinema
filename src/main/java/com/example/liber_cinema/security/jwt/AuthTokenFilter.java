package com.example.liber_cinema.security.jwt;

import com.example.liber_cinema.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);

            // Szczegółowe logowanie dla debugowania
            System.out.println("Request URI: " + request.getRequestURI());
            System.out.println("JWT from request: " + jwt);

            if (jwt != null) {
                try {
                    boolean isValid = jwtUtils.validateJwtToken(jwt);
                    System.out.println("JWT validation result: " + isValid);
                    
                    if (isValid) {
                        String username = jwtUtils.getUserNameFromJwtToken(jwt);
                        System.out.println("Username from JWT: " + username);

                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("Authentication set in SecurityContext for user: " + username);
                    } else {
                        System.out.println("JWT is invalid");
                    }
                } catch (Exception e) {
                    System.out.println("JWT validation exception: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("JWT not present in request");
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        System.out.println("Authorization header: " + headerAuth);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
