package com.mjwsolucoes.sistemanutricao.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Refeicao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @ManyToMany
    @JoinTable(
        name = "refeicao_receita",
        joinColumns = @JoinColumn(name = "refeicao_id"),
        inverseJoinColumns = @JoinColumn(name = "receita_id")
    )
    private List<Receita> receitas;

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<Receita> getReceitas() { return receitas; }
    public void setReceitas(List<Receita> receitas) { this.receitas = receitas; }
} 