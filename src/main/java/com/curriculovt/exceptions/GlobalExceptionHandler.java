package com.curriculovt.exceptions;

import com.curriculovt.dtos.ErroResponseDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CurriculoNaoEncontradoException.class)
    public ResponseEntity<ErroResponseDTO> curriculoNaoEncontrado(CurriculoNaoEncontradoException e) {
        ErroResponseDTO erro = new ErroResponseDTO(404, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroResponseDTO> handleValidation(ConstraintViolationException e) {
        String primeiraMensagem = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("Erro de validação");

        ErroResponseDTO erro = new ErroResponseDTO(400, primeiraMensagem);

        return ResponseEntity.badRequest().body(erro);
    }
}