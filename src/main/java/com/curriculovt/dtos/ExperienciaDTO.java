package com.curriculovt.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExperienciaDTO {

    @NotBlank(message = "O nome da empresa é obrigatório.")
    private String empresa;

    @NotBlank(message = "O cargo é obrigatório.")
    private String cargo;

    @NotNull(message = "A data de início é obrigatória.")
    private LocalDate dataInicio;

    private LocalDate dataFim;

    private boolean atual;

    private String descricao;
}