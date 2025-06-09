package com.mjwsolucoes.sistemanutricao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String categoria;
    private String modoPreparo;
    private int tempoPreparo;
    private int pesoPorcao;
    private int rendimento;
    private int numeroPorcoes;

    @ManyToOne
    @JoinColumn(name = "nutricionista_id")
    private Nutricionista nutricionista;

    @OneToOne(mappedBy = "receita", cascade = CascadeType.ALL)
    private PerfilNutricional perfilNutricional;

    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL)
    private List<ReceitaIngrediente> ingredientes;

    // Getters e Setters
}