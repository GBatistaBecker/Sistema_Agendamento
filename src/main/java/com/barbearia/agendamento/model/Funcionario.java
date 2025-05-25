package com.barbearia.agendamento.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import java.util.List;

@Entity
@Table(name = "tbl_funcionarios")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFuncionario;

    @Column(nullable = false, length = 50)
    private String nomeFuncionario;

    @Column(nullable = false, length = 30)
    private String funcaoFuncionario;

    @Column(nullable = false, length = 80)
    private String emailFuncionario;

    @Column(nullable = false, length = 15)
    private String telefoneFuncionario;

    @ManyToMany
    @JoinTable(name = "tbl_funcionario_servico", joinColumns = @JoinColumn(name = "id_funcionario"), inverseJoinColumns = @JoinColumn(name = "id_corte"))
    private List<Servico> servicos;

    public Funcionario() {
    }

    public Funcionario(Integer idFuncionario, String nomeFuncionario, String funcaoFuncionario, String emailFuncionario,
            String telefoneFuncionario, List<Servico> servicos) {
        this.idFuncionario = idFuncionario;
        this.nomeFuncionario = nomeFuncionario;
        this.funcaoFuncionario = funcaoFuncionario;
        this.emailFuncionario = emailFuncionario;
        this.telefoneFuncionario = telefoneFuncionario;
        this.servicos = servicos;
    }

    public Integer getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Integer idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getNomeFuncionario() {
        return nomeFuncionario;
    }

    public void setNomeFuncionario(String nomeFuncionario) {
        this.nomeFuncionario = nomeFuncionario;
    }

    public String getFuncaoFuncionario() {
        return funcaoFuncionario;
    }

    public void setFuncaoFuncionario(String funcaoFuncionario) {
        this.funcaoFuncionario = funcaoFuncionario;
    }

    public String getEmailFuncionario() {
        return emailFuncionario;
    }

    public void setEmailFuncionario(String emailFuncionario) {
        this.emailFuncionario = emailFuncionario;
    }

    public String getTelefoneFuncionario() {
        return telefoneFuncionario;
    }

    public void setTelefoneFuncionario(String telefoneFuncionario) {
        this.telefoneFuncionario = telefoneFuncionario;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public void setServicos(List<Servico> servicos) {
        this.servicos = servicos;
    }

}
