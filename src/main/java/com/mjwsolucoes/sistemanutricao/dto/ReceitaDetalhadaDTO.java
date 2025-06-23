package com.mjwsolucoes.sistemanutricao.dto;

import lombok.Data;

import java.util.List;

@Data // do Lombok
public class ReceitaDetalhadaDTO {
    private ReceitaDTO receita; // Contém nome, categoria, modoPreparo, etc.
    private List<IngredienteInfoDTO> ingredientes; // Contém nome, quantidade, medida, custoPorcao, etc.
    private PerfilNutricionalDTO perfilNutricional; // Contém perCapita, totalGramas, totalKcal, etc.
}