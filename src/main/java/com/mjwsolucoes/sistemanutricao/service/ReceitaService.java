package com.mjwsolucoes.sistemanutricao.service;

import com.mjwsolucoes.sistemanutricao.dto.*;
import com.mjwsolucoes.sistemanutricao.model.*;
import com.mjwsolucoes.sistemanutricao.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReceitaService {

    private final ReceitaRepository receitaRepository;
    private final NutricionistaRepository nutricionistaRepository;
    private final PerfilNutricionalRepository perfilNutricionalRepository;
    private final ReceitaIngredienteRepository receitaIngredienteRepository;

    public ReceitaService(ReceitaRepository receitaRepository,
                          NutricionistaRepository nutricionistaRepository,
                          PerfilNutricionalRepository perfilNutricionalRepository,
                          ReceitaIngredienteRepository receitaIngredienteRepository) {
        this.receitaRepository = receitaRepository;
        this.nutricionistaRepository = nutricionistaRepository;
        this.perfilNutricionalRepository = perfilNutricionalRepository;
        this.receitaIngredienteRepository = receitaIngredienteRepository;
    }

    @Transactional
    public ReceitaDTO criarReceita(ReceitaDTO receitaDTO, String usernameNutricionista) {
        Nutricionista nutricionista = nutricionistaRepository.findByUsername(usernameNutricionista)
                .orElseThrow(() -> new RuntimeException("Nutricionista não encontrado"));

        Receita receita = new Receita();
        receita.setNome(receitaDTO.getNome());
        receita.setCategoria(receitaDTO.getCategoria());
        receita.setModoPreparo(receitaDTO.getModoPreparo());
        receita.setTempoPreparo(receitaDTO.getTempoPreparo());
        receita.setPesoPorcao(receitaDTO.getPesoPorcao());
        receita.setRendimento(receitaDTO.getRendimento());
        receita.setNumeroPorcoes(receitaDTO.getNumeroPorcoes());
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
                .orElseThrow(() -> new RuntimeException("Receita não encontrada"));

        ReceitaDetalhadaDTO detalhes = new ReceitaDetalhadaDTO();
        detalhes.setReceita(convertToDTO(receita));

        List<ReceitaIngrediente> ingredientes = receitaIngredienteRepository.findByReceitaId(id);
        detalhes.setIngredientes(ingredientes.stream()
                .map(this::convertToIngredienteInfoDTO)
                .collect(Collectors.toList()));

        // Substitua a linha problemática por:
        Optional<PerfilNutricional> perfilOpt = Optional.ofNullable(perfilNutricionalRepository.findByReceitaId(id));
        if (perfilOpt.isPresent()) {
            detalhes.setPerfilNutricional(convertToPerfilDTO(perfilOpt.get()));
        }

        return detalhes;
    }

    private void salvarPerfilNutricional(PerfilNutricionalDTO perfilDTO, Receita receita) {
        PerfilNutricional perfil = new PerfilNutricional();
        perfil.setPerCapita(perfilDTO.getPerCapita());
        perfil.setTotalGramas(perfilDTO.getTotalGramas());
        perfil.setTotalKcal(perfilDTO.getTotalKcal());
        perfil.setTotalPorcentagem(perfilDTO.getTotalPorcentagem());
        perfil.setVct(perfilDTO.getVct());
        perfil.setReceita(receita);
        perfilNutricionalRepository.save(perfil);
    }

    private void salvarIngredientesReceita(List<ReceitaIngredienteDTO> ingredientesDTO, Receita receita) {
        ingredientesDTO.forEach(ingDTO -> {
            ReceitaIngrediente ri = new ReceitaIngrediente();
            ri.setMedidaCaseira(ingDTO.getMedidaCaseira());
            ri.setPesoBruto(ingDTO.getPesoBruto());
            ri.setPesoLiquido(ingDTO.getPesoLiquido());
            ri.setFatorCorrecao(ingDTO.getFatorCorrecao());
            ri.setCustoCompra(ingDTO.getCustoCompra());
            ri.setCustoUtilizado(ingDTO.getCustoUtilizado());
            ri.setCustoTotal(ingDTO.getCustoTotal());
            ri.setCustoPercapita(ingDTO.getCustoPercapita());
            ri.setReceita(receita);
            // Aqui você precisará buscar o ingrediente ou ingredienteNutricionista pelo ID
            // e setar no ReceitaIngrediente
            receitaIngredienteRepository.save(ri);
        });
    }

    private ReceitaDTO convertToDTO(Receita receita) {
        ReceitaDTO dto = new ReceitaDTO();
        dto.setId(receita.getId());
        dto.setNome(receita.getNome());
        dto.setCategoria(receita.getCategoria());
        dto.setModoPreparo(receita.getModoPreparo());
        dto.setTempoPreparo(receita.getTempoPreparo());
        dto.setPesoPorcao(receita.getPesoPorcao());
        dto.setRendimento(receita.getRendimento());
        dto.setNumeroPorcoes(receita.getNumeroPorcoes());
        dto.setNutricionistaId(receita.getNutricionista().getId());
        dto.setNutricionistaUsername(receita.getNutricionista().getUsername());
        return dto;
    }

    private ReceitaResumoDTO convertToResumoDTO(Receita receita) {
        ReceitaResumoDTO dto = new ReceitaResumoDTO();
        dto.setId(receita.getId());
        dto.setNome(receita.getNome());
        dto.setCategoria(receita.getCategoria());
        dto.setTempoPreparo(receita.getTempoPreparo());
        dto.setNumeroPorcoes(receita.getNumeroPorcoes());
        dto.setNutricionistaUsername(receita.getNutricionista().getUsername());

        // Substitua a linha problemática por:
        Optional<PerfilNutricional> perfilOpt = Optional.ofNullable(perfilNutricionalRepository.findByReceitaId(receita.getId()));
        perfilOpt.ifPresent(perfil -> dto.setTotalKcal(perfil.getTotalKcal()));

        return dto;
    }

    private IngredienteInfoDTO convertToIngredienteInfoDTO(ReceitaIngrediente ri) {
        IngredienteInfoDTO info = new IngredienteInfoDTO();
        info.setId(ri.getId());
        info.setQuantidade(ri.getPesoLiquido());
        info.setMedida("g"); // Ou outra unidade de medida
        info.setCustoPorcao(ri.getCustoPercapita());

        if (ri.getIngrediente() != null) {
            info.setNome(ri.getIngrediente().getNome());
            info.setTipo("ORIGINAL");
        } else if (ri.getIngredienteNutricionista() != null) {
            info.setNome(ri.getIngredienteNutricionista().getNome());
            info.setTipo("NUTRICIONISTA");
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
        dto.setReceitaId(perfil.getReceita().getId());
        return dto;
    }
}