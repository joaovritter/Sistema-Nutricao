package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.dto.RegistroDTO;
import com.mjwsolucoes.sistemanutricao.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "index";
    }

    @GetMapping("/registro")
    public String showRegisterForm(Model model) {
        model.addAttribute("registro", new RegistroDTO());
        return "registro";
    }

    @PostMapping("/registro")
    public String processRegister(@ModelAttribute("registro") RegistroDTO registroDTO,
                                  Model model,
                                  RedirectAttributes redirect) {
        AuthService.ResultadoRegistro resultado = authService.registrar(registroDTO);

        if (resultado.isSucesso()) {
            redirect.addFlashAttribute("registroEnviado", true);
            return "redirect:/registro/enviado";
        }

        // Reexibe o formulário preservando nome e usuário digitados, só limpando as senhas.
        registroDTO.setPassword(null);
        registroDTO.setConfirmarPassword(null);
        model.addAttribute("erro", resultado.getMensagem());
        return "registro";
    }

    /** Confirmação de que a solicitação foi enviada e aguarda liberação do administrador. */
    @GetMapping("/registro/enviado")
    public String registroEnviado() {
        return "registroEnviado";
    }
}
