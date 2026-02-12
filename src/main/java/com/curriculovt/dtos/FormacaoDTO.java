package com.curriculovt.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class FormacaoDTO {

    @NotBlank(message = "A instituição é obrigatória.")
    private String instituicao;

    @NotBlank(message = "O curso é obrigatório.")
    private String curso;

    @NotBlank(message = "O tipo de formação é obrigatório (Ex: Superior, Técnico, Avulso).")
    private String tipo;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private boolean concluido;

    private String descricao;
}