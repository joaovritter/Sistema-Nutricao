package com.mjwsolucoes.sistemanutricao.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String username;
    private String role;

    public UsuarioDTO(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
}