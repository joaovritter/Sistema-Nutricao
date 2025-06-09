package com.mjwsolucoes.sistemanutricao.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReceitaDetalhadaDTO {
    private ReceitaDTO receita;
    private List<IngredienteInfoDTO> ingredientes;
    private PerfilNutricionalDTO perfilNutricional;

    public void setPerfilNutricional(PerfilNutricionalDTO perfilNutricional) {
        this.perfilNutricional = perfilNutricional;
    }
}