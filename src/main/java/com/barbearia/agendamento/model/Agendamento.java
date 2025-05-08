package com.barbearia.agendamento.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_agendamento")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAgendamento;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_funcionario", nullable = false)
    private Funcionario funcionario;

    @ManyToOne
    @JoinColumn(name = "id_corte", nullable = false)
    private Servico servico;

    private LocalDate dataAgendamento;
    private LocalTime horaAgendamento;

    @Enumerated(EnumType.STRING)
    private StatusAgendamento statusAgendamento;

    private String observacaoAgendamento;
    private String formaPagamento;

    // Getters e Setters

    public enum StatusAgendamento {
        Agendado, Concluído, Cancelado
    }

    public Agendamento() {
    }

    public Agendamento(Integer idAgendamento, Cliente cliente, Funcionario funcionario, Servico servico,
            LocalDate dataAgendamento, LocalTime horaAgendamento, StatusAgendamento statusAgendamento,
            String observacaoAgendamento, String formaPagamento) {
        this.idAgendamento = idAgendamento;
        this.cliente = cliente;
        this.funcionario = funcionario;
        this.servico = servico;
        this.dataAgendamento = dataAgendamento;
        this.horaAgendamento = horaAgendamento;
        this.statusAgendamento = statusAgendamento;
        this.observacaoAgendamento = observacaoAgendamento;
        this.formaPagamento = formaPagamento;
    }

    public Integer getIdAgendamento() {
        return idAgendamento;
    }

    public void setIdAgendamento(Integer idAgendamento) {
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

    public StatusAgendamento getStatusAgendamento() {
        return statusAgendamento;
    }

    public void setStatusAgendamento(StatusAgendamento statusAgendamento) {
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

}
