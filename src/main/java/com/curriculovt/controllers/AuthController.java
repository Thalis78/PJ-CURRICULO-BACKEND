package com.curriculovt.controllers;

import com.curriculovt.dtos.LoginDTO;
import com.curriculovt.models.User;
import com.curriculovt.services.TokenService;
import com.curriculovt.services.UserService;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO data) {
        Optional<User> userOptional = userService.findByUsername(data.getUsername().toLowerCase().trim());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getDataExpiracao() != null && user.getDataExpiracao().isBefore(LocalDateTime.now())) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("mensagem", "Sua assinatura expirou em " + user.getDataExpiracao() + ". Renove seu plano.");
                return ResponseEntity.status(403).body(errorResponse);
            }

            if (passwordEncoder.matches(data.getPassword(), user.getPassword())) {
                String token = tokenService.generateToken(user);

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("id", user.getId());
                response.put("username", user.getUsername());
                response.put("role", user.getRole());
                response.put("dataExpiracao", user.getDataExpiracao());

                return ResponseEntity.ok(response);
            }
        }

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("mensagem", "Usuário ou senha inválidos");

        return ResponseEntity.status(401).body(errorResponse);
    }
}