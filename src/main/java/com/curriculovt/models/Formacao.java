package com.curriculovt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "formacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Formacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A instituição é obrigatória.")
    private String instituicao;

    @NotBlank(message = "O nome do curso é obrigatório.")
    private String curso;

    @NotBlank(message = "Informe o tipo (Ex: Superior, Técnico, Curso Avulso).")
    private String tipo;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    @Column(nullable = false)
    private Boolean atualmente = false;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
}