package com.curriculovt.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "curriculos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Curriculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campo nome não pode estar em branco.")
    @Column(nullable = false, length = 100)
    private String nome;

    private String img;

    @Column(length = 500)
    private String resumo;

    @NotBlank(message = "Por favor, informe o objetivo.")
    @Column(nullable = false, length = 255)
    private String objetivo;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "O telefone é obrigatório.")
    @Size(min = 10, max = 15, message = "Telefone inválido.")
    @Column(nullable = false, length = 15)
    private String telefone;

    @OneToMany(mappedBy = "curriculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experiencia> experiencias = new ArrayList<>();
}
