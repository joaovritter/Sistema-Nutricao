package com.mjwsolucoes.sistemanutricao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoriaResumoDTO {
    private String nome;
    private long quantidade;
    private int percentual; // 0-100, relativo à categoria com mais fichas — usado na barra da dashboard
}
