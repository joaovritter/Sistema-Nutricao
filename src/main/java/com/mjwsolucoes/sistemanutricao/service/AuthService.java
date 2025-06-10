package com.mjwsolucoes.sistemanutricao.service;

import com.mjwsolucoes.sistemanutricao.dto.LoginDTO;
import com.mjwsolucoes.sistemanutricao.dto.RegistroDTO;
import com.mjwsolucoes.sistemanutricao.model.User;
import com.mjwsolucoes.sistemanutricao.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.mjwsolucoes.sistemanutricao.model.Role.NUTRICIONISTA;
import static com.mjwsolucoes.sistemanutricao.model.Role.USER;

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
        if (registroDTO.getRole().equals(NUTRICIONISTA)) {
            if (userRepository.existsByUsername(registroDTO.getUsername())) {
                return false;
            }

            User nutricionista = new User();
            nutricionista.setUsername(registroDTO.getUsername());
            nutricionista.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
            nutricionista.setRole(NUTRICIONISTA);
            userRepository.save(nutricionista);
            return true;
        } else if (registroDTO.getRole().equals(USER)) {
            if (userRepository.existsByUsername(registroDTO.getUsername())) {
                return false;
            }

            User user = new User();
            user.setUsername(registroDTO.getUsername());
            user.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
            user.setRole(USER);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public String autenticar(LoginDTO loginDTO) {
        Optional<User> userOpt = userRepository.findByUsername(loginDTO.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                return user.getRole().name(); // Retorna "NUTRICIONISTA", "USER" ou "ADMIN"
            }
        }
        return null;
    }
}