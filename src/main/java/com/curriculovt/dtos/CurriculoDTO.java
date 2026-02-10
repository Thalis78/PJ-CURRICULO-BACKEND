package com.curriculovt.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class CurriculoDTO {

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    private String sobrenome;

    private String img;

    private Integer idade;

    private String resumo;

    @NotBlank(message = "O objetivo é obrigatório.")
    private String objetivo;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    private String email;

    @NotBlank(message = "O telefone é obrigatório.")
    @Size(min = 10, max = 15, message = "Telefone inválido.")
    private String telefone;


}
