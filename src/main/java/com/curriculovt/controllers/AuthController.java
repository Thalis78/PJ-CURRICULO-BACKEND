package com.curriculovt.controllers;

import com.curriculovt.dtos.LoginDTO;
import com.curriculovt.models.User;
import com.curriculovt.services.TokenService;
import com.curriculovt.services.UserService;
import com.curriculovt.services.EnviarEmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EnviarEmailService enviarEmailService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO data) {
        Optional<User> userOptional = userService.findByEmail(data.getEmail().toLowerCase().trim());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(data.getPassword(), user.getPassword())) {

                if (user.getDataExpiracao() != null && user.getDataExpiracao().isBefore(LocalDateTime.now())) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("mensagem", "Sua assinatura expirou.");
                    errorResponse.put("status", "EXPIRADO");
                    errorResponse.put("dataExpiracao", user.getDataExpiracao());
                    return ResponseEntity.status(403).body(errorResponse);
                }

                String token = tokenService.generateToken(user);

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("id", user.getId());
                response.put("nome", user.getNome());
                response.put("email", user.getEmail());
                response.put("role", user.getRole());
                response.put("dataExpiracao", user.getDataExpiracao());
                response.put("senhaRedefinidaPorEmail", user.isSenhaRedefinidaPorEmail());

                return ResponseEntity.ok(response);
            }
        }

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("mensagem", "E-mail ou senha inválidos");
        return ResponseEntity.status(401).body(errorResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isBlank()) {
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "O e-mail é obrigatório.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            enviarEmailService.resetarSenha(email.toLowerCase().trim());
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Uma senha temporaria foi enviada para " + email);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", e.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }
}