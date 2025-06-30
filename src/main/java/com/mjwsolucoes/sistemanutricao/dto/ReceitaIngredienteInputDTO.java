// src/main/java/com/mjwsolucoes/sistemanutricao/dto/ReceitaIngredienteInputDTO.java
package com.mjwsolucoes.sistemanutricao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ReceitaIngredienteInputDTO {
    // ID do ingrediente existente no banco de dados (obtido do autocomplete no frontend)
    @NotNull(message = "ID do ingrediente é obrigatório para associação")
    private Long ingredienteId;

    // O nome do ingrediente é opcional aqui, mas pode ser útil para validação ou logging
    private String ingredienteNome;

    @NotBlank(message = "Medida caseira é obrigatória")
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

    // Dados nutricionais do ingrediente (para cálculos do perfil nutricional)
    private Double proteina;
    private Double carboidrato;
    private Double lipidio;
    private Double sodio;
    private Double gorduraSaturada;

    // Campos como custoTotal e custoPercapita são calculados no backend para a entidade ReceitaIngrediente
    // ou são totais da Receita/PerfilNutricional, então não são necessários aqui para INPUT.
}