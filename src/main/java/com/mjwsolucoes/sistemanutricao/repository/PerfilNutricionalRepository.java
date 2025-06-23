package com.mjwsolucoes.sistemanutricao.repository;

import com.mjwsolucoes.sistemanutricao.model.PerfilNutricional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importe Optional

@Repository
public interface PerfilNutricionalRepository extends JpaRepository<PerfilNutricional, Long> {

    // Busca perfil nutricional por receita - CORRIGIDO PARA RETORNAR Optional
    Optional<PerfilNutricional> findByReceitaId(Long receitaId);

    // Verifica se existe perfil para uma receita - OK
    boolean existsByReceitaId(Long receitaId);

    // Deleta perfil por receita - OK
    void deleteByReceitaId(Long receitaId);
}