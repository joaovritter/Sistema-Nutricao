package com.mjwsolucoes.sistemanutricao.configuration;

import com.mjwsolucoes.sistemanutricao.model.Estabelecimento;
import com.mjwsolucoes.sistemanutricao.model.Nutricionista;
import com.mjwsolucoes.sistemanutricao.repository.EstabelecimentoRepository;
import com.mjwsolucoes.sistemanutricao.repository.NutricionistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final NutricionistaRepository nutricionistaRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Primeiro tenta encontrar como nutricionista
        Optional<Nutricionista> nutricionistaOpt = nutricionistaRepository.findByUsername(username);
        if (nutricionistaOpt.isPresent()) {
            Nutricionista nutricionista = nutricionistaOpt.get();
            return new User(
                    nutricionista.getUsername(),
                    nutricionista.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_NUTRICIONISTA"))
            );
        }

        // Se não for nutricionista, tenta como estabelecimento
        Optional<Estabelecimento> estabelecimentoOpt = estabelecimentoRepository.findByUsername(username);
        if (estabelecimentoOpt.isPresent()) {
            Estabelecimento estabelecimento = estabelecimentoOpt.get();
            return new User(
                    estabelecimento.getUsername(),
                    estabelecimento.getSenha(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ESTABELECIMENTO"))
            );
        }

        throw new UsernameNotFoundException("Usuário não encontrado: " + username);
    }
}