package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.dto.AlimentoDTO;
import com.mjwsolucoes.sistemanutricao.dto.AlimentoFiltroDTO;
import com.mjwsolucoes.sistemanutricao.dto.FichaBuscaDTO;
import com.mjwsolucoes.sistemanutricao.dto.FichaFiltroDTO;
import com.mjwsolucoes.sistemanutricao.dto.RefeicaoBuscaDTO;
import com.mjwsolucoes.sistemanutricao.dto.RefeicaoFiltroDTO;
import com.mjwsolucoes.sistemanutricao.dto.ResultadoBuscaAlimentoDTO;
import com.mjwsolucoes.sistemanutricao.service.AlimentoService;
import com.mjwsolucoes.sistemanutricao.service.BuscaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Busca em JSON, usada pelas telas que filtram sem recarregar a página
 * (refeições) e pelo autocompletar de alimentos na criação de fichas.
 */
@RestController
@RequestMapping("/api/busca")
public class BuscaApiController {

    private final AlimentoService alimentoService;
    private final BuscaService buscaService;

    public BuscaApiController(AlimentoService alimentoService, BuscaService buscaService) {
        this.alimentoService = alimentoService;
        this.buscaService = buscaService;
    }

    @GetMapping("/alimentos")
    public ResultadoBuscaAlimentoDTO alimentos(@ModelAttribute AlimentoFiltroDTO filtro) {
        return alimentoService.buscar(filtro);
    }

    /** Um alimento só — alimenta o pop-up de detalhes da tela de Alimentos. */
    @GetMapping("/alimentos/{id}")
    public AlimentoDTO alimento(@PathVariable Long id) {
        return alimentoService.buscarPorId(id);
    }

    @GetMapping("/fichas")
    public List<FichaBuscaDTO> fichas(@ModelAttribute FichaFiltroDTO filtro) {
        return buscaService.buscarFichas(filtro);
    }

    @GetMapping("/refeicoes")
    public List<RefeicaoBuscaDTO> refeicoes(@ModelAttribute RefeicaoFiltroDTO filtro) {
        return buscaService.buscarRefeicoes(filtro);
    }
}
