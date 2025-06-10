package com.mjwsolucoes.sistemanutricao.dto;

import com.mjwsolucoes.sistemanutricao.model.Role;
import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String password;
    private Role role;
}
