package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.dto.AtividadeFiltroDTO;
import com.mjwsolucoes.sistemanutricao.dto.ResultadoAtividadeDTO;
import com.mjwsolucoes.sistemanutricao.model.AlvoAtividade;
import com.mjwsolucoes.sistemanutricao.model.TipoAtividade;
import com.mjwsolucoes.sistemanutricao.service.AtividadeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/** Histórico de atividade: acessos e operações sobre fichas, refeições e alimentos. */
@Controller
@RequestMapping("/admin")
public class AdminLogController {

    private final AtividadeService atividadeService;

    public AdminLogController(AtividadeService atividadeService) {
        this.atividadeService = atividadeService;
    }

    @GetMapping("/logs")
    public String listarLogs(@ModelAttribute("filtro") AtividadeFiltroDTO filtro, Model model) {
        ResultadoAtividadeDTO resultado = atividadeService.consultar(filtro);
        model.addAttribute("resultado", resultado);
        model.addAttribute("registros", resultado.getRegistros());
        model.addAttribute("tipos", TipoAtividade.values());
        model.addAttribute("alvos", AlvoAtividade.values());
        return "admin/logs";
    }
}
