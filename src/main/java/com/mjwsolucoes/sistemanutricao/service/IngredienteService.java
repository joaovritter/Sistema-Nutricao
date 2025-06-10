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
    private final IngredienteUserRepository ingredienteUserRepository;
    private final UserRepository userRepository;

    public IngredienteService(IngredienteRepository ingredienteRepository,
                              IngredienteUserRepository ingredienteUserRepository,
                              UserRepository userRepository) {
        this.ingredienteRepository = ingredienteRepository;
        this.ingredienteUserRepository = ingredienteUserRepository;
        this.userRepository = userRepository;
    }

    public List<IngredienteDTO> listarIngredientes() {
        return ingredienteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<IngredienteUserDTO> listarIngredientesNutricionista(String usernameNutricionista) {
        User nutricionista = userRepository.findByUsername(usernameNutricionista)
                .orElseThrow(() -> new RuntimeException("Nutricionista não encontrado"));
        return ingredienteUserRepository.findByNutricionistaId(nutricionista.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public IngredienteUserDTO criarIngredienteNutricionista(
            IngredienteUserDTO ingredienteDTO, String usernameNutricionista) {
        User nutricionista = userRepository.findByUsername(usernameNutricionista)
                .orElseThrow(() -> new RuntimeException("Nutricionista não encontrado"));

        IngredienteNutricionista ingrediente = new IngredienteNutricionista();
        ingrediente.setNome(ingredienteDTO.getNome());
        ingrediente.setCarboidrato(ingredienteDTO.getCarboidrato());
        ingrediente.setProteina(ingredienteDTO.getProteina());
        ingrediente.setLipido(ingredienteDTO.getLipido());
        ingrediente.setSodio(ingredienteDTO.getSodio());
        ingrediente.setGorduraSaturada(ingredienteDTO.getGorduraSaturada());
        ingrediente.setNutricionista(nutricionista);

        IngredienteNutricionista salvo = ingredienteUserRepository.save(ingrediente);
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

    private IngredienteUserDTO convertToDTO(IngredienteNutricionista ingrediente) {
        IngredienteUserDTO dto = new IngredienteUserDTO();
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