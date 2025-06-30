package com.mjwsolucoes.sistemanutricao.dto;

import java.util.List;

public class RefeicaoInputDTO {
    private String nome;
    private List<Long> receitasIds;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<Long> getReceitasIds() { return receitasIds; }
    public void setReceitasIds(List<Long> receitasIds) { this.receitasIds = receitasIds; }
} 