// src/main/java/com/mjwsolucoes/sistemanutricao/model/User.java
package com.mjwsolucoes.sistemanutricao.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    // varchar em vez do enum do MySQL: acrescentar um cargo novo não exige mexer no schema
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20, columnDefinition = "varchar(20)")
    private Role role;

    /** Contas nascem inativas: só liberam login depois que o admin aprova e define o cargo. */
    @Column(name = "ativo", nullable = false)
    private boolean ativo;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "nutricionista", cascade = CascadeType.ALL)
    private List<Receita> receitas;

    /** Nome de exibição: cai para o username quando o cadastro antigo não tem nome. */
    public String getNomeExibicao() {
        return (nome == null || nome.isBlank()) ? username : nome;
    }
}
