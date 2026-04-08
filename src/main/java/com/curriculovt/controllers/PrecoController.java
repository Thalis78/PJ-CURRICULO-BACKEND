package com.curriculovt.controllers;

import com.curriculovt.models.Preco;
import com.curriculovt.services.PrecoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/precos")
public class PrecoController {

    @Autowired
    private PrecoService precoService;

    @GetMapping
    public ResponseEntity<Preco> buscarConfiguracao() {
        return ResponseEntity.ok(precoService.buscarConfiguracao());
    }

    @PostMapping
    public ResponseEntity<Preco> salvarOuAtualizar(@Valid @RequestBody Preco preco) {
        Preco salvo = precoService.criarOuAtualizar(preco);
        return ResponseEntity.status(201).body(salvo);
    }

    @PutMapping
    public ResponseEntity<Preco> atualizar(@Valid @RequestBody Preco preco) {
        Preco atualizado = precoService.editar(preco);
        return ResponseEntity.ok(atualizado);
    }
}