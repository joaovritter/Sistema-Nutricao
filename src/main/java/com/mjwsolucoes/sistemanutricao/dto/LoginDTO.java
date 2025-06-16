package com.mjwsolucoes.sistemanutricao.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotBlank(message = "Username é obrigatório")
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    private String password;

}