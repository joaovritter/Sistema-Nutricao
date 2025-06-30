package com.mjwsolucoes.sistemanutricao.dto;

import java.util.List;

public class RefeicaoDTO {
    private Long id;
    private String nome;
    private List<ReceitaResumoDTO> receitas;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<ReceitaResumoDTO> getReceitas() { return receitas; }
    public void setReceitas(List<ReceitaResumoDTO> receitas) { this.receitas = receitas; }
} 