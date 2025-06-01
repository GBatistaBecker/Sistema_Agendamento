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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    public String processarLogin(@RequestParam String nomeCliente,
            @RequestParam String telefoneCliente,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Remove caracteres não numéricos do telefone
        String telefoneLimpo = telefoneCliente.replaceAll("\\D", "");

        // Verifica se o telefone tem 10 ou 11 dígitos
        if (telefoneLimpo.length() < 10 || telefoneLimpo.length() > 11) {
            redirectAttributes.addFlashAttribute("erroTelefone",
                    "Telefone inserido incorretamente. Informe um número válido com DDD.");
            return "redirect:/barbearia/login";
        }

        // Verifica se já existe outro cliente com esse telefone
        Optional<Cliente> clienteComTelefone = clienteRepository.findByTelefoneCliente(telefoneCliente);

        if (clienteComTelefone.isPresent()) {
            Cliente clienteExistente = clienteComTelefone.get();
            // Se o telefone está associado a outro nome diferente do que o usuário digitou,
            // bloqueia
            if (!clienteExistente.getNomeCliente().equalsIgnoreCase(nomeCliente)) {
                redirectAttributes.addFlashAttribute("erroTelefoneExistente",
                        "Telefone já cadastrado com outro nome.");
                return "redirect:/barbearia/login";
            }
            // Se nome e telefone batem, login normal
            session.setAttribute("clienteLogado", clienteExistente);
            return "redirect:/barbearia/servicos";
        }

        // Se não existe cliente com esse telefone, cria novo
        Cliente cliente = new Cliente();
        cliente.setNomeCliente(nomeCliente);
        cliente.setTelefoneCliente(telefoneCliente);
        cliente = clienteRepository.save(cliente);

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

    @GetMapping("/barbearia/admin")
    public String mostraAdmin(Model model) {
        List<Agendamento> agendamentos = agendamentoRepository.findAll();
        model.addAttribute("agendamentos", agendamentos);
        return "admin";
    }
}