package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.dto.AlimentoDTO;
import com.mjwsolucoes.sistemanutricao.dto.AlimentoFiltroDTO;
import com.mjwsolucoes.sistemanutricao.dto.ResultadoBuscaAlimentoDTO;
import com.mjwsolucoes.sistemanutricao.service.AlimentoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

/** Seção de Alimentos: listagem da tabela TACO + cadastros do sistema, com busca e filtros. */
@Controller
public class AlimentoController {

    private final AlimentoService alimentoService;

    public AlimentoController(AlimentoService alimentoService) {
        this.alimentoService = alimentoService;
    }

    @GetMapping("/alimentos")
    public String listar(@ModelAttribute("filtro") AlimentoFiltroDTO filtro, Model model) {
        ResultadoBuscaAlimentoDTO resultado = alimentoService.buscar(filtro);
        model.addAttribute("resultado", resultado);
        model.addAttribute("alimentos", resultado.getItens());
        return "alimentos";
    }

    /** Detalhes dos macronutrientes de um alimento. */
    @GetMapping("/alimentos/{id:\\d+}")
    public String detalhes(@PathVariable Long id, Model model) {
        AlimentoDTO alimento = alimentoService.buscarPorId(id);
        model.addAttribute("alimento", alimento);
        return "detalhesAlimento";
    }

    /** Cadastro de alimento — mesma tela do antigo /criarIngrediente. */
    @GetMapping("/alimentos/novo")
    public String novo() {
        return "criarIngrediente";
    }
}
