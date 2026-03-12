package com.curriculovt.controllers;

import com.curriculovt.dtos.FormacaoDTO;
import com.curriculovt.models.Formacao;
import com.curriculovt.services.FormacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/formacoes")
public class FormacaoController {

    @Autowired
    private FormacaoService formacaoService;

    @PostMapping("/profile/{profileId}")
    public ResponseEntity<Formacao> criar(
            @PathVariable Long profileId,
            @Valid @RequestBody FormacaoDTO dto) {

        Formacao salva = formacaoService.criar(profileId, dto);
        return ResponseEntity.status(201).body(salva);
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<List<Formacao>> listarPorProfile(@PathVariable Long profileId) {
        return ResponseEntity.ok(formacaoService.listarPorProfile(profileId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Formacao> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(formacaoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Formacao> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody FormacaoDTO dto) {

        Formacao atualizada = formacaoService.atualizar(id, dto);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        formacaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}