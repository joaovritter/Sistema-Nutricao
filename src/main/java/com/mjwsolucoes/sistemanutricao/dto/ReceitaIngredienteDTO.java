package com.mjwsolucoes.sistemanutricao.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ReceitaIngredienteDTO {
    private int receitaId;
    private int ingredienteId;

    @NotNull(message = "Medida caseira é obrigatória")
    @PositiveOrZero(message = "Medida caseira não pode ser somente numeros")
    private String medidaCaseira;

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

    @NotNull(message = "Peso da compra é obrigatório")
    @PositiveOrZero(message = "Peso da compra não pode ser negativo")
    private Double pesoCompra;

    @NotNull(message = "Custo utilizado é obrigatório")
    @PositiveOrZero(message = "Custo utilizado não pode ser negativo")
    private Double custoUtilizado;

    @NotNull(message = "Custo total é obrigatório")
    @PositiveOrZero(message = "Custo total não pode ser negativo")
    private Double custoTotal;

    @NotNull(message = "Custo per capita é obrigatório")
    @PositiveOrZero(message = "Custo per capita não pode ser negativo")
    private Double custoPercapita;


    private String ingredienteNome;
    private Long userId;
}