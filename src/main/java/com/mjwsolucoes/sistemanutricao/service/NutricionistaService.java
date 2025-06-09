package com.mjwsolucoes.sistemanutricao.service;

import com.mjwsolucoes.sistemanutricao.dto.NutricionistaDTO;
import com.mjwsolucoes.sistemanutricao.model.Nutricionista;
import com.mjwsolucoes.sistemanutricao.repository.NutricionistaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NutricionistaService {

    private final NutricionistaRepository nutricionistaRepository;

    public NutricionistaService(NutricionistaRepository nutricionistaRepository) {
        this.nutricionistaRepository = nutricionistaRepository;
    }

    public List<NutricionistaDTO> listarTodos() {
        return nutricionistaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public NutricionistaDTO buscarPorId(Long id) {
        return nutricionistaRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Nutricionista não encontrado"));
    }

    public NutricionistaDTO buscarPorUsername(String username) {
        return nutricionistaRepository.findByUsername(username)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Nutricionista não encontrado"));
    }


    private NutricionistaDTO convertToDTO(Nutricionista nutricionista) {
        NutricionistaDTO dto = new NutricionistaDTO();
        dto.setId(nutricionista.getId());
        dto.setUsername(nutricionista.getUsername());
        dto.setRole(nutricionista.getRole());
        dto.setTotalReceitas(nutricionista.getReceitas() != null ? nutricionista.getReceitas().size() : 0);
        dto.setTotalIngredientesCriados(nutricionista.getIngredientesCriados() != null ?
                nutricionista.getIngredientesCriados().size() : 0);
        return dto;
    }
}