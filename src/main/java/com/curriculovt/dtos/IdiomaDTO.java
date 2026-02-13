package com.curriculovt.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdiomaDTO {

    @NotBlank(message = "O nome do idioma é obrigatório.")
    private String nome;

    @NotBlank(message = "O nível de proficiência é obrigatório.")
    private String nivel;
}