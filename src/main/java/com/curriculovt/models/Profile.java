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
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campo nome não pode estar em branco.")
    @Column(nullable = false, length = 100)
    private String nome;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String img;

    @Column(length = 500)
    private String resumo;

    @NotBlank(message = "Por favor, informe o objetivo.")
    @Column(nullable = false, length = 255)
    private String objetivo;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    private String email;

    @NotBlank(message = "O telefone é obrigatório.")
    @Size(min = 10, max = 15, message = "Telefone inválido.")
    @Column(nullable = false, length = 15)
    private String telefone;

    @Column(length = 255)
    private String linkedin;

    @NotBlank(message = "O estado é obrigatório.")
    @Size(min = 2, max = 2, message = "Use a sigla do estado (ex: SP).")
    @Column(nullable = false, length = 2)
    private String estado;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experiencia> experiencias = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Formacao> formacoes = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Habilidade> habilidades = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Idioma> idiomas = new ArrayList<>();
}