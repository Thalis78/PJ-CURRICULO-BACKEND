package com.curriculovt.controllers;

import com.curriculovt.dtos.HabilidadeDTO;
import com.curriculovt.models.Habilidade;
import com.curriculovt.services.HabilidadeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/habilidades")
public class HabilidadeController {

    private final HabilidadeService habilidadeService;

    public HabilidadeController(HabilidadeService habilidadeService) {
        this.habilidadeService = habilidadeService;
    }

    @PostMapping("/profile/{profileId}")
    public ResponseEntity<Habilidade> criar(
            @PathVariable Long profileId,
            @Valid @RequestBody HabilidadeDTO dto) {

        Habilidade salva = habilidadeService.criar(profileId, dto);
        return ResponseEntity.status(201).body(salva);
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<List<Habilidade>> listarPorProfile(@PathVariable Long profileId) {
        return ResponseEntity.ok(habilidadeService.listarPorProfile(profileId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Habilidade> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(habilidadeService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Habilidade> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody HabilidadeDTO dto) {

        Habilidade atualizada = habilidadeService.atualizar(id, dto);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        habilidadeService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}