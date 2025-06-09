package com.mjwsolucoes.sistemanutricao.dto;

import lombok.Data;

@Data
public class ReceitaResumoDTO {
    private Long id;
    private String nome;
    private String categoria;
    private Integer tempoPreparo;
    private Integer numeroPorcoes;
    private Integer totalKcal;
    private String nutricionistaUsername;

    // Certifique-se de ter o setter para totalKcal
    public void setTotalKcal(Integer totalKcal) {
        this.totalKcal = totalKcal;
    }
}