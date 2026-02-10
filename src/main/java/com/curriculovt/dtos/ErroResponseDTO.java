package com.curriculovt.dtos;

import lombok.Data;

@Data
public class ErroResponseDTO {
    private int status;
    private Long timestamp;
    private String mensagem;

    public ErroResponseDTO(int status, String mensagem) {
        this.status = status;
        this.mensagem = mensagem;
        this.timestamp = System.currentTimeMillis();
    }
}