package com.curriculovt.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class UserAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String acao;
    private LocalDateTime dataEvento = LocalDateTime.now();

    public UserAuditLog() {}

    public UserAuditLog(Long userId, String acao) {
        this.userId = userId;
        this.acao = acao;
    }
}