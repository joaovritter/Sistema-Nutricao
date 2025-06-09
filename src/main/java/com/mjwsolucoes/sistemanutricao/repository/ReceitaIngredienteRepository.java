package com.mjwsolucoes.sistemanutricao.repository;

import com.mjwsolucoes.sistemanutricao.model.ReceitaIngrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReceitaIngredienteRepository extends JpaRepository<ReceitaIngrediente, Long> {

    // Busca todos os ingredientes de uma receita
    List<ReceitaIngrediente> findByReceitaId(Long receitaId);

    // Busca por tipo de ingrediente (original ou criado pelo nutricionista)
    @Query("SELECT ri FROM ReceitaIngrediente ri WHERE ri.receita.id = :receitaId AND " +
            "(ri.ingrediente IS NOT NULL OR ri.ingredienteNutricionista IS NOT NULL)")
    List<ReceitaIngrediente> findIngredientesByReceitaId(@Param("receitaId") Long receitaId);

    // Deleta todos os ingredientes de uma receita
    @Transactional
    @Modifying
    void deleteByReceitaId(Long receitaId);

    // Verifica se ingrediente está em uso em alguma receita
    boolean existsByIngredienteId(Long ingredienteId);
    boolean existsByIngredienteNutricionistaId(Long ingredienteNutricionistaId);
}