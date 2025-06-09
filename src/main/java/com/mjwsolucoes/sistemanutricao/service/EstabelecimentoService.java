package com.mjwsolucoes.sistemanutricao.service;

import com.mjwsolucoes.sistemanutricao.dto.EstabelecimentoDTO;
import com.mjwsolucoes.sistemanutricao.model.Estabelecimento;
import com.mjwsolucoes.sistemanutricao.repository.EstabelecimentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstabelecimentoService {

    private final EstabelecimentoRepository estabelecimentoRepository;

    public EstabelecimentoService(EstabelecimentoRepository estabelecimentoRepository) {
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    public List<EstabelecimentoDTO> listarTodos() {
        return estabelecimentoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EstabelecimentoDTO buscarPorUsername(String username) {
        return estabelecimentoRepository.findByUsername(username)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado"));
    }

    private EstabelecimentoDTO convertToDTO(Estabelecimento estabelecimento) {
        EstabelecimentoDTO dto = new EstabelecimentoDTO();
        dto.setId(estabelecimento.getId());
        dto.setUsername(estabelecimento.getUsername());
        dto.setRole(estabelecimento.getRole());
        return dto;
    }
}