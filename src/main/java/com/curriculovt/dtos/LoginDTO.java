package com.curriculovt.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {

    @NotBlank(message = "O usuário não pode estar vazio")
    private String username;

    @NotBlank(message = "A senha não pode estar vazia")
    private String password;

}