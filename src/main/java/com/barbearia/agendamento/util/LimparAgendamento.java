package com.barbearia.agendamento.util;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.barbearia.agendamento.repository.AgendamentoRepository;

import jakarta.transaction.Transactional;

@Component
public class LimparAgendamento {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    // Executa todo dia 1 à meia noite
    // para deletar o histórica dos agendamentos
    // mais antigos que 30 dias
    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void limparAgendamentosAntigos() {
        LocalDate limite = LocalDate.now().minusDays(30);
        int removidos = agendamentoRepository.deleteByDataAgendamentoBefore(limite);
    }

}
