package com.barbearia.agendamento.model;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;

@Embeddable
public class FuncionarioServicoId implements Serializable {

    private int idFuncionario;
    private int idCorte;

    public FuncionarioServicoId() {}

    public FuncionarioServicoId(int idFuncionario, int idCorte) {
        this.idFuncionario = idFuncionario;
        this.idCorte = idCorte;
    }

    public int getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(int idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public int getIdCorte() {
        return idCorte;
    }

    public void setIdCorte(int idCorte) {
        this.idCorte = idCorte;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FuncionarioServicoId)) return false;
        FuncionarioServicoId that = (FuncionarioServicoId) o;
        return idFuncionario == that.idFuncionario &&
               idCorte == that.idCorte;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFuncionario, idCorte);
    }
}

