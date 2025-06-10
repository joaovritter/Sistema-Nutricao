package com.mjwsolucoes.sistemanutricao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

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

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

}
