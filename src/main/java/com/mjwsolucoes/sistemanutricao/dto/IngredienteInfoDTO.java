package com.mjwsolucoes.sistemanutricao.dto;

import lombok.Data;

@Data
public class IngredienteInfoDTO {
    private Long id;
    private String nome;
    private String tipo; // "ORIGINAL" ou "NUTRICIONISTA"
    private Double quantidade;
    private String medida;
    private Double custoPorcao;
}