package com.mjwsolucoes.sistemanutricao.model;

/**
 * Cargos do sistema. PENDENTE é o estado inicial de quem acabou de se registrar:
 * a conta existe, mas não tem cargo nem acesso até um ADMIN aprovar.
 */
public enum Role {
    ADMIN("Administrador"),
    NUTRICIONISTA("Nutricionista"),
    ESTUDANTE("Estudante"),
    COZINHA("Cozinha"),
    PENDENTE("Aguardando aprovação");

    private final String descricao;

    Role(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /** Cargos que um admin pode atribuir a um usuário (PENDENTE não é atribuível). */
    public static Role[] atribuiveis() {
        return new Role[]{ADMIN, NUTRICIONISTA, ESTUDANTE, COZINHA};
    }
}
