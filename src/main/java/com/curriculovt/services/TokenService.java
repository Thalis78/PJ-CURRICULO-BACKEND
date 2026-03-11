package com.curriculovt.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.curriculovt.models.User;
import com.curriculovt.models.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

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
                    .withExpiresAt(genExpirationDate(user))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    private Instant genExpirationDate(User user) {
        if (user.getDataExpiracao() != null && user.getDataExpiracao().isBefore(LocalDateTime.now())) {
            return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
        }

        return LocalDateTime.now().with(LocalTime.MAX).toInstant(ZoneOffset.of("-03:00"));
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