package com.mjwsolucoes.sistemanutricao.controller;

import com.mjwsolucoes.sistemanutricao.dto.UserDTO;
import com.mjwsolucoes.sistemanutricao.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nutricionistas")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> criar(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.criar(userDTO);
        return ResponseEntity.status(201).body(createdUser);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> listarTodos() {
        List<UserDTO> usuarios = userService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> buscarPorId(@PathVariable Long id) {
        UserDTO user = userService.buscarPorId(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> buscarPorUsername(@PathVariable String username) {
        UserDTO user = userService.buscarPorUsername(username);
        return ResponseEntity.ok(user);
    }
}