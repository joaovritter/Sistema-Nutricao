package com.mjwsolucoes.sistemanutricao.service;

import com.mjwsolucoes.sistemanutricao.dto.AtividadeFiltroDTO;
import com.mjwsolucoes.sistemanutricao.dto.PaginacaoDTO;
import com.mjwsolucoes.sistemanutricao.dto.ResultadoAtividadeDTO;
import com.mjwsolucoes.sistemanutricao.model.AlvoAtividade;
import com.mjwsolucoes.sistemanutricao.model.RegistroAtividade;
import com.mjwsolucoes.sistemanutricao.model.TipoAtividade;
import com.mjwsolucoes.sistemanutricao.repository.RegistroAtividadeRepository;
import com.mjwsolucoes.sistemanutricao.util.TextoBusca;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Grava o histórico de atividade do sistema.
 *
 * Cada registro é gravado numa transação própria (REQUIRES_NEW): o histórico
 * é observação, não parte da operação — se a gravação do log falhar, a ação do
 * usuário não pode ser desfeita por causa disso, e vice-versa.
 */
@Service
public class AtividadeService {

    private final RegistroAtividadeRepository repository;

    public AtividadeService(RegistroAtividadeRepository repository) {
        this.repository = repository;
    }

    /** Registra em nome do usuário autenticado no momento. */
    public void registrar(TipoAtividade tipo, AlvoAtividade alvo, String descricao, Long alvoId) {
        registrarComo(usuarioAtual(), tipo, alvo, descricao, alvoId);
    }

    /**
     * Registra informando o usuário explicitamente — necessário no login e no
     * logout, quando o contexto de segurança ainda não existe ou já foi limpo.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarComo(String usuario, TipoAtividade tipo, AlvoAtividade alvo,
                              String descricao, Long alvoId) {
        try {
            RegistroAtividade registro = new RegistroAtividade();
            registro.setMomento(LocalDateTime.now());
            registro.setUsuario(recortar(usuario == null ? "desconhecido" : usuario, 80));
            registro.setTipo(tipo);
            registro.setAlvo(alvo);
            registro.setDescricao(recortar(descricao, 255));
            registro.setAlvoId(alvoId);
            repository.save(registro);
        } catch (RuntimeException e) {
            // Um histórico que não grava não pode derrubar a operação em si
            System.err.println("Falha ao registrar atividade: " + e.getMessage());
        }
    }

    /** Histórico filtrado e paginado, do mais recente para o mais antigo. */
    @Transactional(readOnly = true)
    public ResultadoAtividadeDTO consultar(AtividadeFiltroDTO filtro) {
        List<RegistroAtividade> encontrados = repository.findAllByOrderByMomentoDescIdDesc().stream()
                .filter(r -> vazio(filtro.getUsuario()) || filtro.getUsuario().equals(r.getUsuario()))
                .filter(r -> filtro.getTipo() == null || filtro.getTipo() == r.getTipo())
                .filter(r -> filtro.getAlvo() == null || filtro.getAlvo() == r.getAlvo())
                .filter(r -> vazio(filtro.getTermo()) || TextoBusca.contem(r.getDescricao(), filtro.getTermo()))
                .filter(r -> filtro.getDe() == null
                        || !r.getMomento().toLocalDate().isBefore(filtro.getDe()))
                .filter(r -> filtro.getAte() == null
                        || !r.getMomento().toLocalDate().isAfter(filtro.getAte()))
                .collect(Collectors.toList());

        PaginacaoDTO paginacao = montarPaginacao(encontrados.size(), filtro);

        ResultadoAtividadeDTO resultado = new ResultadoAtividadeDTO();
        resultado.setTotal(encontrados.size());
        resultado.setPaginacao(paginacao);
        resultado.setUsuarios(repository.listarUsuarios());

        int inicio = (paginacao.getPaginaAtual() - 1) * paginacao.getTamanhoPagina();
        int fim = Math.min(inicio + paginacao.getTamanhoPagina(), encontrados.size());
        resultado.setRegistros(encontrados.isEmpty() ? List.of() : encontrados.subList(inicio, fim));
        return resultado;
    }

    private PaginacaoDTO montarPaginacao(int totalItens, AtividadeFiltroDTO filtro) {
        PaginacaoDTO p = new PaginacaoDTO();
        p.setBasePath("/admin/logs");
        p.setQueryBase(montarQueryBase(filtro));
        p.setTotalItens(totalItens);

        int tamanho = filtro.getTamanhoPagina() <= 0 ? 30 : filtro.getTamanhoPagina();
        p.setTamanhoPagina(tamanho);

        int totalPaginas = Math.max(1, (int) Math.ceil(totalItens / (double) tamanho));
        p.setTotalPaginas(totalPaginas);

        int atual = Math.min(Math.max(filtro.getPagina(), 1), totalPaginas);
        p.setPaginaAtual(atual);
        p.setPrimeiroItem(totalItens == 0 ? 0 : (long) (atual - 1) * tamanho + 1);
        p.setUltimoItem(Math.min((long) atual * tamanho, totalItens));

        p.setTemAnterior(atual > 1);
        p.setTemProxima(atual < totalPaginas);
        p.setAnterior(Math.max(1, atual - 1));
        p.setProxima(Math.min(totalPaginas, atual + 1));

        int inicio = Math.max(1, atual - PaginacaoDTO.JANELA / 2);
        int fim = Math.min(totalPaginas, inicio + PaginacaoDTO.JANELA - 1);
        inicio = Math.max(1, fim - PaginacaoDTO.JANELA + 1);

        List<Integer> paginas = new ArrayList<>();
        for (int i = inicio; i <= fim; i++) {
            paginas.add(i);
        }
        p.setPaginas(paginas);
        p.setPosicaoNaJanela(paginas.indexOf(atual));
        return p;
    }

    private String montarQueryBase(AtividadeFiltroDTO filtro) {
        StringBuilder query = new StringBuilder();
        anexar(query, "usuario", filtro.getUsuario());
        anexar(query, "tipo", filtro.getTipo() == null ? null : filtro.getTipo().name());
        anexar(query, "alvo", filtro.getAlvo() == null ? null : filtro.getAlvo().name());
        anexar(query, "termo", filtro.getTermo());
        anexar(query, "de", filtro.getDe() == null ? null : filtro.getDe().toString());
        anexar(query, "ate", filtro.getAte() == null ? null : filtro.getAte().toString());
        return query.toString();
    }

    private void anexar(StringBuilder query, String nome, String valor) {
        if (valor != null && !valor.isBlank()) {
            query.append(nome).append('=')
                    .append(URLEncoder.encode(valor, StandardCharsets.UTF_8)).append('&');
        }
    }

    private boolean vazio(String texto) {
        return texto == null || texto.isBlank();
    }

    public String usuarioAtual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "sistema";
        }
        return auth.getName();
    }

    private String recortar(String texto, int limite) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= limite ? texto : texto.substring(0, limite - 1) + "…";
    }
}
