package com.curriculovt.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"profiles", "password", "authorities"}) // Evita o loop e esconde a senha
    private User user;

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
    @Column(nullable = false)
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