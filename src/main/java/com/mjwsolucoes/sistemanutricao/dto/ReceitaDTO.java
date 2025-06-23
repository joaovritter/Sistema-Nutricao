// src/main/java/com/mjwsolucoes/sistemanutricao/dto/ReceitaDTO.java
package com.mjwsolucoes.sistemanutricao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class ReceitaDTO {
    private Long id;

    @NotBlank(message = "Nome da receita é obrigatório")
    private String nome;

    @NotBlank(message = "Categoria é obrigatória")
    private String categoria; // Corresponde ao nome do Enum (ex: "ENTRADA")

    @NotBlank(message = "Equipamento é obrigatório")
    private String equipamentos;

    @NotBlank(message = "Modo de preparo é obrigatório")
    private String modoPreparo;

    @NotNull(message = "Tempo de preparo é obrigatório")
    @Positive(message = "Tempo de preparo deve ser positivo")
    private Integer tempoPreparo;

    @NotNull(message = "Peso da porção é obrigatório")
    @Positive(message = "Peso da porção deve ser positivo")
    private Double pesoPorcao; // Tipo Double no DTO

    @NotNull(message = "Rendimento é obrigatório")
    @Positive(message = "Rendimento deve ser positivo")
    private Double rendimento; // Tipo Double no DTO

    @NotNull(message = "Número de porções é obrigatório")
    @Positive(message = "Número de porções deve ser positivo")
    private Integer numeroPorcoes;

    private Double fcc; // Tipo Double no DTO
    private String medidaCaseira;

    // Estes campos para o username/userId do nutricionista são preenchidos no serviço, não enviados pelo frontend
    private Long userId; // Para resposta DTO, para que o frontend saiba o ID do nutricionista
    private String nutricionistaUsername; // Para resposta DTO, para que o frontend saiba o username do nutricionista

    private PerfilNutricionalDTO perfilNutricional;
    private List<ReceitaIngredienteInputDTO> ingredientes; // Lista do DTO de input para ingredientes
}