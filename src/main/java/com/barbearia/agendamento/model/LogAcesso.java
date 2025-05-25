package com.barbearia.agendamento.model;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.persistence.Column;
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
@Table(name = "tbl_log_acessos")
public class LogAcesso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLog;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    private LocalDate dataAcesso;
    private LocalTime horaAcesso;

    @Enumerated(EnumType.STRING)
    private TipoAcesso tipoAcesso;

    @Column(length = 45)
    private String ipAcesso;

    public enum TipoAcesso {
        Login, Logout, Falha
    }

    public LogAcesso() {
    }

    public LogAcesso(Integer idLog, Usuario usuario, LocalDate dataAcesso, LocalTime horaAcesso, TipoAcesso tipoAcesso,
            String ipAcesso) {
        this.idLog = idLog;
        this.usuario = usuario;
        this.dataAcesso = dataAcesso;
        this.horaAcesso = horaAcesso;
        this.tipoAcesso = tipoAcesso;
        this.ipAcesso = ipAcesso;
    }

    public Integer getIdLog() {
        return idLog;
    }

    public void setIdLog(Integer idLog) {
        this.idLog = idLog;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getDataAcesso() {
        return dataAcesso;
    }

    public void setDataAcesso(LocalDate dataAcesso) {
        this.dataAcesso = dataAcesso;
    }

    public LocalTime getHoraAcesso() {
        return horaAcesso;
    }

    public void setHoraAcesso(LocalTime horaAcesso) {
        this.horaAcesso = horaAcesso;
    }

    public TipoAcesso getTipoAcesso() {
        return tipoAcesso;
    }

    public void setTipoAcesso(TipoAcesso tipoAcesso) {
        this.tipoAcesso = tipoAcesso;
    }

    public String getIpAcesso() {
        return ipAcesso;
    }

    public void setIpAcesso(String ipAcesso) {
        this.ipAcesso = ipAcesso;
    }

}
