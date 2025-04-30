package com.barbearia.agendamento.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_servicos")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCorte;

    private String nomeCorte;
    private BigDecimal valorCorte;
    private String duracaoCorte;

    @ManyToMany(mappedBy = "servicos")
    private List<Funcionario> funcionarios;

    @OneToMany(mappedBy = "servico")
    private List<Agendamento> agendamentos;

    public Servico(String nomeCorte, BigDecimal valorCorte, String duracaoCorte) {
        this.nomeCorte = nomeCorte;
        this.valorCorte = valorCorte;
        this.duracaoCorte = duracaoCorte;
    }

    public Servico() {
    }

    public int getIdCorte() {
        return idCorte;
    }

    public void setIdCorte(int idCorte) {
        this.idCorte = idCorte;
    }

    public String getNomeCorte() {
        return nomeCorte;
    }

    public void setNomeCorte(String nomeCorte) {
        this.nomeCorte = nomeCorte;
    }

    public BigDecimal getValorCorte() {
        return valorCorte;
    }

    public void setValorCorte(BigDecimal valorCorte) {
        this.valorCorte = valorCorte;
    }

    public String getDuracaoCorte() {
        return duracaoCorte;
    }

    public void setDuracaoCorte(String duracaoCorte) {
        this.duracaoCorte = duracaoCorte;
    }

}
