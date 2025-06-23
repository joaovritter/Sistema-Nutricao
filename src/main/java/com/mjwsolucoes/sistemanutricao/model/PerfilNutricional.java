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

    @Column(nullable = false)
    private Double perCapita; // Corrigido para Double

    @Column(nullable = false)
    private Double totalGramas; // Corrigido para Double

    @Column(nullable = false)
    private Double totalKcal;  // Corrigido para Double

    @Column(nullable = false)
    private Double totalPorcentagem; // Corrigido para Double

    @Column(nullable = false)
    private Double vct; // Corrigido para Double

    @OneToOne
    @JoinColumn(name = "receita_id", unique = true, nullable = false) // Garante que uma Receita tenha apenas um PerfilNutricional
    private Receita receita;

    // Getters e Setters são gerados pelo Lombok (@Data), não precisa escrevê-los.
    // O método 'ifPresent(Object o)' foi removido pois não pertence a uma entidade.
}