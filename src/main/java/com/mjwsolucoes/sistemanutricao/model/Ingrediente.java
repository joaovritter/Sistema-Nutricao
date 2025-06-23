// src/main/java/com/mjwsolucoes/sistemanutricao/model/Ingrediente.java
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
public class Ingrediente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Double proteina;

    @Column(nullable = false)
    private Double carboidrato;

    @Column(nullable = false)
    private Double lipidio;

    @Column(nullable = false)
    private Double sodio;

    @Column(nullable = false)
    private Double gorduraSaturada;

    @Column(nullable = false)
    private boolean isIngredienteSistema; // true= do sistema, false= de nutricionista

    @ManyToOne(fetch = FetchType.LAZY) // Um nutricionista pode ter vários ingredientes. Pode ser nulo para ingredientes do sistema.
    @JoinColumn(name = "nutricionista_id") // Nome da coluna da chave estrangeira
    private User nutricionista; // O nutricionista que criou este ingrediente (se não for do sistema)

    @OneToMany(mappedBy = "ingrediente", cascade = CascadeType.ALL, orphanRemoval = true) // Associações de ReceitaIngrediente
    private List<ReceitaIngrediente> receitasAssociadas = new ArrayList<>();
}