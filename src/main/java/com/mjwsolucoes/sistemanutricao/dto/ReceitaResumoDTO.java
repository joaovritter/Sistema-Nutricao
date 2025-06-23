package com.mjwsolucoes.sistemanutricao.dto;

import lombok.Data;

@Data
public class ReceitaResumoDTO {
    private Long id;
    private String nome;
    private String categoria;
    private Integer tempoPreparo;
    private Integer numeroPorcoes;
    private Double totalKcal; // <-- MUDAR AQUI PARA DOUBLE
    private String nutricionistaUsername;

    // Com o @Data do Lombok, você não precisa escrever o setter explicitamente.
    // Ele já será gerado como: public void setTotalKcal(Double totalKcal) { this.totalKcal = totalKcal; }
    // Pode remover o setter manual se o @Data estiver funcionando.
}