package com.mjwsolucoes.sistemanutricao.dto;

import com.mjwsolucoes.sistemanutricao.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Visão de um usuário no painel do administrador. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminDTO {
    private Long id;
    private String nome;
    private String username;
    private Role role;
    private String roleDescricao;
    private boolean ativo;
    private LocalDateTime criadoEm;
    private long totalFichas;

    /** Solicitação nova: ainda sem cargo definido pelo admin. */
    public boolean isPendente() {
        return role == Role.PENDENTE;
    }
}
