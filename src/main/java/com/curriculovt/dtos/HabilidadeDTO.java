package com.curriculovt.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HabilidadeDTO {
    @NotBlank(message = "O nome é obrigatório.")
    private String nome;
    private String tipo;
}