package com.mjwsolucoes.sistemanutricao.service;

import com.mjwsolucoes.sistemanutricao.dto.*;
import com.mjwsolucoes.sistemanutricao.model.*;
import com.mjwsolucoes.sistemanutricao.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;
    private final IngredienteNutricionistaRepository ingredienteNutricionistaRepository;
    private final NutricionistaRepository nutricionistaRepository;

    public IngredienteService(IngredienteRepository ingredienteRepository,
                              IngredienteNutricionistaRepository ingredienteNutricionistaRepository,
                              NutricionistaRepository nutricionistaRepository) {
        this.ingredienteRepository = ingredienteRepository;
        this.ingredienteNutricionistaRepository = ingredienteNutricionistaRepository;
        this.nutricionistaRepository = nutricionistaRepository;
    }

    public List<IngredienteDTO> listarIngredientes() {
        return ingredienteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<IngredienteNutricionistaDTO> listarIngredientesNutricionista(String usernameNutricionista) {
        Nutricionista nutricionista = nutricionistaRepository.findByUsername(usernameNutricionista)
                .orElseThrow(() -> new RuntimeException("Nutricionista não encontrado"));
        return ingredienteNutricionistaRepository.findByNutricionistaId(nutricionista.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public IngredienteNutricionistaDTO criarIngredienteNutricionista(
            IngredienteNutricionistaDTO ingredienteDTO, String usernameNutricionista) {
        Nutricionista nutricionista = nutricionistaRepository.findByUsername(usernameNutricionista)
                .orElseThrow(() -> new RuntimeException("Nutricionista não encontrado"));

        IngredienteNutricionista ingrediente = new IngredienteNutricionista();
        ingrediente.setNome(ingredienteDTO.getNome());
        ingrediente.setCarboidrato(ingredienteDTO.getCarboidrato());
        ingrediente.setProteina(ingredienteDTO.getProteina());
        ingrediente.setLipido(ingredienteDTO.getLipido());
        ingrediente.setSodio(ingredienteDTO.getSodio());
        ingrediente.setGorduraSaturada(ingredienteDTO.getGorduraSaturada());
        ingrediente.setNutricionista(nutricionista);

        IngredienteNutricionista salvo = ingredienteNutricionistaRepository.save(ingrediente);
        return convertToDTO(salvo);
    }

    public List<IngredienteDTO> buscarIngredientesPorNome(String nome) {
        return ingredienteRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private IngredienteDTO convertToDTO(Ingrediente ingrediente) {
        IngredienteDTO dto = new IngredienteDTO();
        dto.setId(ingrediente.getId());
        dto.setNome(ingrediente.getNome());
        dto.setProteina(ingrediente.getProteina());
        dto.setCarboidrato(ingrediente.getCarboidrato());
        dto.setLipidio(ingrediente.getLipidio());
        dto.setSodio(ingrediente.getSodio());
        dto.setGorduraSaturada(ingrediente.getGorduraSaturada());
        return dto;
    }

    private IngredienteNutricionistaDTO convertToDTO(IngredienteNutricionista ingrediente) {
        IngredienteNutricionistaDTO dto = new IngredienteNutricionistaDTO();
        dto.setId(ingrediente.getId());
        dto.setNome(ingrediente.getNome());
        dto.setCarboidrato(ingrediente.getCarboidrato());
        dto.setProteina(ingrediente.getProteina());
        dto.setLipido(ingrediente.getLipido());
        dto.setSodio(ingrediente.getSodio());
        dto.setGorduraSaturada(ingrediente.getGorduraSaturada());
        dto.setNutricionistaId(ingrediente.getNutricionista().getId());
        dto.setNutricionistaUsername(ingrediente.getNutricionista().getUsername());
        return dto;
    }
}