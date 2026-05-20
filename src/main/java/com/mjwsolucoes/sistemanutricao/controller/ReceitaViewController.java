package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.dto.ReceitaDetalhadaDTO;
import com.mjwsolucoes.sistemanutricao.repository.PerfilNutricionalRepository;
import com.mjwsolucoes.sistemanutricao.repository.ReceitaIngredienteRepository;
import com.mjwsolucoes.sistemanutricao.repository.ReceitaRepository;
import com.mjwsolucoes.sistemanutricao.service.ReceitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/receita")
public class ReceitaViewController {

    private final ReceitaService receitaService;

    @Autowired
    private ReceitaRepository receitaRepository;

    @Autowired
    private ReceitaIngredienteRepository receitaIngredienteRepository;

    @Autowired
    private PerfilNutricionalRepository perfilNutricionalRepository;

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

    @PostMapping("/excluir/{id}")
    @Transactional
    public String excluirReceita(@PathVariable Long id) {
        receitaIngredienteRepository.deleteByReceitaId(id);
        perfilNutricionalRepository.deleteByReceitaId(id);
        receitaRepository.deleteById(id);
        return "redirect:/visualizar";
    }
}