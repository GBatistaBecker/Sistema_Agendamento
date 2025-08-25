package com.barbearia.agendamento.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.barbearia.agendamento.model.Agendamento;
import com.barbearia.agendamento.model.Cliente;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {
    List<Agendamento> findByDataAgendamento(LocalDate dataAgendamento);

    boolean existsByDataAgendamentoAndHoraAgendamento(LocalDate dataAgendamento, LocalTime horaAgendamento);

    List<Agendamento> findByCliente(Cliente cliente);

}