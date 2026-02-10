package com.curriculovt.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CurriculoNaoEncontradoException.class)
    public ResponseEntity<String> curriculoNaoEncontrado(CurriculoNaoEncontradoException e){
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<String>> handleValidation(ConstraintViolationException e){

        List<String> erros = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        return ResponseEntity.badRequest().body(erros);
    }
}
