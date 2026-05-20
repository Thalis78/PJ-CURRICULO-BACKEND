package com.curriculovt.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailRequest {
    @NotBlank(message = "O destinatário é obrigatório")
    @Email(message = "E-mail inválido")
    private String destinatario;

    @NotBlank(message = "O assunto é obrigatório")
    private String assunto;

    @NotBlank(message = "O título principal é obrigatório")
    private String tituloPrincipal;

    @NotBlank(message = "O corpo do texto é obrigatório")
    private String textoCorpo;

    private String textoBotao;
    private String linkBotao;
}
