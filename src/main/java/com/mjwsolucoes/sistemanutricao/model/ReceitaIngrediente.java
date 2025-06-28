package com.mjwsolucoes.sistemanutricao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@IdClass(Receita_Ingrediente_Id.class)
@NoArgsConstructor
@AllArgsConstructor
public class ReceitaIngrediente {

    @Id
    @Column(name = "receita_id") // Nome da coluna no banco
    private Long receitaId;

    @Id
    @Column(name = "ingrediente_id") // Nome da coluna no banco
    private Long ingredienteId;

    @ManyToOne
    @JoinColumn(name = "receita_id", referencedColumnName = "id", insertable = false, updatable = false) // Mapeia o ID para o objeto
    private Receita receita;

    @ManyToOne
    @JoinColumn(name = "ingrediente_id", referencedColumnName = "id", insertable = false, updatable = false) // Mapeia o ID para o objeto
    private Ingrediente ingrediente;

    @Column(nullable = false)
    private String medidaCaseira;

    @Column(nullable = false)
    private Double pesoBruto;

    @Column(nullable = false)
    private Double pesoLiquido;

    @Column(nullable = false)
    private Double fatorCorrecao;

    @Column(nullable = false)
    private Double custoCompra;

    @Column(nullable = false)
    private Double pesoCompra;

    @Column(nullable = false)
    private Double custoUtilizado;

    @Column(nullable = false)
    private Double custoTotal;

    @Column(nullable = false)
    private Double custoPercapita;
}