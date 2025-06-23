package com.mjwsolucoes.sistemanutricao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable; // Necessário para @IdClass

// Definindo a chave composta
class Receita_Ingrediente_Id implements Serializable {
    private Long receitaId;
    private Long ingredienteId;

    // Necessário para @IdClass
    public Receita_Ingrediente_Id() {}

    public Receita_Ingrediente_Id(Long receitaId, Long ingredienteId) {
        this.receitaId = receitaId;
        this.ingredienteId = ingredienteId;
    }

    // Métodos equals e hashCode são essenciais para chaves compostas
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Receita_Ingrediente_Id that = (Receita_Ingrediente_Id) o;
        return receitaId.equals(that.receitaId) &&
                ingredienteId.equals(that.ingredienteId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(receitaId, ingredienteId);
    }
}