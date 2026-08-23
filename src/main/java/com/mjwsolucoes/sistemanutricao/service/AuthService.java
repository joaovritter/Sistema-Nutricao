package com.mjwsolucoes.sistemanutricao.service;

import com.mjwsolucoes.sistemanutricao.dto.LoginDTO;
import com.mjwsolucoes.sistemanutricao.dto.RegistroDTO;
import com.mjwsolucoes.sistemanutricao.model.Role;
import com.mjwsolucoes.sistemanutricao.model.User;
import com.mjwsolucoes.sistemanutricao.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    /** Motivo pelo qual um registro foi recusado — vira mensagem na tela. */
    public enum ResultadoRegistro {
        SUCESSO(null),
        USUARIO_EXISTENTE("Este nome de usuário já está em uso."),
        SENHAS_DIFERENTES("As senhas não conferem. Digite a mesma senha nos dois campos."),
        SENHA_CURTA("A senha deve ter no mínimo 6 caracteres."),
        DADOS_INCOMPLETOS("Preencha todos os campos.");

        private final String mensagem;

        ResultadoRegistro(String mensagem) {
            this.mensagem = mensagem;
        }

        public String getMensagem() {
            return mensagem;
        }

        public boolean isSucesso() {
            return this == SUCESSO;
        }
    }

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Cria a conta em estado PENDENTE/inativo. O usuário só consegue entrar
     * depois que um administrador atribuir um cargo e ativar o acesso.
     */
    public ResultadoRegistro registrar(RegistroDTO registroDTO) {
        if (vazio(registroDTO.getNome()) || vazio(registroDTO.getUsername())
                || vazio(registroDTO.getPassword()) || vazio(registroDTO.getConfirmarPassword())) {
            return ResultadoRegistro.DADOS_INCOMPLETOS;
        }
        if (!registroDTO.getPassword().equals(registroDTO.getConfirmarPassword())) {
            return ResultadoRegistro.SENHAS_DIFERENTES;
        }
        if (registroDTO.getPassword().length() < 6) {
            return ResultadoRegistro.SENHA_CURTA;
        }
        if (userRepository.existsByUsername(registroDTO.getUsername().trim())) {
            return ResultadoRegistro.USUARIO_EXISTENTE;
        }

        User novo = new User();
        novo.setNome(registroDTO.getNome().trim());
        novo.setUsername(registroDTO.getUsername().trim());
        novo.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        novo.setRole(Role.PENDENTE);
        novo.setAtivo(false);
        novo.setCriadoEm(LocalDateTime.now());

        userRepository.save(novo);
        return ResultadoRegistro.SUCESSO;
    }

    public String autenticar(LoginDTO loginDTO) {
        Optional<User> userOpt = userRepository.findByUsername(loginDTO.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.isAtivo() && passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                return user.getRole().name();
            }
        }
        return null;
    }

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}
