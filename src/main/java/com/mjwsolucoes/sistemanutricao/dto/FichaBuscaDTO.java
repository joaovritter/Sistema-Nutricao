package com.mjwsolucoes.sistemanutricao.dto;

import lombok.Data;

/** Uma ficha técnica na tela de busca, já com os macros somados. */
@Data
public class FichaBuscaDTO {
    private Long id;
    private String nome;
    private String categoria;
    private String categoriaDescricao;
    private String nutricionista;
    private Integer tempoPreparo;
    private Integer numeroPorcoes;
    private boolean arquivada;

    /** Totais da receita inteira, em gramas (sódio em mg). */
    private Double proteinaTotal;
    private Double carboidratoTotal;
    private Double lipidioTotal;
    private Double sodioTotal;
    private Double gorduraSaturadaTotal;
    private Double kcalTotal;

    /** Os mesmos valores divididos pelo número de porções — base dos filtros. */
    private Double proteinaPorcao;
    private Double carboidratoPorcao;
    private Double lipidioPorcao;
    private Double sodioPorcao;
    private Double gorduraSaturadaPorcao;
    private Double kcalPorcao;
}
