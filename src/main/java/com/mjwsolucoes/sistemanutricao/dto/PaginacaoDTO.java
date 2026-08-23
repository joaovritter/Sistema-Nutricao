package com.mjwsolucoes.sistemanutricao.dto;

import lombok.Data;

import java.util.List;

/**
 * Estado da paginação de uma listagem.
 *
 * 'paginas' é a janela de números mostrada na trilha (no máximo
 * {@code JANELA} itens, centrada na página atual) e 'posicaoNaJanela' diz em
 * qual delas o marcador da trilha deve parar.
 */
@Data
public class PaginacaoDTO {

    /** Quantos números de página cabem na trilha ao mesmo tempo. */
    public static final int JANELA = 5;

    private int paginaAtual;
    private int totalPaginas;
    private int tamanhoPagina;
    private long totalItens;

    /** Faixa exibida, em base 1, para o texto "X–Y de Z". */
    private long primeiroItem;
    private long ultimoItem;

    private List<Integer> paginas;
    private int posicaoNaJanela;

    private boolean temAnterior;
    private boolean temProxima;
    private int anterior;
    private int proxima;

    /**
     * Filtros ativos já codificados como query string, terminando em "&" quando
     * não vazia. Os links de página concatenam "pagina=N" para não perder a busca.
     */
    private String queryBase;

    /** Rota da listagem paginada, ex.: "/alimentos" ou "/admin/logs". */
    private String basePath = "/alimentos";

    /** Sem mais de uma página não há o que navegar. */
    public boolean isNecessaria() {
        return totalPaginas > 1;
    }

    public String linkDaPagina(int pagina) {
        return basePath + "?" + (queryBase == null ? "" : queryBase) + "pagina=" + pagina;
    }
}
