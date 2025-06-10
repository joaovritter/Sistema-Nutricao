package com.mjwsolucoes.sistemanutricao.repository;

import com.mjwsolucoes.sistemanutricao.model.IngredienteNutricionista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredienteUserRepository extends JpaRepository<IngredienteNutricionista, Long> {

    // Busca ingredientes criados por um nutricionista
    List<IngredienteNutricionista> findByNutricionistaId(Long nutricionistaId);

    // Busca por nome e nutricionista
    IngredienteNutricionista findByNomeAndNutricionistaId(String nome, Long nutricionistaId);

    // Busca ingredientes contendo parte do nome (case insensitive) para um nutricionista
    List<IngredienteNutricionista> findByNomeContainingIgnoreCaseAndNutricionistaId(String nome, Long nutricionistaId);

    // Verifica se nome já existe para o nutricionista
    boolean existsByNomeAndNutricionistaId(String nome, Long nutricionistaId);

    // Busca personalizada por faixa de valores nutricionais
    @Query("SELECT i FROM IngredienteNutricionista i WHERE " +
            "i.nutricionista.id = :nutricionistaId AND " +
            "i.proteina BETWEEN :minProteina AND :maxProteina")
    List<IngredienteNutricionista> findByNutricionistaAndProteinaRange(
            @Param("nutricionistaId") Long nutricionistaId,
            @Param("minProteina") double minProteina,
            @Param("maxProteina") double maxProteina);
}