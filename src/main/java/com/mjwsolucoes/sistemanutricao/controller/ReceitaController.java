package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.dto.*;
import com.mjwsolucoes.sistemanutricao.service.ReceitaService;
import org.springframework.http.HttpStatus; // Importe este
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
            @RequestBody ReceitaDTO receitaDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usernameNutricionista = null;

        // --- Adicione estes logs detalhados aqui ---
        System.out.println("ReceitaController - Authentication object: " + authentication);
        if (authentication != null) {
            System.out.println("ReceitaController - Authentication is authenticated: " + authentication.isAuthenticated());
            System.out.println("ReceitaController - Principal type: " + authentication.getPrincipal().getClass().getName());
            System.out.println("ReceitaController - Principal object: " + authentication.getPrincipal());

            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                usernameNutricionista = ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                usernameNutricionista = (String) principal;
            } else {
                System.out.println("ReceitaController - Principal is neither UserDetails nor String. Type: " + principal.getClass().getName());
            }
        }
        System.out.println("ReceitaController - Username obtido do SecurityContextHolder: " + usernameNutricionista);
        // --- Fim dos logs detalhados ---


        if (usernameNutricionista == null) {
            // Se chegar aqui, o username não foi obtido. Retorne um 401 para o frontend.
            // O frontend pode então redirecionar para o login.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

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

    @PutMapping("/{id}")
    public ResponseEntity<ReceitaDTO> editarReceita(
            @PathVariable Long id,
            @RequestBody ReceitaDTO receitaDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usernameNutricionista = null;

        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                usernameNutricionista = ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                usernameNutricionista = (String) principal;
            }
        }

        if (usernameNutricionista == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        ReceitaDTO receitaEditada = receitaService.editarReceita(id, receitaDTO, usernameNutricionista);
        return ResponseEntity.ok(receitaEditada);
    }
}