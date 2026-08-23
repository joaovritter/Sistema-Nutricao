package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.dto.ReceitaDetalhadaDTO;
import com.mjwsolucoes.sistemanutricao.repository.PerfilNutricionalRepository;
import com.mjwsolucoes.sistemanutricao.repository.ReceitaIngredienteRepository;
import com.mjwsolucoes.sistemanutricao.repository.ReceitaRepository;
import com.mjwsolucoes.sistemanutricao.model.AlvoAtividade;
import com.mjwsolucoes.sistemanutricao.model.TipoAtividade;
import com.mjwsolucoes.sistemanutricao.service.AtividadeService;
import com.mjwsolucoes.sistemanutricao.service.BuscaService;
import com.mjwsolucoes.sistemanutricao.service.ReceitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/receita")
public class ReceitaViewController {

    private final ReceitaService receitaService;
    private final BuscaService buscaService;
    private final AtividadeService atividadeService;

    @Autowired
    private ReceitaRepository receitaRepository;

    @Autowired
    private ReceitaIngredienteRepository receitaIngredienteRepository;

    @Autowired
    private PerfilNutricionalRepository perfilNutricionalRepository;

    public ReceitaViewController(ReceitaService receitaService, BuscaService buscaService,
                                 AtividadeService atividadeService) {
        this.receitaService = receitaService;
        this.buscaService = buscaService;
        this.atividadeService = atividadeService;
    }

    @GetMapping("/detalhes/{id}")
    public String exibirDetalhesReceita(@PathVariable Long id, Model model) {
        ReceitaDetalhadaDTO detalhes = receitaService.buscarDetalhesReceita(id);
        model.addAttribute("receitaDetalhes", detalhes);
        return "detalhesReceita";
    }

    /** Tira a ficha das listas e buscas sem apagar nada. */
    @PostMapping("/arquivar/{id}")
    public String arquivar(@PathVariable Long id,
                           @RequestParam(required = false) String retorno,
                           RedirectAttributes redirect) {
        buscaService.alterarArquivamentoFicha(id, true);
        redirect.addFlashAttribute("sucesso", "Ficha arquivada. Use \"Mostrar arquivadas\" para encontrá-la.");
        return "redirect:" + (retorno == null || retorno.isBlank() ? "/visualizar" : retorno);
    }

    @PostMapping("/desarquivar/{id}")
    public String desarquivar(@PathVariable Long id,
                              @RequestParam(required = false) String retorno,
                              RedirectAttributes redirect) {
        buscaService.alterarArquivamentoFicha(id, false);
        redirect.addFlashAttribute("sucesso", "Ficha restaurada e disponível para uso.");
        return "redirect:" + (retorno == null || retorno.isBlank() ? "/visualizar" : retorno);
    }

    @PostMapping("/excluir/{id}")
    @Transactional
    public String excluirReceita(@PathVariable Long id, RedirectAttributes redirect) {
        String nome = receitaRepository.findById(id).map(r -> r.getNome()).orElse("?");
        receitaIngredienteRepository.deleteByReceitaId(id);
        perfilNutricionalRepository.deleteByReceitaId(id);
        receitaRepository.deleteById(id);

        atividadeService.registrar(TipoAtividade.EXCLUSAO, AlvoAtividade.FICHA,
                "Ficha \"" + nome + "\" excluida", id);
        redirect.addFlashAttribute("sucesso", "Ficha excluída definitivamente.");
        return "redirect:/visualizar";
    }
}
