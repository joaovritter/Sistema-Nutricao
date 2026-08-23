package com.mjwsolucoes.sistemanutricao.model;

/** O que foi feito. */
public enum TipoAtividade {
    LOGIN("Entrou no sistema"),
    LOGIN_FALHOU("Tentativa de login"),
    LOGOUT("Saiu do sistema"),
    CRIACAO("Criação"),
    EDICAO("Edição"),
    ARQUIVAMENTO("Arquivamento"),
    DESARQUIVAMENTO("Desarquivamento"),
    EXCLUSAO("Exclusão");

    private final String descricao;

    TipoAtividade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
