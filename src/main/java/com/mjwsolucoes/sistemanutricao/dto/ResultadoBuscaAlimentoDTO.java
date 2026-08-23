package com.mjwsolucoes.sistemanutricao.dto;

import lombok.Data;

import java.util.List;

/**
 * Resultado da busca de alimentos. Quando nada casa com o termo digitado,
 * 'itens' vem vazio e 'relacionados' traz as sugestões mais parecidas.
 */
@Data
public class ResultadoBuscaAlimentoDTO {
    private List<AlimentoDTO> itens;
    private List<AlimentoDTO> relacionados;
    private String termo;

    /** Total de alimentos que combinam com o filtro (não só os da página). */
    private long total;

    private PaginacaoDTO paginacao;

    public boolean isVazioComSugestoes() {
        return (itens == null || itens.isEmpty()) && relacionados != null && !relacionados.isEmpty();
    }
}
