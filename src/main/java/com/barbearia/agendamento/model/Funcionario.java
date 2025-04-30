package com.barbearia.agendamento.model;

import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "tbl_funcionarios")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idFuncionario;

    private String nomeFuncionario;
    private String funcaoFuncionario;
    private String emailFuncionario;
    private String telefoneFuncionario;

    @OneToMany(mappedBy = "funcionario")
    private List<Agendamento> agendamentos;

    @ManyToMany
    @JoinTable(
        name = "tbl_funcionario_servico",
        joinColumns = @JoinColumn(name = "id_funcionario"),
        inverseJoinColumns = @JoinColumn(name = "id_corte")
    )
    private List<Servico> servicos;

    public Funcionario(String nomeFuncionario, String funcaoFuncionario, String emailFuncionario,
            String telefoneFuncionario) {
        this.nomeFuncionario = nomeFuncionario;
        this.funcaoFuncionario = funcaoFuncionario;
        this.emailFuncionario = emailFuncionario;
        this.telefoneFuncionario = telefoneFuncionario;
    }

    public Funcionario() {
    }

    public int getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(int idFuncionario) {
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

}
