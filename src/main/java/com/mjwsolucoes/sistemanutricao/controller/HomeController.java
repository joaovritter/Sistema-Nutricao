package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.model.Receita;
import com.mjwsolucoes.sistemanutricao.repository.ReceitaRepository;
import com.mjwsolucoes.sistemanutricao.service.ReceitaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HomeController {
    private ReceitaRepository receitaRepository;
    private ReceitaService receitaService;

    public HomeController(ReceitaRepository receitaRepository, ReceitaService receitaService) {
        this.receitaRepository = receitaRepository;
        this.receitaService = receitaService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/home")
    public String index() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String login() {
        return "dashboard";
    }

    @GetMapping("/fichatecnica")
    public String criar() {
        return "criarFichatecnica";
    }

    @GetMapping("/criarIngrediente")
    public String criarIngrediente() {
        return "criarIngrediente";
    }

    @GetMapping("/visualizar") // <-- URL ajustada para /visualizarFichaTecnica
    public String visualizarFichas(Model model) { // <-- Adicionado 'Model model'
        List<Receita> receitas = receitaRepository.findAll(); // <-- Busca as receitas
        model.addAttribute("receitas", receitas); // <-- Adiciona a lista ao Model

        return "visualizarFichaTecnica";
    }

    @GetMapping("/receita/editar/{id}")
    public String editarReceita(@PathVariable Long id, Model model) {
        // Adicionar o ID da receita ao modelo para que o JavaScript possa carregar os dados
        model.addAttribute("receitaId", id);
        return "criarFichaTecnica";
    }

    @GetMapping("/refeicoes")
    public String refeicoes() {
        return "refeicoes";
    }
}
