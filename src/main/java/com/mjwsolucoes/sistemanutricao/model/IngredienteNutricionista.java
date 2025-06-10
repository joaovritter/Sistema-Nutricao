package com.mjwsolucoes.sistemanutricao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredienteNutricionista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private double carboidrato;
    private double proteina;
    private double lipido;
    private double sodio;
    private double gorduraSaturada;

    @ManyToOne
    @JoinColumn(name = "nutricionista_id")
    private User nutricionista;

    // Getters e Setters
}