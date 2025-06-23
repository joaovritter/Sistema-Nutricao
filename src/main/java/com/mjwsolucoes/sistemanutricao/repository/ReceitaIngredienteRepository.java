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

    // Busca todos os ingredientes de uma receita - OK
    List<ReceitaIngrediente> findByReceitaId(Long receitaId);

    // Busca por tipo de ingrediente (original ou criado pelo nutricionista)
    // Usando o campo 'isIngredienteSistema' da entidade Ingrediente
    @Query("SELECT ri FROM ReceitaIngrediente ri JOIN ri.ingrediente i WHERE ri.receita.id = :receitaId AND i.isIngredienteSistema = :isSistema")
    List<ReceitaIngrediente> findIngredientesByReceitaIdAndTipo(@Param("receitaId") Long receitaId, @Param("isSistema") boolean isSistema);

    // Se você ainda precisa de uma query genérica que apenas pegue os ingredientes associados:
    // List<ReceitaIngrediente> findIngredientesByReceitaId(@Param("receitaId") Long receitaId);
    // Mas 'findByReceitaId(Long receitaId)' já faz isso, então o método original pode ser removido
    // ou renomeado para algo mais específico se a intenção for outra.

    // Deleta todos os ingredientes de uma receita - OK
    @Transactional
    @Modifying
    void deleteByReceitaId(Long receitaId);

    // Verifica se ingrediente está em uso em alguma receita - OK
    boolean existsByIngredienteId(Long ingredienteId);

    // Esta linha provavelmente causará erro se 'ReceitaIngrediente' não tiver um campo 'ingredienteNutricionistaId'.
    // Sua entidade 'ReceitaIngrediente' tem relacionamento APENAS com 'Ingrediente'.
    // Se 'Ingrediente' é a entidade que contém a flag 'isIngredienteSistema' e o nutricionista,
    // você deve verificar o ingrediente diretamente.
    // REMOVIDA OU AJUSTAR CONFORME SEU MODELO.
    // boolean existsByIngredienteNutricionistaId(Long ingredienteNutricionistaId);
    // Se Ingrediente.java já tem `isIngredienteSistema` e o relacionamento com `User` (nutricionista),
    // então você pode verificar assim:
    // boolean existsByIngrediente_Nutricionista_Id(Long nutricionistaId); // Exemplo de uso de propriedade aninhada
}