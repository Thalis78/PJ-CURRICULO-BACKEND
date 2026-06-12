package com.curriculovt.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.curriculovt.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class TokenService {

    @Value("${api.security.token.secret:secret-key-vt-2026}")
    private String secret;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("curriculo-vt")
                    .withSubject(user.getEmail())
                    .withClaim("role", "ROLE_" + user.getRole().toString())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(24).atZone(java.time.ZoneId.systemDefault()).toInstant();
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("curriculo-vt")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (Exception exception) {
            return null;
        }
    }
}