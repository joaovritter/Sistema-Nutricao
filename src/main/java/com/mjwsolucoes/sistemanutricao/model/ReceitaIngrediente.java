package com.mjwsolucoes.sistemanutricao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceitaIngrediente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String medidaCaseira;
    private double pesoBruto;
    private double pesoLiquido;
    private double fatorCorrecao;
    private double custoCompra;
    private double custoUtilizado;
    private double custoTotal;
    private double custoPercapita;

    @ManyToOne
    @JoinColumn(name = "receita_id")
    private Receita receita;

    @ManyToOne
    @JoinColumn(name = "ingrediente_id")
    private Ingrediente ingrediente;

    @ManyToOne
    @JoinColumn(name = "ingrediente_nutricionista_id")
    private IngredienteNutricionista ingredienteNutricionista;

    // Getters e Setters
}