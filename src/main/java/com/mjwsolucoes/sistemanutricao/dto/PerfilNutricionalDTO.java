package com.mjwsolucoes.sistemanutricao.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PerfilNutricionalDTO {
    private Long id;

    @NotNull(message = "Per capita é obrigatório")
    @Positive(message = "Per capita deve ser positivo")
    private Integer perCapita;

    @NotNull(message = "Total de gramas é obrigatório")
    @Positive(message = "Total de gramas deve ser positivo")
    private Integer totalGramas;

    @NotNull(message = "Total de kcal é obrigatório")
    @Positive(message = "Total de kcal deve ser positivo")
    private Integer totalKcal;

    @NotNull(message = "Total em porcentagem é obrigatório")
    @Positive(message = "Total em porcentagem deve ser positivo")
    private Integer totalPorcentagem;

    @NotNull(message = "VCT é obrigatório")
    @Positive(message = "VCT deve ser positivo")
    private Integer vct;

    private Long receitaId;
}
