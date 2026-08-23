package com.mjwsolucoes.sistemanutricao.dto;

import lombok.Data;

import java.util.List;

/** Uma refeição na tela de busca, com os macros somados das fichas que a compõem. */
@Data
public class RefeicaoBuscaDTO {
    private Long id;
    private String nome;
    private boolean arquivada;
    private int totalFichas;
    private List<String> fichas;

    private Double proteina;
    private Double carboidrato;
    private Double lipidio;
    private Double sodio;
    private Double kcal;
}
