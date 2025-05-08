package com.barbearia.agendamento.model;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_servicos")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCorte;

    @Column(nullable = false, length = 50)
    private String nomeCorte;

    @Column(nullable = false)
    private BigDecimal valorCorte;

    @Column(nullable = false, length = 20)
    private String duracaoCorte;

    public Servico() {
    }

    public Servico(Integer idCorte, String nomeCorte, BigDecimal valorCorte, String duracaoCorte) {
        this.idCorte = idCorte;
        this.nomeCorte = nomeCorte;
        this.valorCorte = valorCorte;
        this.duracaoCorte = duracaoCorte;
    }

    public Integer getIdCorte() {
        return idCorte;
    }

    public void setIdCorte(Integer idCorte) {
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
