package com.mjwsolucoes.sistemanutricao.service;

import com.mjwsolucoes.sistemanutricao.dto.*;
import com.mjwsolucoes.sistemanutricao.model.*;
import com.mjwsolucoes.sistemanutricao.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReceitaService {

    private final ReceitaRepository receitaRepository;
    private final UserRepository userRepository;
    private final PerfilNutricionalRepository perfilNutricionalRepository;
    private final ReceitaIngredienteRepository receitaIngredienteRepository;
    private final IngredienteRepository ingredienteRepository;

    public ReceitaService(ReceitaRepository receitaRepository,
                          UserRepository userRepository,
                          PerfilNutricionalRepository perfilNutricionalRepository,
                          ReceitaIngredienteRepository receitaIngredienteRepository,
                          IngredienteRepository ingredienteRepository) {
        this.receitaRepository = receitaRepository;
        this.userRepository = userRepository;
        this.perfilNutricionalRepository = perfilNutricionalRepository;
        this.receitaIngredienteRepository = receitaIngredienteRepository;
        this.ingredienteRepository = ingredienteRepository;
    }

    @Transactional
    public ReceitaDTO criarReceita(ReceitaDTO receitaDTO, String usernameNutricionista) {
        System.out.println("ReceitaService - Recebendo username: '" + usernameNutricionista + "'");
        User nutricionista = userRepository.findByUsername(usernameNutricionista)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nutricionista não encontrado com username: " + usernameNutricionista));

        Receita receita = new Receita();
        receita.setNome(receitaDTO.getNome());
        try {
            receita.setCategoria(CategoriaReceita.valueOf(receitaDTO.getCategoria()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Categoria de receita inválida: " + receitaDTO.getCategoria(), e);
        }

        receita.setModoPreparo(receitaDTO.getModoPreparo());
        receita.setTempoPreparo(receitaDTO.getTempoPreparo());
        receita.setPesoPorcao(receitaDTO.getPesoPorcao());
        receita.setRendimento(receitaDTO.getRendimento());
        receita.setNumeroPorcoes(receitaDTO.getNumeroPorcoes());
        receita.setFcc(receitaDTO.getFcc());
        receita.setMedidaCaseira(receitaDTO.getMedidaCaseira());
        receita.setEquipamentos(receitaDTO.getEquipamentos());
        receita.setNutricionista(nutricionista);

        Receita receitaSalva = receitaRepository.save(receita);

        if (receitaDTO.getPerfilNutricional() != null) {
            salvarPerfilNutricional(receitaDTO.getPerfilNutricional(), receitaSalva);
        }

        if (receitaDTO.getIngredientes() != null && !receitaDTO.getIngredientes().isEmpty()) {
            salvarIngredientesReceita(receitaDTO.getIngredientes(), receitaSalva);
        }

        return convertToDTO(receitaSalva);
    }

    public List<ReceitaResumoDTO> listarTodas() {
        return receitaRepository.findAll().stream()
                .map(this::convertToResumoDTO)
                .collect(Collectors.toList());
    }

    public ReceitaDetalhadaDTO buscarDetalhesReceita(Long id) {
        Receita receita = receitaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receita não encontrada"));

        ReceitaDetalhadaDTO detalhes = new ReceitaDetalhadaDTO();
        detalhes.setReceita(convertToDTO(receita));

        List<ReceitaIngrediente> ingredientes = receitaIngredienteRepository.findByReceitaId(id);
        detalhes.setIngredientes(ingredientes.stream()
                .map(this::convertToIngredienteInfoDTO)
                .collect(Collectors.toList()));

        Optional<PerfilNutricional> perfilOpt = perfilNutricionalRepository.findByReceitaId(id);
        if (perfilOpt.isPresent()) {
            detalhes.setPerfilNutricional(convertToPerfilDTO(perfilOpt.get()));
        }

        return detalhes;
    }

    @Transactional
    public void salvarPerfilNutricional(PerfilNutricionalDTO perfilDTO, Receita receita) {
        PerfilNutricional perfil = new PerfilNutricional();
        perfil.setPerCapita(perfilDTO.getPerCapita());
        perfil.setTotalGramas(perfilDTO.getTotalGramas());
        perfil.setTotalKcal(perfilDTO.getTotalKcal());
        perfil.setTotalPorcentagem(perfilDTO.getTotalPorcentagem());
        perfil.setVct(perfilDTO.getVct());
        perfil.setReceita(receita);
        perfilNutricionalRepository.save(perfil);
    }

    @Transactional
    public void salvarIngredientesReceita(List<ReceitaIngredienteInputDTO> ingredientesDTO, Receita receita) {
        ingredientesDTO.forEach(ingDTO -> {
            // Busque o ingrediente primeiro
            Ingrediente ingrediente = ingredienteRepository.findById(ingDTO.getIngredienteId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingrediente não encontrado com ID: " + ingDTO.getIngredienteId()));

            ReceitaIngrediente ri = new ReceitaIngrediente();

            // Definir os IDs da chave composta
            ri.setReceitaId(receita.getId());
            ri.setIngredienteId(ingDTO.getIngredienteId());

            // Setar os objetos de relacionamento (isso é crucial para o JPA)
            ri.setReceita(receita);
            ri.setIngrediente(ingrediente);

            // Setar os demais atributos
            ri.setMedidaCaseira(ingDTO.getMedidaCaseira());
            ri.setPesoBruto(ingDTO.getPesoBruto());
            ri.setPesoLiquido(ingDTO.getPesoLiquido());
            ri.setFatorCorrecao(ingDTO.getFatorCorrecao());
            ri.setCustoCompra(ingDTO.getCustoCompra());
            ri.setCustoUtilizado(ingDTO.getCustoUtilizado());

            // Definir valores padrão ou calculados para custoTotal e custoPercapita,
            // já que não vêm do DTO de entrada.
            ri.setCustoTotal(0.0);       // Valor padrão, se não for calculado aqui
            ri.setCustoPercapita(0.0);   // Valor padrão, se não for calculado aqui

            // Log para verificar os valores antes de salvar (útil para depuração)
            System.out.println("Salvando ReceitaIngrediente para Receita ID: " + ri.getReceitaId() +
                    ", Ingrediente ID: " + ri.getIngredienteId() +
                    ", Peso Líquido: " + ri.getPesoLiquido());

            receitaIngredienteRepository.save(ri);
        });
    }

    private ReceitaDTO convertToDTO(Receita receita) {
        ReceitaDTO dto = new ReceitaDTO();
        dto.setId(receita.getId());
        dto.setNome(receita.getNome());
        dto.setCategoria(receita.getCategoria().name());
        dto.setModoPreparo(receita.getModoPreparo());
        dto.setTempoPreparo(receita.getTempoPreparo());
        dto.setPesoPorcao(receita.getPesoPorcao());
        dto.setRendimento(receita.getRendimento());
        dto.setNumeroPorcoes(receita.getNumeroPorcoes());
        dto.setFcc(receita.getFcc());
        dto.setMedidaCaseira(receita.getMedidaCaseira());
        dto.setEquipamentos(receita.getEquipamentos());
        if (receita.getNutricionista() != null) {
            dto.setUserId(receita.getNutricionista().getId());
            dto.setNutricionistaUsername(receita.getNutricionista().getUsername());
        }
        return dto;
    }

    private ReceitaResumoDTO convertToResumoDTO(Receita receita) {
        ReceitaResumoDTO dto = new ReceitaResumoDTO();
        dto.setId(receita.getId());
        dto.setNome(receita.getNome());
        dto.setCategoria(receita.getCategoria().name());
        dto.setTempoPreparo(receita.getTempoPreparo());
        dto.setNumeroPorcoes(receita.getNumeroPorcoes());
        if (receita.getNutricionista() != null) {
            dto.setNutricionistaUsername(receita.getNutricionista().getUsername());
        }

        Optional<PerfilNutricional> perfilOpt = perfilNutricionalRepository.findByReceitaId(receita.getId());
        perfilOpt.ifPresent(perfil -> dto.setTotalKcal(perfil.getTotalKcal()));

        return dto;
    }

    private IngredienteInfoDTO convertToIngredienteInfoDTO(ReceitaIngrediente ri) {
        IngredienteInfoDTO info = new IngredienteInfoDTO();
        info.setId(ri.getIngredienteId());
        info.setQuantidade(ri.getPesoLiquido());
        info.setMedida("g");
        info.setCustoPorcao(ri.getCustoPercapita());

        if (ri.getIngrediente() != null) {
            info.setNome(ri.getIngrediente().getNome());
            info.setTipo(ri.getIngrediente().isIngredienteSistema() ? "ORIGINAL" : "NUTRICIONISTA");
        } else {
            info.setNome("Ingrediente Desconhecido");
            info.setTipo("DESCONHECIDO");
        }
        return info;
    }

    private PerfilNutricionalDTO convertToPerfilDTO(PerfilNutricional perfil) {
        PerfilNutricionalDTO dto = new PerfilNutricionalDTO();
        dto.setId(perfil.getId());
        dto.setPerCapita(perfil.getPerCapita());
        dto.setTotalGramas(perfil.getTotalGramas());
        dto.setTotalKcal(perfil.getTotalKcal());
        dto.setTotalPorcentagem(perfil.getTotalPorcentagem());
        dto.setVct(perfil.getVct());
        if (perfil.getReceita() != null) {
            dto.setReceitaId(perfil.getReceita().getId());
        }
        return dto;
    }
}