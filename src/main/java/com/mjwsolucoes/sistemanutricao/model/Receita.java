package com.mjwsolucoes.sistemanutricao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaReceita categoria;

    @Column(nullable = false, columnDefinition = "TEXT") // Pode ser um texto mais longo
    private String modoPreparo;

    @Column(nullable = false)
    private Integer tempoPreparo; // Continua Integer se for em minutos inteiros

    @Column(nullable = false)
    private Double pesoPorcao; // Corrigido para Double

    @Column(nullable = false)
    private Double rendimento; // Corrigido para Double

    @Column(nullable = false)
    private String equipamentos; // Corrigido para minúscula e nullable = false

    @Column(nullable = false)
    private Integer numeroPorcoes;

    @Column(nullable = false)
    private Double fcc; // Corrigido para Double

    @Column(nullable = false)
    private String medidaCaseira;

    @ManyToOne
    @JoinColumn(name = "nutricionista_id", nullable = false)
    private User nutricionista;

    @OneToOne(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true)
    private PerfilNutricional perfilNutricional;

    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceitaIngrediente> ingredientesReceita = new ArrayList<>(); // Renomeado para clareza
}