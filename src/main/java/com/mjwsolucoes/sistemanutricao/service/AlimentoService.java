package com.mjwsolucoes.sistemanutricao.service;

import com.mjwsolucoes.sistemanutricao.dto.AlimentoDTO;
import com.mjwsolucoes.sistemanutricao.dto.AlimentoFiltroDTO;
import com.mjwsolucoes.sistemanutricao.dto.PaginacaoDTO;
import com.mjwsolucoes.sistemanutricao.dto.ResultadoBuscaAlimentoDTO;
import com.mjwsolucoes.sistemanutricao.model.Ingrediente;
import com.mjwsolucoes.sistemanutricao.repository.IngredienteRepository;
import com.mjwsolucoes.sistemanutricao.util.TextoBusca;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Consulta da tabela de alimentos (TACO + cadastros dos usuários).
 *
 * A base tem poucas centenas de linhas, então os filtros rodam em memória:
 * isso permite filtrar por kcal — que é derivado, não uma coluna — e comparar
 * nomes ignorando acentos, coisas que a consulta SQL não faria bem.
 */
@Service
public class AlimentoService {

    /** Acima disso um nome conta como "relacionado" ao termo buscado. */
    private static final double LIMIAR_SUGESTAO = 0.34;
    private static final int MAX_SUGESTOES = 8;

    private static final Map<String, Function<AlimentoDTO, Double>> CAMPOS_ORDENACAO = Map.of(
            "proteina", AlimentoDTO::getProteina,
            "carboidrato", AlimentoDTO::getCarboidrato,
            "lipidio", AlimentoDTO::getLipidio,
            "sodio", AlimentoDTO::getSodio,
            "gorduraSaturada", AlimentoDTO::getGorduraSaturada,
            "kcal", AlimentoDTO::getKcal
    );

    private final IngredienteRepository ingredienteRepository;

    public AlimentoService(IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }

