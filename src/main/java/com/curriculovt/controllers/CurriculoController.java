package com.curriculovt.controllers;

import com.curriculovt.dtos.CurriculoDTO;
import com.curriculovt.models.Curriculo;
import com.curriculovt.services.CurriculoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/curriculos")
public class CurriculoController {

    private final CurriculoService curriculoService;

    public CurriculoController(CurriculoService curriculoService) {
        this.curriculoService = curriculoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Curriculo> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(curriculoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Curriculo> criar(
            @Valid @RequestBody CurriculoDTO dto) {

        Curriculo salvo = curriculoService.criar(dto);
        return ResponseEntity.status(201).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Curriculo> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CurriculoDTO dto) {

        Curriculo atualizado = curriculoService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        curriculoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
