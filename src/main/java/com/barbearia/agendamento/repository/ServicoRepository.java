package com.barbearia.agendamento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.barbearia.agendamento.model.Servico;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Integer> {
}
