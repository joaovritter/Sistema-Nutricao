package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.model.Role;
import com.mjwsolucoes.sistemanutricao.service.AdminUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/** Painel do administrador: aprova solicitações e define o cargo de cada usuário. */
@Controller
@RequestMapping("/admin")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("pendentes", adminUserService.listarPendentes());
        model.addAttribute("usuarios", adminUserService.listarAvaliados());
        model.addAttribute("cargos", Role.atribuiveis());
        return "admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/cargo")
    public String definirCargo(@PathVariable Long id,
                               @RequestParam Role cargo,
                               Principal principal,
                               RedirectAttributes redirect) {
        try {
            adminUserService.definirCargo(id, cargo, principal.getName());
            redirect.addFlashAttribute("sucesso", "Cargo atualizado para " + cargo.getDescricao() + ".");
        } catch (ResponseStatusException e) {
            redirect.addFlashAttribute("erro", e.getReason());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/ativacao")
    public String alterarAtivacao(@PathVariable Long id,
                                  @RequestParam boolean ativo,
                                  Principal principal,
                                  RedirectAttributes redirect) {
        try {
            adminUserService.alterarAtivacao(id, ativo, principal.getName());
            redirect.addFlashAttribute("sucesso", ativo ? "Acesso liberado." : "Acesso suspenso.");
        } catch (ResponseStatusException e) {
            redirect.addFlashAttribute("erro", e.getReason());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/recusar")
    public String recusar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            adminUserService.recusarSolicitacao(id);
            redirect.addFlashAttribute("sucesso", "Solicitação recusada.");
        } catch (ResponseStatusException e) {
            redirect.addFlashAttribute("erro", e.getReason());
        }
        return "redirect:/admin/usuarios";
    }
}
