package com.barbearia.agendamento.util;

import java.time.format.DateTimeFormatter;
import com.barbearia.agendamento.model.Agendamento;

public class ListaOrdenadaAgendamento {

    private AgendamentoNo inicio;
    private AgendamentoNo fim;

    // Nó interno da fila
    private class AgendamentoNo {
        Agendamento agendamento;
        AgendamentoNo proximo;

        AgendamentoNo(Agendamento agendamento) {
            this.agendamento = agendamento;
        }
    }

    public boolean isEmpty() {
        return inicio == null;
    }

    public void inserirOrdenado(Agendamento agendamento) {
        AgendamentoNo novo = new AgendamentoNo(agendamento);

        if (isEmpty()) {
            inicio = novo;
            fim = novo;
            return;
        }

        // Insere inicio
        if (compararAgendamento(agendamento, inicio.agendamento) < 0) {
            novo.proximo = inicio;
            inicio = novo;
            return;
        }

        // Insere meio e fim
        AgendamentoNo atual = inicio;
        while (atual.proximo != null && compararAgendamento(atual.proximo.agendamento, agendamento) < 0) {
            atual = atual.proximo;
        }

        novo.proximo = atual.proximo;
        atual.proximo = novo;

        if (novo.proximo == null) {
            fim = novo;
        }
    }

    public Agendamento remover() {
        if (!isEmpty()) {
            Agendamento ag = inicio.agendamento;
            inicio = inicio.proximo;
            if (inicio == null) {
                fim = null;
            }
            return ag;
        }
        return null;
    }

    // Método View para a tela do admin
    public String viewAdmin() {
        StringBuilder sb = new StringBuilder();
        AgendamentoNo atual = inicio;
        DateTimeFormatter dataFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter horaFmt = DateTimeFormatter.ofPattern("HH:mm");

        while (atual != null) {
            Agendamento ag = atual.agendamento;
            //Inserção das informações nas divs para os agendamentos
            sb.append("<div class='agenda-item' id='agendamento-").append(ag.getIdAgendamento()).append("'>");
            sb.append("<div class='horario'>").append(ag.getHoraAgendamento().format(horaFmt)).append("</div>");
            sb.append("<div class='info'>")
                    .append("<strong>Nome:</strong> ").append(ag.getCliente().getNomeCliente()).append("<br/>")
                    .append("<strong>Telefone:</strong> ").append(ag.getCliente().getTelefoneCliente()).append("<br/>")
                    .append("<strong>Data:</strong> ").append(ag.getDataAgendamento().format(dataFmt)).append("<br/>")
                    .append("<strong>Serviço:</strong> ").append(ag.getServico().getNomeCorte())
                    .append("</div>");
            sb.append("<div class='acoes'>")
                    .append("<button class='btn btn-success' onclick='confirmar(")
                    .append(ag.getIdAgendamento()).append(")'><i class='bi bi-check'></i></button>")
                    .append(" ")

                    .append("<button class='btn btn-danger' onclick='cancelar(")
                    .append(ag.getIdAgendamento()).append(")'><i class='bi bi-x'></i></button>")
                    .append("</div>");
            sb.append("</div>");

            atual = atual.proximo;
        }

        return sb.toString();
    }

    //Método view para o cliente
    public String viewCliente() {
        StringBuilder sb = new StringBuilder();
        AgendamentoNo atual = inicio;
        DateTimeFormatter dataFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter horaFmt = DateTimeFormatter.ofPattern("HH:mm");

        while (atual != null) {
            Agendamento ag = atual.agendamento;
            //Inserção dos agendamentos no menu lateral para o cliente logado
            sb.append("<li data-id='").append(ag.getIdAgendamento()).append("'>");
            sb.append(dataFmt.format(ag.getDataAgendamento()))
                    .append(" às ")
                    .append(horaFmt.format(ag.getHoraAgendamento()))
                    .append(" - ")
                    .append(ag.getServico().getNomeCorte());
            sb.append("<button class='btn-excluir' onclick='confirmarExclusao(")
                    .append(ag.getIdAgendamento()).append(")'>🗑️</button>");
            sb.append("</li>");

            atual = atual.proximo;
        }

        return sb.toString();
    }

    // Compara data e hora
    private int compararAgendamento(Agendamento a, Agendamento b) {
        int cmpData = a.getDataAgendamento().compareTo(b.getDataAgendamento());
        if (cmpData != 0) {
            return cmpData;
        }
        return a.getHoraAgendamento().compareTo(b.getHoraAgendamento());
    }
}