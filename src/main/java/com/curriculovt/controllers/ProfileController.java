package com.curriculovt.controllers;

import com.curriculovt.dtos.ProfileDTO;
import com.curriculovt.models.Profile;
import com.curriculovt.services.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profile> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Profile> criar(
            @Valid @RequestBody ProfileDTO dto) {

        Profile salvo = profileService.criar(dto);
        return ResponseEntity.status(201).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Profile> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProfileDTO dto) {

        Profile atualizado = profileService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        profileService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}