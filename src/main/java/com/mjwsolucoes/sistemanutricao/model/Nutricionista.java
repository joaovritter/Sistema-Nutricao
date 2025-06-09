package com.mjwsolucoes.sistemanutricao.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Nutricionista {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    // Relacionamentos
    @OneToMany(mappedBy = "nutricionista", cascade = CascadeType.ALL)
    private List<Receita> receitas;

    @OneToMany(mappedBy = "nutricionista", cascade = CascadeType.ALL)
    private List<IngredienteNutricionista> ingredientesCriados;
}