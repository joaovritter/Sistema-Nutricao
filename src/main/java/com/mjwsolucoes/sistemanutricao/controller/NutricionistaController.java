package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.dto.NutricionistaDTO;
import com.mjwsolucoes.sistemanutricao.service.NutricionistaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nutricionistas")
public class NutricionistaController {

    private final NutricionistaService nutricionistaService;

    public NutricionistaController(NutricionistaService nutricionistaService) {
        this.nutricionistaService = nutricionistaService;
    }

    @GetMapping
    public ResponseEntity<List<NutricionistaDTO>> listarTodos() {
        List<NutricionistaDTO> nutricionistas = nutricionistaService.listarTodos();
        return ResponseEntity.ok(nutricionistas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NutricionistaDTO> buscarPorId(@PathVariable Long id) {
        NutricionistaDTO nutricionista = nutricionistaService.buscarPorId(id);
        return ResponseEntity.ok(nutricionista);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<NutricionistaDTO> buscarPorUsername(@PathVariable String username) {
        NutricionistaDTO nutricionista = nutricionistaService.buscarPorUsername(username);
        return ResponseEntity.ok(nutricionista);
    }
}