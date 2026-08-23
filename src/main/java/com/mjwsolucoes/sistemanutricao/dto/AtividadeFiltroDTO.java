package com.mjwsolucoes.sistemanutricao.dto;

import com.mjwsolucoes.sistemanutricao.model.AlvoAtividade;
import com.mjwsolucoes.sistemanutricao.model.TipoAtividade;
import lombok.Data;

import java.time.LocalDate;

/** Filtros da tela de registro de atividade. Campo nulo significa "não filtrar". */
@Data
public class AtividadeFiltroDTO {

    private String usuario;
    private TipoAtividade tipo;
    private AlvoAtividade alvo;

    /** Texto livre buscado na descrição. */
    private String termo;

    private LocalDate de;
    private LocalDate ate;

    private int pagina = 1;
    private int tamanhoPagina = 30;

    public boolean temFiltroAtivo() {
        return (usuario != null && !usuario.isBlank())
                || tipo != null || alvo != null
                || (termo != null && !termo.isBlank())
                || de != null || ate != null;
    }
}
