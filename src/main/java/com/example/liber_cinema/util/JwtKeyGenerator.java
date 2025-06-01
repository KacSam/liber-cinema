package com.example.liber_cinema.util;

import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import javax.crypto.SecretKey;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        // Generate a secure key specifically for HS512 algorithm
        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
        
        // Convert the key to Base64 encoded string
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        
        System.out.println("Generated JWT Secret Key (Base64):");
        System.out.println(base64Key);
        System.out.println("Key Length: " + base64Key.length());
    }
}
