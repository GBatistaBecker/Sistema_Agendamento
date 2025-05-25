package com.barbearia.agendamento.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import com.barbearia.agendamento.model.Agendamento;
import com.barbearia.agendamento.model.Cliente;
import com.barbearia.agendamento.model.Servico;
import com.barbearia.agendamento.repository.AgendamentoRepository;
import com.barbearia.agendamento.repository.ClienteRepository;
import com.barbearia.agendamento.repository.ServicoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class BarbeariaController {

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ServicoRepository servicoRepository;

    @Autowired
    AgendamentoRepository agendamentoRepository;

    @GetMapping("/barbearia/index")
    public String mostrarIndex() {
        return "index";
    }

    @GetMapping("/barbearia/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/barbearia/login")
    public String processarLogin(@RequestParam String nomeCliente, @RequestParam String telefoneCliente,
            HttpSession session) {
        Optional<Cliente> usuarioOpt = clienteRepository.findByNomeClienteAndTelefoneCliente(nomeCliente,
                telefoneCliente);

        Cliente cliente;
        if (usuarioOpt.isPresent()) {
            cliente = usuarioOpt.get();
        } else {
            cliente = new Cliente();
            cliente.setNomeCliente(nomeCliente);
            cliente.setTelefoneCliente(telefoneCliente);
            cliente = clienteRepository.save(cliente);
        }

        // Armazena o cliente logado na sessão
        session.setAttribute("clienteLogado", cliente);

        return "redirect:/barbearia/servicos";
    }

    @GetMapping("/barbearia/servicos")
    public String listarServicos(Model model) {
        List<Servico> servicos = servicoRepository.findAll();
        model.addAttribute("servicos", servicos);
        return "servicos"; // servicos.html
    }

    @GetMapping("/barbearia/agendamentos")
    public String exibirAgendamento(@RequestParam(required = false) Integer servicoId, Model model) {
        List<Servico> servicos = servicoRepository.findAll();
        model.addAttribute("servicos", servicos);
        model.addAttribute("servicoSelecionadoId", servicoId);
        return "agendamentos"; // agendamento.html
    }

    @GetMapping("/barbearia/horarios")
    public String mostrarHorarios(Model model) {
        // aqui você pode carregar informações se precisar
        return "horarios";
    }

    @PostMapping("/barbearia/agendar")
    @ResponseBody
    public ResponseEntity<String> realizarAgendamento(
            @RequestParam Integer idServico,
            @RequestParam String dataAgendamento,
            @RequestParam String horaAgendamento,
            HttpSession session) {

        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");

        if (cliente == null) {
            return ResponseEntity.badRequest().body("Cliente não está logado.");
        }

        LocalDate dataConvertida = LocalDate.parse(dataAgendamento);
        LocalTime horaConvertida = LocalTime.parse(horaAgendamento);

        if (agendamentoRepository.existsByDataAgendamentoAndHoraAgendamento(dataConvertida, horaConvertida)) {
            return ResponseEntity.badRequest().body("Horário já ocupado.");
        }

        Servico servico = servicoRepository.findById(idServico)
                .orElseThrow(() -> new IllegalArgumentException("Serviço inválido."));

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setServico(servico);
        agendamento.setDataAgendamento(dataConvertida);
        agendamento.setHoraAgendamento(horaConvertida);
        agendamentoRepository.save(agendamento);

        return ResponseEntity.ok("Agendamento realizado com sucesso.");
    }

    @GetMapping("/barbearia/agendamentos-do-usuario")
    @ResponseBody
    public ResponseEntity<List<Agendamento>> listarAgendamentosDoUsuario(HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");

        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Agendamento> agendamentos = agendamentoRepository.findByCliente(cliente);
        return ResponseEntity.ok(agendamentos);
    }

    @PostMapping("/barbearia/excluir-agendamento")
    @ResponseBody
    public ResponseEntity<String> excluirAgendamento(@RequestParam Integer idAgendamento, HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");

        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cliente não está logado.");
        }

        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(idAgendamento);
        if (agendamentoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Agendamento não encontrado.");
        }

        Agendamento agendamento = agendamentoOpt.get();

        // Garante que o agendamento pertence ao cliente logado
        if (!agendamento.getCliente().getIdCliente().equals(cliente.getIdCliente())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não pode excluir este agendamento.");
        }

        agendamentoRepository.delete(agendamento);
        return ResponseEntity.ok("Agendamento excluído com sucesso.");
    }
}