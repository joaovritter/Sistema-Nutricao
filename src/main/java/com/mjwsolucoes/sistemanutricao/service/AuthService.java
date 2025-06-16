package com.mjwsolucoes.sistemanutricao.service;

import com.mjwsolucoes.sistemanutricao.dto.LoginDTO;
import com.mjwsolucoes.sistemanutricao.dto.RegistroDTO;
import com.mjwsolucoes.sistemanutricao.model.User;
import com.mjwsolucoes.sistemanutricao.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.mjwsolucoes.sistemanutricao.model.Role.USER; // Importe apenas USER, se NUTRICIONISTA não for padrão de registro

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registrar(RegistroDTO registroDTO) {
        // Verifica se o username já existe no banco de dados
        if (userRepository.existsByUsername(registroDTO.getUsername())) {
            return false; // O username já existe, não é possível registrar
        }

        // Cria um novo usuário
        User newUser = new User();
        newUser.setUsername(registroDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(registroDTO.getPassword()));

        // Define a role como USER por padrão
        newUser.setRole(USER);

        // Salva o novo usuário no banco de dados
        userRepository.save(newUser);
        return true;
    }

    public String autenticar(LoginDTO loginDTO) {
        Optional<User> userOpt = userRepository.findByUsername(loginDTO.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                return user.getRole().name(); // Retorna "NUTRICIONISTA", "USER" ou "ADMIN"
            }
        }
        return null; // Autenticação falhou
    }
}