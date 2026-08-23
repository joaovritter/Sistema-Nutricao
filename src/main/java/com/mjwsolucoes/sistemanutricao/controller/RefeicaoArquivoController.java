package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.service.BuscaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Arquivamento de refeições. Fica fora de /api porque o acesso é mais restrito
 * que o das demais rotas de escrita: só nutricionista e admin arquivam.
 */
@RestController
@RequestMapping("/refeicao")
public class RefeicaoArquivoController {

    private final BuscaService buscaService;

    public RefeicaoArquivoController(BuscaService buscaService) {
        this.buscaService = buscaService;
    }

    @PostMapping("/arquivar/{id}")
    public ResponseEntity<Void> arquivar(@PathVariable Long id) {
        buscaService.alterarArquivamentoRefeicao(id, true);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/desarquivar/{id}")
    public ResponseEntity<Void> desarquivar(@PathVariable Long id) {
        buscaService.alterarArquivamentoRefeicao(id, false);
        return ResponseEntity.noContent().build();
    }
}
