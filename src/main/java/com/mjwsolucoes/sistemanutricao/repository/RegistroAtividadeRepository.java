package com.mjwsolucoes.sistemanutricao.repository;

import com.mjwsolucoes.sistemanutricao.model.RegistroAtividade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroAtividadeRepository extends JpaRepository<RegistroAtividade, Long> {

    /** Histórico completo, do mais recente para o mais antigo. */
    List<RegistroAtividade> findAllByOrderByMomentoDescIdDesc();

    /** Nomes que já apareceram no histórico, para o filtro por usuário. */
    @Query("SELECT DISTINCT r.usuario FROM RegistroAtividade r ORDER BY r.usuario")
    List<String> listarUsuarios();
}
