package com.mjwsolucoes.sistemanutricao.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingrediente {
    @Id
    private Long id;

    private String nome;
    private double proteina;
    private double carboidrato;
    private double lipidio;
    private double sodio;
    private double gorduraSaturada;

    // Getters e Setters
}