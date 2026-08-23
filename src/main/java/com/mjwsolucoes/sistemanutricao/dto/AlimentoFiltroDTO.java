package com.mjwsolucoes.sistemanutricao.dto;

import lombok.Data;

/** Critérios da busca de alimentos. Todo campo nulo significa "não filtrar por isso". */
@Data
public class AlimentoFiltroDTO {
    private String termo;

    /** TODOS | TACO | PERSONALIZADO */
    private String origem = "TODOS";

    private Double proteinaMin;
    private Double proteinaMax;
    private Double carboidratoMin;
    private Double carboidratoMax;
    private Double lipidioMin;
    private Double lipidioMax;
    private Double sodioMin;
    private Double sodioMax;
    private Double gorduraSaturadaMin;
    private Double gorduraSaturadaMax;
    private Double kcalMin;
    private Double kcalMax;

    /** Página pedida, base 1. */
    private int pagina = 1;

    /** Itens por página; zero ou negativo devolve tudo de uma vez. */
    private int tamanhoPagina = 24;

    /** nome | proteina | carboidrato | lipidio | sodio | gorduraSaturada | kcal */
    private String ordenarPor = "nome";
    /** asc | desc */
    private String direcao = "asc";

    /** Indica se o usuário aplicou algum filtro além da ordenação padrão. */
    public boolean temFiltroAtivo() {
        return (termo != null && !termo.isBlank())
                || (origem != null && !"TODOS".equals(origem))
                || proteinaMin != null || proteinaMax != null
                || carboidratoMin != null || carboidratoMax != null
                || lipidioMin != null || lipidioMax != null
                || sodioMin != null || sodioMax != null
                || gorduraSaturadaMin != null || gorduraSaturadaMax != null
                || kcalMin != null || kcalMax != null;
    }
}
