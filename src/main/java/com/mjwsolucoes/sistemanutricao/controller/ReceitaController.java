package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.dto.*;
import com.mjwsolucoes.sistemanutricao.service.ReceitaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receitas")
public class ReceitaController {

    private final ReceitaService receitaService;

    public ReceitaController(ReceitaService receitaService) {
        this.receitaService = receitaService;
    }

    @PostMapping
    public ResponseEntity<ReceitaDTO> criarReceita(
            @RequestBody ReceitaDTO receitaDTO,
            @RequestHeader("X-Username") String usernameNutricionista) {
        ReceitaDTO receitaCriada = receitaService.criarReceita(receitaDTO, usernameNutricionista);
        return ResponseEntity.ok(receitaCriada);
    }

    @GetMapping
    public ResponseEntity<List<ReceitaResumoDTO>> listarTodas() {
        List<ReceitaResumoDTO> receitas = receitaService.listarTodas();
        return ResponseEntity.ok(receitas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceitaDetalhadaDTO> buscarDetalhesReceita(@PathVariable Long id) {
        ReceitaDetalhadaDTO detalhes = receitaService.buscarDetalhesReceita(id);
        return ResponseEntity.ok(detalhes);
    }
}