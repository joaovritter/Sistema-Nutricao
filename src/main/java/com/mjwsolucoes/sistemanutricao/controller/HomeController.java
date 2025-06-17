package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.model.Receita;
import com.mjwsolucoes.sistemanutricao.repository.ReceitaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Scanner;

@Controller
public class HomeController {
    private ReceitaRepository receitaRepository;

    public HomeController(ReceitaRepository receitaRepository) {
        this.receitaRepository = receitaRepository;
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

    @GetMapping("/visualizar") // <-- URL ajustada para /visualizarFichaTecnica
    public String visualizarFichas(Model model) { // <-- Adicionado 'Model model'
        List<Receita> receitas = receitaRepository.findAll(); // <-- Busca as receitas
        model.addAttribute("receitas", receitas); // <-- Adiciona a lista ao Model

        return "visualizarFichaTecnica";
    }
}
