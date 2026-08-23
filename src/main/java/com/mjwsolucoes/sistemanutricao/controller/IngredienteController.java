package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.dto.*;
import com.mjwsolucoes.sistemanutricao.service.IngredienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    /**
     * Cadastra um alimento em nome de quem está logado.
     * Substitui a versão que recebia o username pela URL — o autor vem da sessão,
     * então ninguém cadastra alimento no nome de outra pessoa.
     */
    @PostMapping
    public ResponseEntity<IngredienteUserDTO> criarAlimento(@RequestBody IngredienteUserDTO ingredienteDTO,
                                                            Principal principal) {
        IngredienteUserDTO criado =
                ingredienteService.criarIngredienteNutricionista(ingredienteDTO, principal.getName());
        return ResponseEntity.ok(criado);
    }

    @GetMapping("/meus")
    public ResponseEntity<List<IngredienteUserDTO>> listarMeusAlimentos(Principal principal) {
        return ResponseEntity.ok(ingredienteService.listarIngredientesNutricionista(principal.getName()));
    }

    @GetMapping("/nutricionista/{username}")
    public ResponseEntity<List<IngredienteUserDTO>> listarIngredientesNutricionista(
            @PathVariable String username) {
        List<IngredienteUserDTO> ingredientes = ingredienteService.listarIngredientesNutricionista(username);
        return ResponseEntity.ok(ingredientes);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<IngredienteDTO>> buscarIngredientesPorNome(
            @RequestParam String nome) {
        List<IngredienteDTO> ingredientes = ingredienteService.buscarIngredientesPorNome(nome);
        return ResponseEntity.ok(ingredientes);
    }
}
