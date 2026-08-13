package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.dto.CategoriaResumoDTO;
import com.mjwsolucoes.sistemanutricao.model.CategoriaReceita;
import com.mjwsolucoes.sistemanutricao.model.Receita;
import com.mjwsolucoes.sistemanutricao.repository.IngredienteRepository;
import com.mjwsolucoes.sistemanutricao.repository.ReceitaRepository;
import com.mjwsolucoes.sistemanutricao.repository.RefeicaoRepository;
import com.mjwsolucoes.sistemanutricao.service.ReceitaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private ReceitaRepository receitaRepository;
    private ReceitaService receitaService;
    private RefeicaoRepository refeicaoRepository;
    private IngredienteRepository ingredienteRepository;

    public HomeController(ReceitaRepository receitaRepository, ReceitaService receitaService,
                           RefeicaoRepository refeicaoRepository, IngredienteRepository ingredienteRepository) {
        this.receitaRepository = receitaRepository;
        this.receitaService = receitaService;
        this.refeicaoRepository = refeicaoRepository;
        this.ingredienteRepository = ingredienteRepository;
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
    public String login(Model model) {
        model.addAttribute("totalFichas", receitaRepository.count());
        model.addAttribute("totalRefeicoes", refeicaoRepository.count());
        model.addAttribute("totalIngredientes", ingredienteRepository.count());
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
