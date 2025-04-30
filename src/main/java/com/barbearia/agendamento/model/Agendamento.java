package com.barbearia.agendamento.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_agendamento")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAgendamento;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_funcionario")
    private Funcionario funcionario;

    @ManyToOne
    @JoinColumn(name = "id_corte")
    private Servico servico;

    private LocalDate dataAgendamento;
    private LocalTime horaAgendamento;
    private String statusAgendamento;
    private String observacaoAgendamento;
    private String formaPagamento;

    @OneToOne(mappedBy = "agendamento", cascade = CascadeType.ALL)
    private TempoAtendimento tempoAtendimento;

    @OneToOne(mappedBy = "agendamento", cascade = CascadeType.ALL)
    private Chegada chegada;

    public Agendamento(Cliente cliente, Funcionario funcionario, Servico servico, LocalDate dataAgendamento,
            LocalTime horaAgendamento, String statusAgendamento, String observacaoAgendamento, String formaPagamento,
            TempoAtendimento tempoAtendimento, Chegada chegada) {
        this.cliente = cliente;
        this.funcionario = funcionario;
        this.servico = servico;
        this.dataAgendamento = dataAgendamento;
        this.horaAgendamento = horaAgendamento;
        this.statusAgendamento = statusAgendamento;
        this.observacaoAgendamento = observacaoAgendamento;
        this.formaPagamento = formaPagamento;
        this.tempoAtendimento = tempoAtendimento;
        this.chegada = chegada;
    }

    public Agendamento() {
    }

    public int getIdAgendamento() {
        return idAgendamento;
    }

    public void setIdAgendamento(int idAgendamento) {
        this.idAgendamento = idAgendamento;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDate dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public LocalTime getHoraAgendamento() {
        return horaAgendamento;
    }

    public void setHoraAgendamento(LocalTime horaAgendamento) {
        this.horaAgendamento = horaAgendamento;
    }

    public String getStatusAgendamento() {
        return statusAgendamento;
    }

    public void setStatusAgendamento(String statusAgendamento) {
        this.statusAgendamento = statusAgendamento;
    }

    public String getObservacaoAgendamento() {
        return observacaoAgendamento;
    }

    public void setObservacaoAgendamento(String observacaoAgendamento) {
        this.observacaoAgendamento = observacaoAgendamento;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public TempoAtendimento getTempoAtendimento() {
        return tempoAtendimento;
    }

    public void setTempoAtendimento(TempoAtendimento tempoAtendimento) {
        this.tempoAtendimento = tempoAtendimento;
    }

    public Chegada getChegada() {
        return chegada;
    }

    public void setChegada(Chegada chegada) {
        this.chegada = chegada;
    }

}
