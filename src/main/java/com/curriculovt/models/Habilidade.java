package com.curriculovt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size; // Importação necessária
import lombok.*;

@Entity
@Table(name = "habilidades")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Habilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da habilidade é obrigatório.")
    @Size(max = 20, message = "O nome da habilidade deve ter no máximo 20 caracteres.")
    @Column(length = 20)
    private String nome;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
}