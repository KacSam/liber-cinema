package com.example.liber_cinema.controllers;

import com.example.liber_cinema.dtos.JwtResponse;
import com.example.liber_cinema.dtos.LoginRequest;
import com.example.liber_cinema.dtos.MessageResponse;
import com.example.liber_cinema.dtos.SignupRequest;
import com.example.liber_cinema.models.User;
import com.example.liber_cinema.repositories.UserRepository;
import com.example.liber_cinema.security.jwt.JwtUtils;
import com.example.liber_cinema.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("Login request received for user: " + loginRequest.getUsername());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            System.out.println("Login successful for user: " + userDetails.getUsername());
            System.out.println("Generated token length: " + jwt.length());
            System.out.println("Token starts with: " + jwt.substring(0, Math.min(10, jwt.length())));

            // Wydrukujmy obiekt odpowiedzi, aby upewnić się, że struktura jest poprawna
            JwtResponse response = new JwtResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail());
            
            System.out.println("JWT Response object: " + response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Login failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(encoder.encode(signupRequest.getPassword()));

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
