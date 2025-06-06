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
@Table(name = "tbl_tempo_atendimento")
public class TempoAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAtendimento;

    @OneToOne
    @JoinColumn(name = "id_agendamento", nullable = false)
    private Agendamento agendamento;

    private LocalTime horaInicio;
    private LocalTime horaTermino;
    private Integer duracaoAtendimento;
    private String observacaoAtendimento;

    public TempoAtendimento() {
    }

    public TempoAtendimento(Integer idAtendimento, Agendamento agendamento, LocalTime horaInicio, LocalTime horaTermino,
            Integer duracaoAtendimento, String observacaoAtendimento) {
        this.idAtendimento = idAtendimento;
        this.agendamento = agendamento;
        this.horaInicio = horaInicio;
        this.horaTermino = horaTermino;
        this.duracaoAtendimento = duracaoAtendimento;
        this.observacaoAtendimento = observacaoAtendimento;
    }

    public Integer getIdAtendimento() {
        return idAtendimento;
    }

    public void setIdAtendimento(Integer idAtendimento) {
        this.idAtendimento = idAtendimento;
    }

    public Agendamento getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Agendamento agendamento) {
        this.agendamento = agendamento;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraTermino() {
        return horaTermino;
    }

    public void setHoraTermino(LocalTime horaTermino) {
        this.horaTermino = horaTermino;
    }

    public Integer getDuracaoAtendimento() {
        return duracaoAtendimento;
    }

    public void setDuracaoAtendimento(Integer duracaoAtendimento) {
        this.duracaoAtendimento = duracaoAtendimento;
    }

    public String getObservacaoAtendimento() {
        return observacaoAtendimento;
    }

    public void setObservacaoAtendimento(String observacaoAtendimento) {
        this.observacaoAtendimento = observacaoAtendimento;
    }

}
