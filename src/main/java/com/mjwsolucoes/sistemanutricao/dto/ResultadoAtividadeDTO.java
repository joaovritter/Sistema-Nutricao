package com.mjwsolucoes.sistemanutricao.dto;

import com.mjwsolucoes.sistemanutricao.model.RegistroAtividade;
import lombok.Data;

import java.util.List;

/** Página do histórico de atividade, com o que a tela precisa para os filtros. */
@Data
public class ResultadoAtividadeDTO {
    private List<RegistroAtividade> registros;
    private PaginacaoDTO paginacao;
    private long total;

    /** Nomes já presentes no histórico, para preencher o seletor de usuário. */
    private List<String> usuarios;
}
