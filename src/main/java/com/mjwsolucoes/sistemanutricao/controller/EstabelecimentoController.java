package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.dto.EstabelecimentoDTO;
import com.mjwsolucoes.sistemanutricao.service.EstabelecimentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estabelecimentos")
public class EstabelecimentoController {

    private final EstabelecimentoService estabelecimentoService;

    public EstabelecimentoController(EstabelecimentoService estabelecimentoService) {
        this.estabelecimentoService = estabelecimentoService;
    }

    @GetMapping
    public ResponseEntity<List<EstabelecimentoDTO>> listarTodos() {
        List<EstabelecimentoDTO> estabelecimentos = estabelecimentoService.listarTodos();
        return ResponseEntity.ok(estabelecimentos);
    }

    @GetMapping("/{username}")
    public ResponseEntity<EstabelecimentoDTO> buscarPorUsername(@PathVariable String username) {
        EstabelecimentoDTO estabelecimento = estabelecimentoService.buscarPorUsername(username);
        return ResponseEntity.ok(estabelecimento);
    }
}