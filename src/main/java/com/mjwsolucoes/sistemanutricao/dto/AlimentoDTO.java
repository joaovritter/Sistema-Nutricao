package com.mjwsolucoes.sistemanutricao.dto;

import lombok.Data;

/**
 * Um alimento como aparece na listagem: valores por 100 g, mais a energia
 * derivada dos macronutrientes (4/4/9 kcal por grama).
 */
@Data
public class AlimentoDTO {
    private Long id;
    private String nome;
    private Double proteina;
    private Double carboidrato;
    private Double lipidio;
    private Double sodio;
    private Double gorduraSaturada;
    private Double kcal;

    /** "TACO" para os alimentos do sistema, "PERSONALIZADO" para os cadastrados por usuários. */
    private String origem;
    private String autor;

    public boolean isPersonalizado() {
        return "PERSONALIZADO".equals(origem);
    }
}
