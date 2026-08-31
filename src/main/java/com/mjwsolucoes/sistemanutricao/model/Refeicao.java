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

    /** Refeicao arquivada some das listas e buscas, mas continua no banco. */
    @Column(nullable = false)
    private boolean arquivada;

    /**
     * Quem criou a refeicao. Nulo para refeicoes antigas, criadas antes desse
     * controle existir - nesse caso a edicao fica liberada para qualquer AUTOR,
     * como acontecia antes.
     */
    @ManyToOne
    @JoinColumn(name = "criado_por_id")
    private User criadoPor;

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<Receita> getReceitas() { return receitas; }
    public void setReceitas(List<Receita> receitas) { this.receitas = receitas; }
    public boolean isArquivada() { return arquivada; }
    public void setArquivada(boolean arquivada) { this.arquivada = arquivada; }
    public User getCriadoPor() { return criadoPor; }
    public void setCriadoPor(User criadoPor) { this.criadoPor = criadoPor; }
}