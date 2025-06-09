package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.dto.LoginDTO;
import com.mjwsolucoes.sistemanutricao.dto.RegistroDTO;
import com.mjwsolucoes.sistemanutricao.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        boolean autenticado = authService.autenticar(loginDTO);
        if (autenticado) {
            return ResponseEntity.ok().body("Login bem-sucedido");
        }
        return ResponseEntity.status(401).body("Credenciais inválidas");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistroDTO registroDTO) {
        boolean registrado = authService.registrar(registroDTO);
        if (registrado) {
            return ResponseEntity.ok().body("Usuário registrado com sucesso");
        }
        return ResponseEntity.badRequest().body("Nome de usuário já existe ou tipo de usuário inválido");
    }
}