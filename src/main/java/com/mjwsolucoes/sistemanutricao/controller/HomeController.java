package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.dto.CategoriaResumoDTO;
import com.mjwsolucoes.sistemanutricao.dto.FichaBuscaDTO;
import com.mjwsolucoes.sistemanutricao.dto.FichaFiltroDTO;
import com.mjwsolucoes.sistemanutricao.model.CategoriaReceita;
import com.mjwsolucoes.sistemanutricao.repository.IngredienteRepository;
import com.mjwsolucoes.sistemanutricao.repository.ReceitaRepository;
import com.mjwsolucoes.sistemanutricao.repository.RefeicaoRepository;
import com.mjwsolucoes.sistemanutricao.service.AdminUserService;
import com.mjwsolucoes.sistemanutricao.service.BuscaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private final ReceitaRepository receitaRepository;
    private final RefeicaoRepository refeicaoRepository;
    private final IngredienteRepository ingredienteRepository;
    private final BuscaService buscaService;
    private final AdminUserService adminUserService;

    public HomeController(ReceitaRepository receitaRepository,
                          RefeicaoRepository refeicaoRepository, IngredienteRepository ingredienteRepository,
                          BuscaService buscaService, AdminUserService adminUserService) {
        this.receitaRepository = receitaRepository;
        this.refeicaoRepository = refeicaoRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.buscaService = buscaService;
        this.adminUserService = adminUserService;
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
    public String dashboard(Model model) {
        model.addAttribute("totalFichas", receitaRepository.countByArquivadaFalse());
        model.addAttribute("totalRefeicoes", refeicaoRepository.countByArquivadaFalse());
        model.addAttribute("totalAlimentos", ingredienteRepository.count());
        model.addAttribute("totalArquivadas", receitaRepository.countByArquivadaTrue());
        model.addAttribute("solicitacoesPendentes", adminUserService.contarPendentes());
        model.addAttribute("categorias", resumoPorCategoria());
        return "dashboard";
    }

    private List<CategoriaResumoDTO> resumoPorCategoria() {
        List<Object[]> contagens = receitaRepository.countByCategoria();

        long maior = 1L;
        for (Object[] linha : contagens) {
            maior = Math.max(maior, (Long) linha[1]);
        }

        List<CategoriaResumoDTO> resumo = new ArrayList<>();
        for (Object[] linha : contagens) {
            CategoriaReceita categoria = (CategoriaReceita) linha[0];
            long quantidade = (Long) linha[1];
            int percentual = (int) Math.round((quantidade * 100.0) / maior);
            resumo.add(new CategoriaResumoDTO(categoria.getDescricao(), quantidade, percentual));
        }
        resumo.sort((a, b) -> Long.compare(b.getQuantidade(), a.getQuantidade()));
        return resumo;
    }

    @GetMapping("/fichatecnica")
    public String criar() {
        return "criarFichaTecnica";
    }

    @GetMapping("/criarIngrediente")
    public String criarAlimento() {
        return "criarIngrediente";
    }

    /** Listagem de fichas técnicas com busca e filtros por macronutriente. */
    @GetMapping("/visualizar")
    public String visualizarFichas(@ModelAttribute("filtro") FichaFiltroDTO filtro, Model model) {
        List<FichaBuscaDTO> fichas = buscaService.buscarFichas(filtro);
        model.addAttribute("fichas", fichas);

        // Nome digitado sem resultado: mostra fichas de nome parecido
        if (fichas.isEmpty() && filtro.getTermo() != null && !filtro.getTermo().isBlank()) {
            model.addAttribute("relacionadas", buscaService.fichasRelacionadas(filtro.getTermo(), filtro));
        }

        model.addAttribute("categorias", CategoriaReceita.values());
        model.addAttribute("totalArquivadas", receitaRepository.countByArquivadaTrue());
        return "visualizarFichaTecnica";
    }

    @GetMapping("/receita/editar/{id}")
    public String editarReceita(@PathVariable Long id, Model model) {
        model.addAttribute("receitaId", id);
        return "criarFichaTecnica";
    }

    @GetMapping("/refeicoes")
    public String refeicoes() {
        return "refeicoes";
    }
}
