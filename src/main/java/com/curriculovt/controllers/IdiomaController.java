package com.curriculovt.controllers;

import com.curriculovt.dtos.IdiomaDTO;
import com.curriculovt.models.Idioma;
import com.curriculovt.services.IdiomaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/idiomas")
public class IdiomaController {

    @Autowired
    private IdiomaService idiomaService;

    @PostMapping("/profile/{profileId}")
    public ResponseEntity<Idioma> criar(
            @PathVariable Long profileId,
            @Valid @RequestBody IdiomaDTO dto) {

        Idioma salvo = idiomaService.criar(profileId, dto);
        return ResponseEntity.status(201).body(salvo);
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<List<Idioma>> listarPorProfile(@PathVariable Long profileId) {
        return ResponseEntity.ok(idiomaService.listarPorProfile(profileId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Idioma> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(idiomaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Idioma> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody IdiomaDTO dto) {

        Idioma atualizado = idiomaService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        idiomaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}