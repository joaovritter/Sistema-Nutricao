package com.mjwsolucoes.sistemanutricao.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ReceitaIngredienteDTO {
    private Long id;

    @NotNull(message = "Medida caseira é obrigatória")
    @PositiveOrZero(message = "Medida caseira não pode ser negativa")
    private Integer medidaCaseira;

    @NotNull(message = "Peso bruto é obrigatório")
    @PositiveOrZero(message = "Peso bruto não pode ser negativo")
    private Double pesoBruto;

    @NotNull(message = "Peso líquido é obrigatório")
    @PositiveOrZero(message = "Peso líquido não pode ser negativo")
    private Double pesoLiquido;

    @NotNull(message = "Fator de correção é obrigatório")
    @PositiveOrZero(message = "Fator de correção não pode ser negativo")
    private Double fatorCorrecao;

    @NotNull(message = "Custo de compra é obrigatório")
    @PositiveOrZero(message = "Custo de compra não pode ser negativo")
    private Double custoCompra;

    @NotNull(message = "Custo utilizado é obrigatório")
    @PositiveOrZero(message = "Custo utilizado não pode ser negativo")
    private Double custoUtilizado;

    @NotNull(message = "Custo total é obrigatório")
    @PositiveOrZero(message = "Custo total não pode ser negativo")
    private Double custoTotal;

    @NotNull(message = "Custo per capita é obrigatório")
    @PositiveOrZero(message = "Custo per capita não pode ser negativo")
    private Double custoPercapita;

    private Long receitaId;
    private Long ingredienteId;
    private String ingredienteNome;
    private Long ingredienteNutricionistaId;
    private String ingredienteNutricionistaNome;
}