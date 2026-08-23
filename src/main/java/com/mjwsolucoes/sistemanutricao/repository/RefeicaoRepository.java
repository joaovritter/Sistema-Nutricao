package com.mjwsolucoes.sistemanutricao.repository;

import com.mjwsolucoes.sistemanutricao.model.Refeicao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefeicaoRepository extends JpaRepository<Refeicao, Long> {

    /** Refeições em uso — é o que as telas listam por padrão. */
    List<Refeicao> findByArquivadaFalse();

    long countByArquivadaFalse();

    long countByArquivadaTrue();
}
