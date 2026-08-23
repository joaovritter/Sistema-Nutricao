package com.mjwsolucoes.sistemanutricao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Uma linha do histórico de atividade do sistema: quem fez, o que fez e quando.
 *
 * O registro guarda o nome do usuário como texto, e não uma chave estrangeira,
 * para o histórico sobreviver à exclusão de uma conta.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroAtividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime momento;

    @Column(nullable = false, length = 80)
    private String usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAtividade tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlvoAtividade alvo;

    /** Frase pronta para leitura, ex.: "Ficha \"Arroz integral\" criada". */
    @Column(nullable = false, length = 255)
    private String descricao;

    /** Id do item afetado, quando existe — permite ligar o registro à tela dele. */
    @Column(name = "alvo_id")
    private Long alvoId;
}
