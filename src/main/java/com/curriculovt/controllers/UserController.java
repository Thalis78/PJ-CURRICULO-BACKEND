package com.curriculovt.controllers;

import com.curriculovt.models.User;
import com.curriculovt.models.UserRole;
import com.curriculovt.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
    }

    @PostMapping("/ativar-assinatura")
    public ResponseEntity<Void> ativarAssinatura(@RequestBody Map<String, Object> payload) {
        Object idObj = payload.get("usuarioId");

        if (idObj == null) {
            return ResponseEntity.badRequest().build();
        }

        Long usuarioId = Long.parseLong(String.valueOf(idObj));
        userService.adicionarMesDeAssinatura(usuarioId);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) UserRole role,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(filtro, role, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return ResponseEntity.ok(userService.getMetrics(year, month));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        return ResponseEntity.ok(userService.update(id, userDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}