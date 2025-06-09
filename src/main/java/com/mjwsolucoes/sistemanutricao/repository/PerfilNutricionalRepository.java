package com.mjwsolucoes.sistemanutricao.repository;

import com.mjwsolucoes.sistemanutricao.model.PerfilNutricional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilNutricionalRepository extends JpaRepository<PerfilNutricional, Long> {

    // Busca perfil nutricional por receita
    PerfilNutricional findByReceitaId(Long receitaId);

    // Verifica se existe perfil para uma receita
    boolean existsByReceitaId(Long receitaId);

    // Deleta perfil por receita
    void deleteByReceitaId(Long receitaId);
}