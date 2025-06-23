// src/main/java/com/mjwsolucoes/sistemanutricao/dto/PerfilNutricionalDTO.java
package com.mjwsolucoes.sistemanutricao.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero; // Alterado para PositiveOrZero
import lombok.Data;

@Data
public class PerfilNutricionalDTO {
    private Long id;

    @NotNull(message = "Per capita é obrigatório")
    @PositiveOrZero(message = "Per capita não pode ser negativo")
    private Double perCapita; // MUDADO PARA Double

    @NotNull(message = "Total de gramas é obrigatório")
    @PositiveOrZero(message = "Total de gramas não pode ser negativo")
    private Double totalGramas; // MUDADO PARA Double

    @NotNull(message = "Total de kcal é obrigatório")
    @PositiveOrZero(message = "Total de kcal não pode ser negativo")
    private Double totalKcal; // MUDADO PARA Double

    @NotNull(message = "Total em porcentagem é obrigatório")
    @PositiveOrZero(message = "Total em porcentagem não pode ser negativo")
    private Double totalPorcentagem; // MUDADO PARA Double

    @NotNull(message = "VCT é obrigatório")
    @PositiveOrZero(message = "VCT não pode ser negativo")
    private Double vct; // MUDADO PARA Double

    private Long receitaId; // ID da receita associada
}