// src/main/java/com/mjwsolucoes/sistemanutricao/service/IngredienteService.java
package com.mjwsolucoes.sistemanutricao.service;

import com.mjwsolucoes.sistemanutricao.dto.*;
import com.mjwsolucoes.sistemanutricao.model.*;
import com.mjwsolucoes.sistemanutricao.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importe Transactional

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;
    private final UserRepository userRepository;

    public IngredienteService(IngredienteRepository ingredienteRepository,
                              UserRepository userRepository) {
        this.ingredienteRepository = ingredienteRepository;
        this.userRepository = userRepository;
    }

    // Lista todos os ingredientes (incluindo do sistema e de nutricionistas)
    public List<IngredienteDTO> listarIngredientes() {
        return ingredienteRepository.findAll().stream()
                .map(this::convertToDTO) // Mapeia para IngredienteDTO (mais genérico)
                .collect(Collectors.toList());
    }

    // Lista apenas os ingredientes criados por um nutricionista específico
    public List<IngredienteUserDTO> listarIngredientesNutricionista(String usernameNutricionista) {
        User nutricionista = userRepository.findByUsername(usernameNutricionista)
                .orElseThrow(() -> new RuntimeException("Nutricionista não encontrado com username: " + usernameNutricionista));

        // Usa o novo método do repositório para filtrar ingredientes de nutricionista
        return ingredienteRepository.findByNutricionistaIdAndIsIngredienteSistemaFalse(nutricionista.getId()).stream()
                .map(this::convertToUserDTO) // Mapeia para IngredienteUserDTO (com info do nutricionista)
                .collect(Collectors.toList());
    }

    @Transactional // Garante que a operação seja transacional
    public IngredienteUserDTO criarIngredienteNutricionista(
            IngredienteUserDTO ingredienteDTO, String usernameNutricionista) {
        User nutricionista = userRepository.findByUsername(usernameNutricionista)
                .orElseThrow(() -> new RuntimeException("Nutricionista não encontrado com username: " + usernameNutricionista));

        // Cria uma nova entidade Ingrediente
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNome(ingredienteDTO.getNome());
        ingrediente.setCarboidrato(ingredienteDTO.getCarboidrato());
        ingrediente.setProteina(ingredienteDTO.getProteina());
        ingrediente.setLipidio(ingredienteDTO.getLipidio());
        ingrediente.setSodio(ingredienteDTO.getSodio());
        ingrediente.setGorduraSaturada(ingredienteDTO.getGorduraSaturada());

        // Define que este ingrediente NÃO é do sistema
        ingrediente.setIngredienteSistema(false);
        // Associa o objeto User nutricionista (e não apenas o ID)
        ingrediente.setNutricionista(nutricionista);

        // Salva o ingrediente (agora uma instância de Ingrediente, não IngredienteNutricionista)
        Ingrediente salvo = ingredienteRepository.save(ingrediente);

        // Converte o ingrediente salvo de volta para IngredienteUserDTO para o retorno
        return convertToUserDTO(salvo);
    }

    // Busca ingredientes por nome (funciona para todos os tipos de ingredientes)
    public List<IngredienteDTO> buscarIngredientesPorNome(String nome) {
        return ingredienteRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(this::convertToDTO) // Mapeia para o DTO geral
                .collect(Collectors.toList());
    }

    // Método de conversão para IngredienteDTO (para listar ingredientes gerais)
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

    // Método de conversão para IngredienteUserDTO (para listar/criar ingredientes de nutricionista)
    // Este método substitui e absorve a lógica do antigo convertToDTO(IngredienteNutricionista)
    private IngredienteUserDTO convertToUserDTO(Ingrediente ingrediente) {
        IngredienteUserDTO dto = new IngredienteUserDTO();
        dto.setId(ingrediente.getId());
        dto.setNome(ingrediente.getNome());
        dto.setCarboidrato(ingrediente.getCarboidrato());
        dto.setProteina(ingrediente.getProteina());
        dto.setLipidio(ingrediente.getLipidio());
        dto.setSodio(ingrediente.getSodio());
        dto.setGorduraSaturada(ingrediente.getGorduraSaturada());

        // Popula as informações do nutricionista se o ingrediente não for do sistema
        if (ingrediente.getNutricionista() != null) {
            dto.setNutricionistaId(ingrediente.getNutricionista().getId());
            dto.setNutricionistaUsername(ingrediente.getNutricionista().getUsername());
        }
        return dto;
    }
}