package com.mjwsolucoes.sistemanutricao.service;

import com.mjwsolucoes.sistemanutricao.dto.RefeicaoDTO;
import com.mjwsolucoes.sistemanutricao.dto.RefeicaoInputDTO;
import com.mjwsolucoes.sistemanutricao.dto.ReceitaResumoDTO;
import com.mjwsolucoes.sistemanutricao.model.AlvoAtividade;
import com.mjwsolucoes.sistemanutricao.model.Refeicao;
import com.mjwsolucoes.sistemanutricao.model.TipoAtividade;
import com.mjwsolucoes.sistemanutricao.model.Receita;
import com.mjwsolucoes.sistemanutricao.repository.RefeicaoRepository;
import com.mjwsolucoes.sistemanutricao.repository.ReceitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RefeicaoService {
    @Autowired
    private RefeicaoRepository refeicaoRepository;
    @Autowired
    private ReceitaRepository receitaRepository;
    @Autowired
    private AtividadeService atividadeService;

    /** Somente as refeicoes em uso; as arquivadas ficam fora das listas. */
    public List<RefeicaoDTO> listarTodas() {
        return refeicaoRepository.findByArquivadaFalse().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public RefeicaoDTO criar(RefeicaoInputDTO dto) {
        Refeicao refeicao = new Refeicao();
        refeicao.setNome(dto.getNome());
        List<Receita> receitas = receitaRepository.findAllById(dto.getReceitasIds());
        refeicao.setReceitas(receitas);
        refeicao = refeicaoRepository.save(refeicao);
        atividadeService.registrar(TipoAtividade.CRIACAO, AlvoAtividade.REFEICAO,
                "Refeicao \"" + refeicao.getNome() + "\" criada", refeicao.getId());
        return toDTO(refeicao);
    }

    @Transactional
    public RefeicaoDTO editar(Long id, RefeicaoInputDTO dto) {
        Optional<Refeicao> opt = refeicaoRepository.findById(id);
        if (opt.isEmpty()) throw new RuntimeException("Refeição não encontrada");
        Refeicao refeicao = opt.get();
        refeicao.setNome(dto.getNome());
        List<Receita> receitas = receitaRepository.findAllById(dto.getReceitasIds());
        refeicao.setReceitas(receitas);
        refeicao = refeicaoRepository.save(refeicao);
        atividadeService.registrar(TipoAtividade.EDICAO, AlvoAtividade.REFEICAO,
                "Refeicao \"" + refeicao.getNome() + "\" editada", refeicao.getId());
        return toDTO(refeicao);
    }

    @Transactional
    public void excluir(Long id) {
        String nome = refeicaoRepository.findById(id).map(Refeicao::getNome).orElse("?");
        refeicaoRepository.deleteById(id);
        atividadeService.registrar(TipoAtividade.EXCLUSAO, AlvoAtividade.REFEICAO,
                "Refeicao \"" + nome + "\" excluida", id);
    }

    public RefeicaoDTO buscarPorId(Long id) {
        return refeicaoRepository.findById(id).map(this::toDTO).orElseThrow(() -> new RuntimeException("Refeição não encontrada"));
    }

    private RefeicaoDTO toDTO(Refeicao refeicao) {
        RefeicaoDTO dto = new RefeicaoDTO();
        dto.setId(refeicao.getId());
        dto.setNome(refeicao.getNome());
        List<ReceitaResumoDTO> receitas = refeicao.getReceitas().stream().map(r -> {
            ReceitaResumoDTO resumo = new ReceitaResumoDTO();
            resumo.setId(r.getId());
            resumo.setNome(r.getNome());
            return resumo;
        }).collect(Collectors.toList());
        dto.setReceitas(receitas);
        dto.setArquivada(refeicao.isArquivada());
        return dto;
    }
}