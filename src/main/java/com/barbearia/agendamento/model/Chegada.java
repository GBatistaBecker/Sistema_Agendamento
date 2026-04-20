package com.barbearia.agendamento.model;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_chegada")
public class Chegada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idChegada;

    @OneToOne
    @JoinColumn(name = "id_agendamento", nullable = false)
    private Agendamento agendamento;

    private LocalTime horaChegada;
    private LocalTime horaSaida;

    @Enumerated(EnumType.STRING)
    private StatusChegada statusChegada;

    public enum StatusChegada {
        Aguardando, Em_atendimento, Finalizado
    }

    public Chegada() {
    }

    public Chegada(Integer idChegada, Agendamento agendamento, LocalTime horaChegada, LocalTime horaSaida,
            StatusChegada statusChegada) {
        this.idChegada = idChegada;
        this.agendamento = agendamento;
        this.horaChegada = horaChegada;
        this.horaSaida = horaSaida;
        this.statusChegada = statusChegada;
    }

    public Integer getIdChegada() {
        return idChegada;
    }

    public void setIdChegada(Integer idChegada) {
        this.idChegada = idChegada;
    }

    public Agendamento getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Agendamento agendamento) {
        this.agendamento = agendamento;
    }

    public LocalTime getHoraChegada() {
        return horaChegada;
    }

    public void setHoraChegada(LocalTime horaChegada) {
        this.horaChegada = horaChegada;
    }

    public LocalTime getHoraSaida() {
        return horaSaida;
    }

    public void setHoraSaida(LocalTime horaSaida) {
        this.horaSaida = horaSaida;
    }

    public StatusChegada getStatusChegada() {
        return statusChegada;
    }

    public void setStatusChegada(StatusChegada statusChegada) {
        this.statusChegada = statusChegada;
    }

}
