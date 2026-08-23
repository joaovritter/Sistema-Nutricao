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

    long countByNutricionistaId(Long nutricionistaId);

    // Busca receitas por categoria
    List<Receita> findByCategoria(String categoria);

    // Busca receitas contendo parte do nome (case insensitive)
    List<Receita> findByNomeContainingIgnoreCase(String nome);

    // Fichas em uso (não arquivadas) — é o que as telas mostram por padrão
    List<Receita> findByArquivadaFalse();

    long countByArquivadaFalse();

    long countByArquivadaTrue();

    // Busca personalizada com JOIN
    @Query("SELECT r FROM Receita r JOIN r.ingredientesReceita ri WHERE ri.ingrediente.id = :ingredienteId")
    List<Receita> findByIngredienteId(@Param("ingredienteId") Long ingredienteId);

    // Verifica se existe receita com determinado nome para um nutricionista
    boolean existsByNomeAndNutricionistaId(String nome, Long nutricionistaId);

    // Contagem de receitas agrupadas por categoria, para o resumo da dashboard
    @Query("SELECT r.categoria, COUNT(r) FROM Receita r WHERE r.arquivada = false GROUP BY r.categoria")
    List<Object[]> countByCategoria();

    /**
     * Soma dos macronutrientes de cada ficha, em gramas, a partir do peso líquido
     * de cada ingrediente (os valores da tabela são por 100 g).
     * Retorna: receitaId, proteína, carboidrato, lipídio, sódio, gordura saturada.
     */
    @Query("""
            SELECT ri.receitaId,
                   SUM(ri.pesoLiquido * i.proteina / 100),
                   SUM(ri.pesoLiquido * i.carboidrato / 100),
                   SUM(ri.pesoLiquido * i.lipidio / 100),
                   SUM(ri.pesoLiquido * i.sodio / 100),
                   SUM(ri.pesoLiquido * i.gorduraSaturada / 100)
            FROM ReceitaIngrediente ri
            JOIN ri.ingrediente i
            GROUP BY ri.receitaId
            """)
    List<Object[]> somarMacronutrientesPorReceita();
}
