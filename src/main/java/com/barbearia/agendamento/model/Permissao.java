package com.barbearia.agendamento.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_permissoes")
public class Permissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPermissao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPermissao tipoPermissao;

    public enum TipoPermissao {
        Administrador, Cliente
    }

    public Permissao() {
    }

    public Permissao(Integer idPermissao, TipoPermissao tipoPermissao) {
        this.idPermissao = idPermissao;
        this.tipoPermissao = tipoPermissao;
    }

    public Integer getIdPermissao() {
        return idPermissao;
    }

    public void setIdPermissao(Integer idPermissao) {
        this.idPermissao = idPermissao;
    }

    public TipoPermissao getTipoPermissao() {
        return tipoPermissao;
    }

    public void setTipoPermissao(TipoPermissao tipoPermissao) {
        this.tipoPermissao = tipoPermissao;
    }

}
