package com.mjwsolucoes.sistemanutricao.dto;

import lombok.Data;

/**
 * Filtros da busca de fichas técnicas. Os limites de macronutriente são
 * aplicados sobre o valor POR PORÇÃO, que é como a ficha é consumida.
 */
@Data
public class FichaFiltroDTO {
    private String termo;
    private String categoria;
    private String nutricionista;

    private Double proteinaMin;
    private Double proteinaMax;
    private Double carboidratoMin;
    private Double carboidratoMax;
    private Double lipidioMin;
    private Double lipidioMax;
    private Double sodioMin;
    private Double sodioMax;
    private Double kcalMin;
    private Double kcalMax;

    /** Por padrão as fichas arquivadas ficam fora das listas. */
    private boolean incluirArquivadas = false;
    /** Mostra apenas o que está arquivado — atalho do painel de arquivadas. */
    private boolean somenteArquivadas = false;

    /** nome | categoria | proteina | carboidrato | lipidio | kcal | porcoes */
    private String ordenarPor = "nome";
    private String direcao = "asc";

    public boolean temFiltroAtivo() {
        return (termo != null && !termo.isBlank())
                || (categoria != null && !categoria.isBlank())
                || (nutricionista != null && !nutricionista.isBlank())
                || proteinaMin != null || proteinaMax != null
                || carboidratoMin != null || carboidratoMax != null
                || lipidioMin != null || lipidioMax != null
                || sodioMin != null || sodioMax != null
                || kcalMin != null || kcalMax != null
                || incluirArquivadas || somenteArquivadas;
    }
}
