package com.barbearia.agendamento.model;

import java.time.LocalTime;

import jakarta.persistence.Entity;
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
    private int idChegada;

    @OneToOne
    @JoinColumn(name = "id_agendamento")
    private Agendamento agendamento;

    private LocalTime horaChegada;
    private LocalTime horaSaida;
    private String statusChegada;

    public Chegada(Agendamento agendamento, LocalTime horaChegada, LocalTime horaSaida, String statusChegada) {
        this.agendamento = agendamento;
        this.horaChegada = horaChegada;
        this.horaSaida = horaSaida;
        this.statusChegada = statusChegada;
    }

    public Chegada() {
    }

    public int getIdChegada() {
        return idChegada;
    }

    public void setIdChegada(int idChegada) {
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

    public String getStatusChegada() {
        return statusChegada;
    }

    public void setStatusChegada(String statusChegada) {
        this.statusChegada = statusChegada;
    }

}
