package com.curriculovt.exceptions;

import com.curriculovt.dtos.ErroResponseDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            ProfileNaoEncontradoException.class,
            FormacaoNaoEncontradaException.class,
            ExperienciaNaoEncontradaException.class,
            HabilidadeNaoEncontradaException.class,
            IdiomaNaoEncontradoException.class,
            UserNaoEncontradoException.class
    })
    public ResponseEntity<ErroResponseDTO> handleNotFound(RuntimeException e) {
        logger.warn("Recurso não encontrado: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroResponseDTO(404, e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErroResponseDTO> handleBusinessRule(IllegalStateException e) {
        logger.warn("Regra de negócio violada: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroResponseDTO(400, e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponseDTO> handleAccessDenied(AccessDeniedException e) {
        logger.warn("Tentativa de acesso não autorizado: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErroResponseDTO(403, "Acesso negado: Você não tem permissão para esta ação."));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroResponseDTO> handleValidation(ConstraintViolationException e) {
        String primeiraMensagem = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("Erro de validação");

        return ResponseEntity.badRequest().body(new ErroResponseDTO(400, primeiraMensagem));
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponseDTO> handleMethodArgumentNotValid(org.springframework.web.bind.MethodArgumentNotValidException e) {
        String mensagem = e.getBindingResult().getFieldErrors()
                .stream()
                .map(org.springframework.validation.FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Erro de validação nos dados");

        return ResponseEntity.badRequest().body(new ErroResponseDTO(400, mensagem));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponseDTO> handleDataIntegrity(DataIntegrityViolationException e) {
        logger.error("Erro de integridade no banco: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErroResponseDTO(400, "Os dados enviados são inválidos ou já existem no sistema."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponseDTO> handleInvalidJson(HttpMessageNotReadableException e) {
        logger.warn("JSON enviado com erro de sintaxe.");
        return ResponseEntity.badRequest()
                .body(new ErroResponseDTO(400, "Erro na leitura do JSON. Verifique a sintaxe."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponseDTO> handleGeneralException(Exception e) {
        logger.error("ERRO NÃO TRATADO: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErroResponseDTO(500, "Ocorreu um erro interno no servidor."));
    }
}