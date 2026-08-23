package com.mjwsolucoes.sistemanutricao.service;

import com.mjwsolucoes.sistemanutricao.dto.*;
import com.mjwsolucoes.sistemanutricao.model.AlvoAtividade;
import com.mjwsolucoes.sistemanutricao.model.Receita;
import com.mjwsolucoes.sistemanutricao.model.Refeicao;
import com.mjwsolucoes.sistemanutricao.model.TipoAtividade;
import com.mjwsolucoes.sistemanutricao.repository.ReceitaRepository;
import com.mjwsolucoes.sistemanutricao.repository.RefeicaoRepository;
import com.mjwsolucoes.sistemanutricao.util.TextoBusca;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Busca com filtros de fichas tecnicas e refeicoes, e o arquivamento de ambas.
 *
 * Os macronutrientes de uma ficha nao ficam gravados: sao somados a partir dos
 * ingredientes (peso liquido x valor por 100 g) sempre que a busca roda.
 */
@Service
public class BuscaService {

    /** Acima disso um nome conta como "relacionado" ao termo buscado. */
    private static final double LIMIAR_SUGESTAO = 0.34;

    private final ReceitaRepository receitaRepository;
    private final RefeicaoRepository refeicaoRepository;
    private final AtividadeService atividadeService;

    public BuscaService(ReceitaRepository receitaRepository, RefeicaoRepository refeicaoRepository,
                        AtividadeService atividadeService) {
        this.receitaRepository = receitaRepository;
        this.refeicaoRepository = refeicaoRepository;
        this.atividadeService = atividadeService;
    }

    // --------------------------- Fichas tecnicas ---------------------------

    @Transactional(readOnly = true)
    public List<FichaBuscaDTO> buscarFichas(FichaFiltroDTO filtro) {
        Map<Long, double[]> macros = mapaDeMacros();

        return receitaRepository.findAll().stream()
                .filter(r -> visivel(r.isArquivada(), filtro.isIncluirArquivadas(), filtro.isSomenteArquivadas()))
                .map(r -> montarFicha(r, macros.get(r.getId())))
                .filter(f -> vazio(filtro.getTermo()) || TextoBusca.contem(f.getNome(), filtro.getTermo()))
                .filter(f -> vazio(filtro.getCategoria()) || filtro.getCategoria().equals(f.getCategoria()))
                .filter(f -> vazio(filtro.getNutricionista())
                        || TextoBusca.contem(f.getNutricionista(), filtro.getNutricionista()))
                .filter(f -> naFaixa(f.getProteinaPorcao(), filtro.getProteinaMin(), filtro.getProteinaMax()))
                .filter(f -> naFaixa(f.getCarboidratoPorcao(), filtro.getCarboidratoMin(), filtro.getCarboidratoMax()))
                .filter(f -> naFaixa(f.getLipidioPorcao(), filtro.getLipidioMin(), filtro.getLipidioMax()))
                .filter(f -> naFaixa(f.getSodioPorcao(), filtro.getSodioMin(), filtro.getSodioMax()))
                .filter(f -> naFaixa(f.getKcalPorcao(), filtro.getKcalMin(), filtro.getKcalMax()))
                .sorted(comparadorFicha(filtro))
                .collect(Collectors.toList());
    }

