package com.curriculovt.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HabilidadeDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 20, message = "O nome da habilidade deve ter no máximo 20 caracteres.")
    private String nome;

}