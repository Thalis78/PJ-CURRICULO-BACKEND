package com.curriculovt.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ProfileDTO {

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    private String img;

    private String resumo;

    @NotBlank(message = "O objetivo é obrigatório.")
    private String objetivo;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    private String email;

    @NotBlank(message = "O telefone é obrigatório.")
    @Size(min = 10, max = 15, message = "Telefone inválido.")
    private String telefone;

    private String linkedin;

    @NotBlank(message = "O estado é obrigatório.")
    @Size(min = 2, max = 2, message = "Informe apenas a sigla do estado (ex: SP).")
    @Pattern(regexp = "[A-Z]{2}", message = "O estado deve conter 2 letras maiúsculas.")
    private String estado;

}
