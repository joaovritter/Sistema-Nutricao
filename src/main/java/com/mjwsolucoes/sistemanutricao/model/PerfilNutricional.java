package com.mjwsolucoes.sistemanutricao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilNutricional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int perCapita;
    private int totalGramas;
    private int totalKcal;  // Verifique se o nome do campo está exatamente assim
    private int totalPorcentagem;
    private int vct;

    @OneToOne
    @JoinColumn(name = "receita_id")
    private Receita receita;


    public void ifPresent(Object o) {
    }

    // Getters e Setters
}