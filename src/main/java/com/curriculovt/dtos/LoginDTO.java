package com.curriculovt.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {

    @NotBlank(message = "O e-mail não pode estar vazio")
    @Email(message = "O e-mail deve ser válido")
    private String email;

    @NotBlank(message = "A senha não pode estar vazia")
    private String password;

}