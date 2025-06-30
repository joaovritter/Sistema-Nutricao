package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.dto.RefeicaoDTO;
import com.mjwsolucoes.sistemanutricao.dto.RefeicaoInputDTO;
import com.mjwsolucoes.sistemanutricao.service.RefeicaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/refeicoes")
public class RefeicaoController {
    @Autowired
    private RefeicaoService refeicaoService;

    @GetMapping
    public List<RefeicaoDTO> listar() {
        return refeicaoService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RefeicaoDTO> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(refeicaoService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<RefeicaoDTO> criar(@RequestBody RefeicaoInputDTO dto) {
        return ResponseEntity.ok(refeicaoService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RefeicaoDTO> editar(@PathVariable Long id, @RequestBody RefeicaoInputDTO dto) {
        try {
            return ResponseEntity.ok(refeicaoService.editar(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        refeicaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
} 