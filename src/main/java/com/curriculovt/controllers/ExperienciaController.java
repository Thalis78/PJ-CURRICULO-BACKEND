package com.curriculovt.controllers;

import com.curriculovt.dtos.ExperienciaDTO;
import com.curriculovt.models.Experiencia;
import com.curriculovt.services.ExperienciaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/experiencias")
public class ExperienciaController {

    private final ExperienciaService experienciaService;

    public ExperienciaController(ExperienciaService experienciaService) {
        this.experienciaService = experienciaService;
    }

    @PostMapping("/curriculo/{curriculoId}")
    public ResponseEntity<Experiencia> criar(
            @PathVariable Long curriculoId,
            @Valid @RequestBody ExperienciaDTO dto) {

        Experiencia salva = experienciaService.criar(curriculoId, dto);
        return ResponseEntity.status(201).body(salva);
    }

    @GetMapping("/curriculo/{curriculoId}")
    public ResponseEntity<List<Experiencia>> listarPorCurriculo(@PathVariable Long curriculoId) {
        return ResponseEntity.ok(experienciaService.listarPorCurriculo(curriculoId));
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