package com.mjwsolucoes.sistemanutricao.dto;

import lombok.Data;

/** Filtros da busca de refeições. Macros somam uma porção de cada ficha da refeição. */
@Data
public class RefeicaoFiltroDTO {
    private String termo;
    /** Nome (ou parte) de uma ficha que a refeição precisa conter. */
    private String contemFicha;

    private Double proteinaMin;
    private Double proteinaMax;
    private Double carboidratoMin;
    private Double carboidratoMax;
    private Double lipidioMin;
    private Double lipidioMax;
    private Double kcalMin;
    private Double kcalMax;

    private boolean incluirArquivadas = false;
    private boolean somenteArquivadas = false;

    /** nome | fichas | proteina | carboidrato | lipidio | kcal */
    private String ordenarPor = "nome";
    private String direcao = "asc";

    public boolean temFiltroAtivo() {
        return (termo != null && !termo.isBlank())
                || (contemFicha != null && !contemFicha.isBlank())
                || proteinaMin != null || proteinaMax != null
                || carboidratoMin != null || carboidratoMax != null
                || lipidioMin != null || lipidioMax != null
                || kcalMin != null || kcalMax != null
                || incluirArquivadas || somenteArquivadas;
    }
}
