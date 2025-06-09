package com.mjwsolucoes.sistemanutricao.repository;

import com.mjwsolucoes.sistemanutricao.model.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

    // Busca ingrediente por nome (exato)
    Ingrediente findByNome(String nome);

    // Busca ingredientes por parte do nome (case insensitive)
    List<Ingrediente> findByNomeContainingIgnoreCase(String nome);

    // Busca ingredientes com maior teor de proteína
    @Query("SELECT i FROM Ingrediente i ORDER BY i.proteina DESC")
    List<Ingrediente> findAllOrderByProteinaDesc();

    // Busca ingredientes com menor teor de gordura saturada
    @Query("SELECT i FROM Ingrediente i ORDER BY i.gorduraSaturada ASC")
    List<Ingrediente> findAllOrderByGorduraSaturadaAsc();
}