    /** Sugestoes por semelhanca quando o nome buscado nao retorna nenhuma ficha. */
    @Transactional(readOnly = true)
    public List<FichaBuscaDTO> fichasRelacionadas(String termo, FichaFiltroDTO filtro) {
        if (vazio(termo)) {
            return List.of();
        }
        Map<Long, double[]> macros = mapaDeMacros();
        return receitaRepository.findAll().stream()
                .filter(r -> visivel(r.isArquivada(), filtro.isIncluirArquivadas(), filtro.isSomenteArquivadas()))
                .map(r -> montarFicha(r, macros.get(r.getId())))
                .map(f -> Map.entry(f, TextoBusca.similaridade(f.getNome(), termo)))
                .filter(e -> e.getValue() >= LIMIAR_SUGESTAO)
                .sorted(Map.Entry.<FichaBuscaDTO, Double>comparingByValue().reversed())
                .limit(6)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Transactional
    public void alterarArquivamentoFicha(Long id, boolean arquivada) {
        Receita receita = receitaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ficha nao encontrada"));
        receita.setArquivada(arquivada);
        receitaRepository.save(receita);

        atividadeService.registrar(
                arquivada ? TipoAtividade.ARQUIVAMENTO : TipoAtividade.DESARQUIVAMENTO,
                AlvoAtividade.FICHA,
                "Ficha \"" + receita.getNome() + (arquivada ? "\" arquivada" : "\" desarquivada"),
                receita.getId());
    }

    // ----------------------------- Refeicoes -----------------------------

    @Transactional(readOnly = true)
    public List<RefeicaoBuscaDTO> buscarRefeicoes(RefeicaoFiltroDTO filtro) {
        Map<Long, double[]> macros = mapaDeMacros();

        return refeicaoRepository.findAll().stream()
                .filter(r -> visivel(r.isArquivada(), filtro.isIncluirArquivadas(), filtro.isSomenteArquivadas()))
                .map(r -> montarRefeicao(r, macros))
                .filter(r -> vazio(filtro.getTermo()) || TextoBusca.contem(r.getNome(), filtro.getTermo()))
                .filter(r -> vazio(filtro.getContemFicha())
                        || r.getFichas().stream().anyMatch(f -> TextoBusca.contem(f, filtro.getContemFicha())))
                .filter(r -> naFaixa(r.getProteina(), filtro.getProteinaMin(), filtro.getProteinaMax()))
                .filter(r -> naFaixa(r.getCarboidrato(), filtro.getCarboidratoMin(), filtro.getCarboidratoMax()))
                .filter(r -> naFaixa(r.getLipidio(), filtro.getLipidioMin(), filtro.getLipidioMax()))
                .filter(r -> naFaixa(r.getKcal(), filtro.getKcalMin(), filtro.getKcalMax()))
                .sorted(comparadorRefeicao(filtro))
                .collect(Collectors.toList());
    }

    @Transactional
    public void alterarArquivamentoRefeicao(Long id, boolean arquivada) {
        Refeicao refeicao = refeicaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refeicao nao encontrada"));
        refeicao.setArquivada(arquivada);
        refeicaoRepository.save(refeicao);

        atividadeService.registrar(
                arquivada ? TipoAtividade.ARQUIVAMENTO : TipoAtividade.DESARQUIVAMENTO,
                AlvoAtividade.REFEICAO,
                "Refeicao \"" + refeicao.getNome() + (arquivada ? "\" arquivada" : "\" desarquivada"),
                refeicao.getId());
    }

    // ------------------------------- Apoio -------------------------------

    /** receitaId -> [proteina, carboidrato, lipidio, sodio, gordura saturada] totais. */
    private Map<Long, double[]> mapaDeMacros() {
        Map<Long, double[]> mapa = new HashMap<>();
        for (Object[] linha : receitaRepository.somarMacronutrientesPorReceita()) {
            Long receitaId = ((Number) linha[0]).longValue();
            double[] valores = new double[5];
            for (int i = 0; i < 5; i++) {
                valores[i] = linha[i + 1] == null ? 0.0 : ((Number) linha[i + 1]).doubleValue();
            }
            mapa.put(receitaId, valores);
        }
        return mapa;
    }

    private FichaBuscaDTO montarFicha(Receita receita, double[] macros) {
        double[] m = macros == null ? new double[5] : macros;
        int porcoes = porcoesValidas(receita);

        FichaBuscaDTO dto = new FichaBuscaDTO();
        dto.setId(receita.getId());
        dto.setNome(receita.getNome());
        dto.setArquivada(receita.isArquivada());
        dto.setTempoPreparo(receita.getTempoPreparo());
        dto.setNumeroPorcoes(receita.getNumeroPorcoes());
        if (receita.getCategoria() != null) {
            dto.setCategoria(receita.getCategoria().name());
            dto.setCategoriaDescricao(receita.getCategoria().getDescricao());
        }
        dto.setNutricionista(receita.getNutricionista() == null
                ? "-" : receita.getNutricionista().getNomeExibicao());

        double kcal = energia(m[0], m[1], m[2]);
        dto.setProteinaTotal(arredondar(m[0]));
        dto.setCarboidratoTotal(arredondar(m[1]));
        dto.setLipidioTotal(arredondar(m[2]));
        dto.setSodioTotal(arredondar(m[3]));
        dto.setGorduraSaturadaTotal(arredondar(m[4]));
        dto.setKcalTotal(arredondar(kcal));

        dto.setProteinaPorcao(arredondar(m[0] / porcoes));
        dto.setCarboidratoPorcao(arredondar(m[1] / porcoes));
        dto.setLipidioPorcao(arredondar(m[2] / porcoes));
        dto.setSodioPorcao(arredondar(m[3] / porcoes));
        dto.setGorduraSaturadaPorcao(arredondar(m[4] / porcoes));
        dto.setKcalPorcao(arredondar(kcal / porcoes));
        return dto;
    }

    /** Soma uma porcao de cada ficha que compoe a refeicao. */
    private RefeicaoBuscaDTO montarRefeicao(Refeicao refeicao, Map<Long, double[]> macros) {
        List<Receita> receitas = refeicao.getReceitas() == null ? List.of() : refeicao.getReceitas();

        RefeicaoBuscaDTO dto = new RefeicaoBuscaDTO();
        dto.setId(refeicao.getId());
        dto.setNome(refeicao.getNome());
        dto.setArquivada(refeicao.isArquivada());
        dto.setTotalFichas(receitas.size());
        dto.setFichas(receitas.stream().map(Receita::getNome).collect(Collectors.toList()));

        double proteina = 0, carboidrato = 0, lipidio = 0, sodio = 0;
        for (Receita receita : receitas) {
            double[] m = macros.getOrDefault(receita.getId(), new double[5]);
            int porcoes = porcoesValidas(receita);
            proteina += m[0] / porcoes;
            carboidrato += m[1] / porcoes;
            lipidio += m[2] / porcoes;
            sodio += m[3] / porcoes;
        }

        dto.setProteina(arredondar(proteina));
        dto.setCarboidrato(arredondar(carboidrato));
        dto.setLipidio(arredondar(lipidio));
        dto.setSodio(arredondar(sodio));
        dto.setKcal(arredondar(energia(proteina, carboidrato, lipidio)));
        return dto;
    }

    private Comparator<FichaBuscaDTO> comparadorFicha(FichaFiltroDTO filtro) {
        Map<String, Function<FichaBuscaDTO, Double>> numericos = Map.of(
                "proteina", FichaBuscaDTO::getProteinaPorcao,
                "carboidrato", FichaBuscaDTO::getCarboidratoPorcao,
                "lipidio", FichaBuscaDTO::getLipidioPorcao,
                "kcal", FichaBuscaDTO::getKcalPorcao
        );
        String campo = filtro.getOrdenarPor() == null ? "nome" : filtro.getOrdenarPor();

        Comparator<FichaBuscaDTO> comparador;
        if (numericos.containsKey(campo)) {
            Function<FichaBuscaDTO, Double> extrator = numericos.get(campo);
            comparador = Comparator.comparingDouble(f -> ou(extrator.apply(f)));
        } else if ("categoria".equals(campo)) {
            comparador = Comparator.comparing(f -> TextoBusca.normalizar(f.getCategoriaDescricao()));
        } else if ("porcoes".equals(campo)) {
            comparador = Comparator.comparingInt(f -> f.getNumeroPorcoes() == null ? 0 : f.getNumeroPorcoes());
        } else {
            comparador = Comparator.comparing(f -> TextoBusca.normalizar(f.getNome()));
        }
        return "desc".equalsIgnoreCase(filtro.getDirecao()) ? comparador.reversed() : comparador;
    }

    private Comparator<RefeicaoBuscaDTO> comparadorRefeicao(RefeicaoFiltroDTO filtro) {
        Map<String, Function<RefeicaoBuscaDTO, Double>> numericos = Map.of(
                "proteina", RefeicaoBuscaDTO::getProteina,
                "carboidrato", RefeicaoBuscaDTO::getCarboidrato,
                "lipidio", RefeicaoBuscaDTO::getLipidio,
                "kcal", RefeicaoBuscaDTO::getKcal
        );
        String campo = filtro.getOrdenarPor() == null ? "nome" : filtro.getOrdenarPor();

        Comparator<RefeicaoBuscaDTO> comparador;
        if (numericos.containsKey(campo)) {
            Function<RefeicaoBuscaDTO, Double> extrator = numericos.get(campo);
            comparador = Comparator.comparingDouble(r -> ou(extrator.apply(r)));
        } else if ("fichas".equals(campo)) {
            comparador = Comparator.comparingInt(RefeicaoBuscaDTO::getTotalFichas);
        } else {
            comparador = Comparator.comparing(r -> TextoBusca.normalizar(r.getNome()));
        }
        return "desc".equalsIgnoreCase(filtro.getDirecao()) ? comparador.reversed() : comparador;
    }

    private int porcoesValidas(Receita receita) {
        Integer porcoes = receita.getNumeroPorcoes();
        return (porcoes == null || porcoes < 1) ? 1 : porcoes;
    }

    /** 4 kcal/g para proteina e carboidrato, 9 kcal/g para lipidio. */
    private double energia(double proteina, double carboidrato, double lipidio) {
        return 4 * proteina + 4 * carboidrato + 9 * lipidio;
    }

    private boolean visivel(boolean arquivada, boolean incluirArquivadas, boolean somenteArquivadas) {
        if (somenteArquivadas) {
            return arquivada;
        }
        return incluirArquivadas || !arquivada;
    }

    private boolean naFaixa(Double valor, Double minimo, Double maximo) {
        double v = ou(valor);
        if (minimo != null && v < minimo) {
            return false;
        }
        return maximo == null || v <= maximo;
    }

    private boolean vazio(String texto) {
        return texto == null || texto.isBlank();
    }

    private double ou(Double valor) {
        return valor == null ? 0.0 : valor;
    }

    private double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
