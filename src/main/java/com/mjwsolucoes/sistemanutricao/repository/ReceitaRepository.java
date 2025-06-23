package com.mjwsolucoes.sistemanutricao.repository;

import com.mjwsolucoes.sistemanutricao.model.Receita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceitaRepository extends JpaRepository<Receita, Long> {

    // Busca receitas por nutricionista
    List<Receita> findByNutricionistaId(Long nutricionistaId);

    // Busca receitas por categoria
    List<Receita> findByCategoria(String categoria);

    // Busca receitas contendo parte do nome (case insensitive)
    List<Receita> findByNomeContainingIgnoreCase(String nome);

    // Busca personalizada com JOIN
    // CORREÇÃO AQUI: 'r.ingredientesReceita' em vez de 'r.ingredientesAssociados'
    @Query("SELECT r FROM Receita r JOIN r.ingredientesReceita ri WHERE ri.ingrediente.id = :ingredienteId")
    List<Receita> findByIngredienteId(@Param("ingredienteId") Long ingredienteId);

    // Verifica se existe receita com determinado nome para um nutricionista
    boolean existsByNomeAndNutricionistaId(String nome, Long nutricionistaId);
}