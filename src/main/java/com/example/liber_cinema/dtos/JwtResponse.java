package com.example.liber_cinema.dtos;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;    public JwtResponse(String accessToken, String refreshToken, Long id, String username, String email) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
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
