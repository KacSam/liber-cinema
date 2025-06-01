package com.example.liber_cinema.services;

import com.example.liber_cinema.models.User;
import com.example.liber_cinema.repositories.UserRepository;
import com.example.liber_cinema.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
      public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        System.out.println("Authentication object: " + authentication);
        
        if (authentication == null) {
            System.out.println("Authentication is null");
            return null;
        }
        
        if (!authentication.isAuthenticated()) {
            System.out.println("User is not authenticated");
            return null;
        }
        
        System.out.println("Authentication name: " + authentication.getName());
        System.out.println("Authentication principal type: " + 
                (authentication.getPrincipal() != null ? authentication.getPrincipal().getClass().getName() : "null"));
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            Long userId = ((UserDetailsImpl) principal).getId();
            System.out.println("Found user ID from principal: " + userId);
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                System.out.println("Found user in database: " + userOpt.get().getUsername());
                return userOpt.get();
            } else {
                System.out.println("User with ID " + userId + " not found in database");
                return null;
            }
        } else {
            System.out.println("Principal is not UserDetailsImpl: " + 
                    (principal != null ? principal.toString() : "null"));
            return null;
        }
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
