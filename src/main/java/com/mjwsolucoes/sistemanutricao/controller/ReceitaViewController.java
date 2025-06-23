package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.dto.ReceitaDetalhadaDTO;
import com.mjwsolucoes.sistemanutricao.service.ReceitaService;
import org.springframework.stereotype.Controller; // Use @Controller para retornar views, não @RestController
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // <--- Mude para @Controller se este for um Controller de views
@RequestMapping("/receita") // <--- Mapeamento para as URLs de visualização da receita
public class ReceitaViewController { // <--- Renomeei para evitar conflito com ReceitaController REST

    private final ReceitaService receitaService;

    public ReceitaViewController(ReceitaService receitaService) {
        this.receitaService = receitaService;
    }

    // Este é o método que você precisa adicionar
    @GetMapping("/detalhes/{id}")
    public String exibirDetalhesReceita(@PathVariable Long id, Model model) {
        // Busca os detalhes da receita usando seu serviço
        ReceitaDetalhadaDTO detalhes = receitaService.buscarDetalhesReceita(id);

        // Adiciona os detalhes ao modelo para que o Thymeleaf possa acessá-los
        model.addAttribute("receitaDetalhes", detalhes); // Use um nome de atributo consistente

        // Retorna o nome do template Thymeleaf (ex: src/main/resources/templates/detalhesReceita.html)
        return "detalhesReceita"; // Certifique-se de que este é o nome do seu arquivo HTML
    }

    // Você pode ter outros métodos para /receita/editar, /receita/excluir, etc. aqui
}