    @Transactional(readOnly = true)
    public ResultadoBuscaAlimentoDTO buscar(AlimentoFiltroDTO filtro) {
        List<AlimentoDTO> todos = ingredienteRepository.findAll().stream()
                .map(this::converter)
                .collect(Collectors.toList());

        String termo = filtro.getTermo() == null ? "" : filtro.getTermo().trim();

        List<AlimentoDTO> itens = todos.stream()
                .filter(a -> combinaOrigem(a, filtro.getOrigem()))
                .filter(a -> dentroDaFaixa(a.getProteina(), filtro.getProteinaMin(), filtro.getProteinaMax()))
                .filter(a -> dentroDaFaixa(a.getCarboidrato(), filtro.getCarboidratoMin(), filtro.getCarboidratoMax()))
                .filter(a -> dentroDaFaixa(a.getLipidio(), filtro.getLipidioMin(), filtro.getLipidioMax()))
                .filter(a -> dentroDaFaixa(a.getSodio(), filtro.getSodioMin(), filtro.getSodioMax()))
                .filter(a -> dentroDaFaixa(a.getGorduraSaturada(), filtro.getGorduraSaturadaMin(), filtro.getGorduraSaturadaMax()))
                .filter(a -> dentroDaFaixa(a.getKcal(), filtro.getKcalMin(), filtro.getKcalMax()))
                .filter(a -> termo.isEmpty() || TextoBusca.contem(a.getNome(), termo))
                .sorted(comparador(filtro))
                .collect(Collectors.toList());

        ResultadoBuscaAlimentoDTO resultado = new ResultadoBuscaAlimentoDTO();
        resultado.setTermo(termo);
        resultado.setTotal(itens.size());

        PaginacaoDTO paginacao = montarPaginacao(itens.size(), filtro);
        resultado.setPaginacao(paginacao);
        resultado.setItens(recortarPagina(itens, paginacao));

        // Nome digitado não bateu com nada: oferece os alimentos mais parecidos,
        // respeitando os demais filtros já escolhidos.
        if (itens.isEmpty() && !termo.isEmpty()) {
            resultado.setRelacionados(sugerir(todos, filtro, termo));
        } else {
            resultado.setRelacionados(List.of());
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public AlimentoDTO buscarPorId(Long id) {
        return ingredienteRepository.findById(id)
                .map(this::converter)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alimento não encontrado"));
    }

    /** Recorta a lista completa na fatia da página pedida. */
    private List<AlimentoDTO> recortarPagina(List<AlimentoDTO> itens, PaginacaoDTO paginacao) {
        if (paginacao.getTamanhoPagina() <= 0 || itens.isEmpty()) {
            return itens;
        }
        int inicio = (paginacao.getPaginaAtual() - 1) * paginacao.getTamanhoPagina();
        int fim = Math.min(inicio + paginacao.getTamanhoPagina(), itens.size());
        return itens.subList(inicio, fim);
    }

    private PaginacaoDTO montarPaginacao(int totalItens, AlimentoFiltroDTO filtro) {
        PaginacaoDTO p = new PaginacaoDTO();
        p.setTotalItens(totalItens);
        p.setBasePath("/alimentos");
        p.setQueryBase(montarQueryBase(filtro));

        int tamanho = filtro.getTamanhoPagina();
        if (tamanho <= 0) {
            // Modo "tudo de uma vez" — usado pela API quando ninguém pede paginação
            p.setTamanhoPagina(0);
            p.setPaginaAtual(1);
            p.setTotalPaginas(1);
            p.setPaginas(List.of(1));
            p.setPrimeiroItem(totalItens == 0 ? 0 : 1);
            p.setUltimoItem(totalItens);
            return p;
        }

        p.setTamanhoPagina(tamanho);
        int totalPaginas = Math.max(1, (int) Math.ceil(totalItens / (double) tamanho));
        p.setTotalPaginas(totalPaginas);

        // Página fora do intervalo (filtro mudou, link antigo) volta para a borda
        int atual = Math.min(Math.max(filtro.getPagina(), 1), totalPaginas);
        p.setPaginaAtual(atual);

        p.setPrimeiroItem(totalItens == 0 ? 0 : (long) (atual - 1) * tamanho + 1);
        p.setUltimoItem(Math.min((long) atual * tamanho, totalItens));

        p.setTemAnterior(atual > 1);
        p.setTemProxima(atual < totalPaginas);
        p.setAnterior(Math.max(1, atual - 1));
        p.setProxima(Math.min(totalPaginas, atual + 1));

        // Janela de números centrada na página atual, encostando nas bordas
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

    /** Repete os filtros ativos na URL para a navegação não perder a busca. */
    private String montarQueryBase(AlimentoFiltroDTO filtro) {
        StringBuilder query = new StringBuilder();
        adicionarTexto(query, "termo", filtro.getTermo());
        if (filtro.getOrigem() != null && !"TODOS".equalsIgnoreCase(filtro.getOrigem())) {
            adicionarTexto(query, "origem", filtro.getOrigem());
        }
        adicionarNumero(query, "proteinaMin", filtro.getProteinaMin());
        adicionarNumero(query, "proteinaMax", filtro.getProteinaMax());
        adicionarNumero(query, "carboidratoMin", filtro.getCarboidratoMin());
        adicionarNumero(query, "carboidratoMax", filtro.getCarboidratoMax());
        adicionarNumero(query, "lipidioMin", filtro.getLipidioMin());
        adicionarNumero(query, "lipidioMax", filtro.getLipidioMax());
        adicionarNumero(query, "sodioMin", filtro.getSodioMin());
        adicionarNumero(query, "sodioMax", filtro.getSodioMax());
        adicionarNumero(query, "gorduraSaturadaMin", filtro.getGorduraSaturadaMin());
        adicionarNumero(query, "gorduraSaturadaMax", filtro.getGorduraSaturadaMax());
        adicionarNumero(query, "kcalMin", filtro.getKcalMin());
        adicionarNumero(query, "kcalMax", filtro.getKcalMax());
        adicionarTexto(query, "ordenarPor", filtro.getOrdenarPor());
        adicionarTexto(query, "direcao", filtro.getDirecao());
        return query.toString();
    }

    private void adicionarTexto(StringBuilder query, String nome, String valor) {
        if (valor != null && !valor.isBlank()) {
            query.append(nome).append('=')
                    .append(URLEncoder.encode(valor, StandardCharsets.UTF_8)).append('&');
        }
    }

    private void adicionarNumero(StringBuilder query, String nome, Double valor) {
        if (valor != null) {
            query.append(nome).append('=').append(valor).append('&');
        }
    }

    private List<AlimentoDTO> sugerir(List<AlimentoDTO> todos, AlimentoFiltroDTO filtro, String termo) {
        return todos.stream()
                .filter(a -> combinaOrigem(a, filtro.getOrigem()))
                .map(a -> Map.entry(a, TextoBusca.similaridade(a.getNome(), termo)))
                .filter(e -> e.getValue() >= LIMIAR_SUGESTAO)
                .sorted(Map.Entry.<AlimentoDTO, Double>comparingByValue().reversed())
                .limit(MAX_SUGESTOES)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private Comparator<AlimentoDTO> comparador(AlimentoFiltroDTO filtro) {
        String campo = filtro.getOrdenarPor() == null ? "nome" : filtro.getOrdenarPor();
        boolean descendente = "desc".equalsIgnoreCase(filtro.getDirecao());

        Comparator<AlimentoDTO> comparador;
        Function<AlimentoDTO, Double> extrator = CAMPOS_ORDENACAO.get(campo);
        if (extrator == null) {
            comparador = Comparator.comparing(a -> TextoBusca.normalizar(a.getNome()));
        } else {
            comparador = Comparator.comparing(a -> valor(extrator.apply(a)));
        }
        return descendente ? comparador.reversed() : comparador;
    }

    private boolean combinaOrigem(AlimentoDTO alimento, String origem) {
        if (origem == null || origem.isBlank() || "TODOS".equalsIgnoreCase(origem)) {
            return true;
        }
        return origem.equalsIgnoreCase(alimento.getOrigem());
    }

    private boolean dentroDaFaixa(Double valor, Double minimo, Double maximo) {
        double v = valor(valor);
        if (minimo != null && v < minimo) {
            return false;
        }
        return maximo == null || v <= maximo;
    }

    private double valor(Double d) {
        return d == null ? 0.0 : d;
    }

    private AlimentoDTO converter(Ingrediente ingrediente) {
        AlimentoDTO dto = new AlimentoDTO();
        dto.setId(ingrediente.getId());
        dto.setNome(ingrediente.getNome());
        dto.setProteina(ingrediente.getProteina());
        dto.setCarboidrato(ingrediente.getCarboidrato());
        dto.setLipidio(ingrediente.getLipidio());
        dto.setSodio(ingrediente.getSodio());
        dto.setGorduraSaturada(ingrediente.getGorduraSaturada());
        dto.setKcal(calcularKcal(ingrediente));
        dto.setOrigem(ingrediente.isIngredienteSistema() ? "TACO" : "PERSONALIZADO");
        if (ingrediente.getNutricionista() != null) {
            dto.setAutor(ingrediente.getNutricionista().getNomeExibicao());
        }
        return dto;
    }

    /** Energia estimada a partir dos macronutrientes: 4 kcal/g para proteína e carboidrato, 9 para lipídio. */
    private double calcularKcal(Ingrediente ingrediente) {
        return 4 * valor(ingrediente.getProteina())
                + 4 * valor(ingrediente.getCarboidrato())
                + 9 * valor(ingrediente.getLipidio());
    }
}
