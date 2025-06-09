package com.mjwsolucoes.sistemanutricao.dto;

import lombok.Data;

@Data
public class NutricionistaDTO {
    private Long id;
    private String username;
    private String role;
    private int totalReceitas;
    private int totalIngredientesCriados;
}
