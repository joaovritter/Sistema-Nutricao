package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.dto.*;
import com.mjwsolucoes.sistemanutricao.service.IngredienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredientes")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    public IngredienteController(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    @GetMapping
    public ResponseEntity<List<IngredienteDTO>> listarIngredientes() {
        List<IngredienteDTO> ingredientes = ingredienteService.listarIngredientes();
        return ResponseEntity.ok(ingredientes);
    }

    @GetMapping("/nutricionista/{username}")
    public ResponseEntity<List<IngredienteUserDTO>> listarIngredientesNutricionista(
            @PathVariable String username) {
        List<IngredienteUserDTO> ingredientes = ingredienteService.listarIngredientesNutricionista(username);
        return ResponseEntity.ok(ingredientes);
    }

    @PostMapping("/nutricionista/{username}")
    public ResponseEntity<IngredienteUserDTO> criarIngredienteNutricionista(
            @RequestBody IngredienteUserDTO ingredienteDTO,
            @PathVariable String username) {
        IngredienteUserDTO ingredienteCriado =
                ingredienteService.criarIngredienteNutricionista(ingredienteDTO, username);
        return ResponseEntity.ok(ingredienteCriado);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<IngredienteDTO>> buscarIngredientesPorNome(
            @RequestParam String nome) {
        List<IngredienteDTO> ingredientes = ingredienteService.buscarIngredientesPorNome(nome);
        return ResponseEntity.ok(ingredientes);
    }
}