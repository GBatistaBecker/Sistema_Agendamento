package com.barbearia.agendamento.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.barbearia.agendamento.model.Agendamento;
import com.barbearia.agendamento.model.Cliente;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {
    List<Agendamento> findByDataAgendamento(LocalDate dataAgendamento);

    boolean existsByDataAgendamentoAndHoraAgendamento(LocalDate dataAgendamento, LocalTime horaAgendamento);

    List<Agendamento> findByCliente(Cliente cliente);

    int deleteByDataAgendamentoBefore(LocalDate limite);

    Page<Agendamento> findByDataAgendamento(LocalDate dataAgendamento,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT a.servico.nomeCorte AS nomeCorte, COUNT(a) AS total "
            + "FROM Agendamento a "
            + "GROUP BY a.servico.nomeCorte "
            + "ORDER BY total DESC")
    List<Object[]> findServicosMaisFeitos();
}