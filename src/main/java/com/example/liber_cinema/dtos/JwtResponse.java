package com.example.liber_cinema.dtos;

import lombok.Data;

@Data
public class JwtResponse {
    private String token; // Upewnij się, że pole nazywa się "token", nie "accessToken"
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;

    public JwtResponse(String accessToken, Long id, String username, String email) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
    }
    
    @Override
    public String toString() {
        return "JwtResponse{" +
                "token='" + (token != null ? token.substring(0, Math.min(10, token.length())) + "..." : null) + '\'' +
                ", type='" + type + '\'' +
                ", id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
