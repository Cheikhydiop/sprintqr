package com.dette.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/login")
@Api(value = "AuthController", description = "Opérations d'authentification pour se connecter et générer un JWT")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${jwt.secret}")
    private String secretKey;

    @PostMapping
    @ApiOperation(value = "Connexion d'un utilisateur et génération d'un JWT", response = ResponseEntity.class)
    public ResponseEntity<String> login(
            @ApiParam(value = "Données de connexion de l'utilisateur", required = true)
            @RequestBody AuthRequest authRequest) {
        System.out.println("Tentative de connexion pour l'utilisateur : " + authRequest.getUsername());

        if (authRequest.getUsername() == null || authRequest.getUsername().isEmpty() || 
            authRequest.getPassword() == null || authRequest.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Le nom d'utilisateur et le mot de passe ne doivent pas être vides");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            String token = generateToken(authentication);
            return ResponseEntity.ok("Utilisateur connecté avec succès : " + authRequest.getUsername() + ", Token : " + token);
        } catch (AuthenticationException e) {
            System.err.println("Échec de l'authentification : " + e.getMessage());
            return ResponseEntity.status(401).body("Nom d'utilisateur ou mot de passe invalide");
        } catch (Exception e) {
            System.err.println("Erreur lors de la connexion : " + e.getMessage());
            return ResponseEntity.status(500).body("Erreur interne du serveur");
        }
    }

    private String generateToken(Authentication authentication) {
        long expirationTime = 1000 * 60 * 60; // 1 heure
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationTime);

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", authentication.getName());
        claims.put("roles", authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));

        try {
            return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du token : " + e.getMessage());
            throw new RuntimeException("Erreur interne");
        }
    }

    public static class AuthRequest {
        private String username;
        private String password;

        // Getters et Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
