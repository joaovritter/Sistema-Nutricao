package com.mjwsolucoes.sistemanutricao.model;

/** Sobre o que a atividade aconteceu. */
public enum AlvoAtividade {
    SESSAO("Acesso"),
    FICHA("Ficha técnica"),
    REFEICAO("Refeição"),
    ALIMENTO("Alimento"),
    USUARIO("Usuário");

    private final String descricao;

    AlvoAtividade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
