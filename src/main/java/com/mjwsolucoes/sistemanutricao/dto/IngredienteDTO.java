// src/main/java/com/mjwsolucoes/sistemanutricao/dto/IngredienteDTO.java
package com.mjwsolucoes.sistemanutricao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class IngredienteDTO {
    private Long id;

    @NotBlank(message = "Nome do ingrediente é obrigatório")
    private String nome;

    @NotNull(message = "Proteína é obrigatória")
    @PositiveOrZero(message = "Proteína não pode ser negativa")
    private Double proteina;

    @NotNull(message = "Carboidrato é obrigatório")
    @PositiveOrZero(message = "Carboidrato não pode ser negativo")
    private Double carboidrato;

    @NotNull(message = "Lipídio é obrigatório")
    @PositiveOrZero(message = "Lipídio não pode ser negativo")
    private Double lipidio;

    @NotNull(message = "Sódio é obrigatório")
    @PositiveOrZero(message = "Sódio não pode ser negativo")
    private Double sodio;

    @NotNull(message = "Gordura saturada é obrigatória")
    @PositiveOrZero(message = "Gordura saturada não pode ser negativa")
    private Double gorduraSaturada;

    // Não inclui campos de nutricionista, pois este DTO é para ingredientes gerais/sistema
}