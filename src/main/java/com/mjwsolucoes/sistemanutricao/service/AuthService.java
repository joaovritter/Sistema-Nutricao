package com.mjwsolucoes.sistemanutricao.service;

import com.mjwsolucoes.sistemanutricao.dto.LoginDTO;
import com.mjwsolucoes.sistemanutricao.dto.RegistroDTO;
import com.mjwsolucoes.sistemanutricao.model.Estabelecimento;
import com.mjwsolucoes.sistemanutricao.model.Nutricionista;
import com.mjwsolucoes.sistemanutricao.repository.EstabelecimentoRepository;
import com.mjwsolucoes.sistemanutricao.repository.NutricionistaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final NutricionistaRepository nutricionistaRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(NutricionistaRepository nutricionistaRepository,
                       EstabelecimentoRepository estabelecimentoRepository,
                       PasswordEncoder passwordEncoder) {
        this.nutricionistaRepository = nutricionistaRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registrar(RegistroDTO registroDTO) {
        if (registroDTO.getRole().equals("NUTRICIONISTA")) {
            if (nutricionistaRepository.existsByUsername(registroDTO.getUsername())) {
                return false;
            }

            Nutricionista nutricionista = new Nutricionista();
            nutricionista.setUsername(registroDTO.getUsername());
            nutricionista.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
            nutricionista.setRole("NUTRICIONISTA");
            nutricionistaRepository.save(nutricionista);
            return true;
        } else if (registroDTO.getRole().equals("ESTABELECIMENTO")) {
            if (estabelecimentoRepository.existsByUsername(registroDTO.getUsername())) {
                return false;
            }

            Estabelecimento estabelecimento = new Estabelecimento();
            estabelecimento.setUsername(registroDTO.getUsername());
            estabelecimento.setSenha(passwordEncoder.encode(registroDTO.getPassword()));
            estabelecimento.setRole("ESTABELECIMENTO");
            estabelecimentoRepository.save(estabelecimento);
            return true;
        }
        return false;
    }

    public boolean autenticar(LoginDTO loginDTO) {
        // Verifica se é nutricionista
        Optional<Nutricionista> nutricionistaOpt = nutricionistaRepository.findByUsername(loginDTO.getUsername());
        if (nutricionistaOpt.isPresent()) {
            return passwordEncoder.matches(loginDTO.getPassword(), nutricionistaOpt.get().getPassword());
        }

        // Verifica se é estabelecimento
        Optional<Estabelecimento> estabelecimentoOpt = estabelecimentoRepository.findByUsername(loginDTO.getUsername());
        if (estabelecimentoOpt.isPresent()) {
            return passwordEncoder.matches(loginDTO.getPassword(), estabelecimentoOpt.get().getSenha());
        }

        return false;
    }
}