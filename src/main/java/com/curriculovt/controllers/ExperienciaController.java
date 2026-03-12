package com.curriculovt.controllers;

import com.curriculovt.dtos.ExperienciaDTO;
import com.curriculovt.models.Experiencia;
import com.curriculovt.services.ExperienciaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/experiencias")
public class ExperienciaController {

    @Autowired
    private ExperienciaService experienciaService;

    @PostMapping("/profile/{profileId}")
    public ResponseEntity<Experiencia> criar(
            @PathVariable Long profileId,
            @Valid @RequestBody ExperienciaDTO dto) {

        Experiencia salva = experienciaService.criar(profileId, dto);
        return ResponseEntity.status(201).body(salva);
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<List<Experiencia>> listarPorCurriculo(@PathVariable Long profileId) {
        return ResponseEntity.ok(experienciaService.listarPorProfile(profileId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Experiencia> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(experienciaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Experiencia> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ExperienciaDTO dto) {

        Experiencia atualizada = experienciaService.atualizar(id, dto);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        experienciaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}