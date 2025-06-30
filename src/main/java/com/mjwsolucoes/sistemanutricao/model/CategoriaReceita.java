package com.mjwsolucoes.sistemanutricao.model;

public enum CategoriaReceita {
    ENTRADA("Entrada"),
    PRATO_PRINCIPAL("Prato Principal (Proteína)"),
    GUARNICAO("Complemento (Guarnição)"),
    SALADA("Salada"),
    SOBREMESA("Sobremesa"),
    BEBIDA("Bebida");

    private final String descricao;

    CategoriaReceita(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
