package com.barbearia.agendamento.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.barbearia.agendamento.model.Agendamento;
import com.barbearia.agendamento.model.Cliente;
import com.barbearia.agendamento.model.Funcionario;
import com.barbearia.agendamento.model.Servico;
import com.barbearia.agendamento.repository.*;
import com.barbearia.agendamento.util.ListaOrdenadaAgendamento;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/barbearia")
public class BarbeariaController {

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    FuncionarioRepository funcionarioRepository;

    @Autowired
    ServicoRepository servicoRepository;

    @Autowired
    AgendamentoRepository agendamentoRepository;

    // Páginas principais
    @GetMapping("/cadastro")
    public String mostrarCadastro() {
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrarCliente(@RequestParam String nomeCliente,
            @RequestParam String telefoneCliente,
            @RequestParam String emailCliente,
            RedirectAttributes redirectAttributes) {

        String telefoneLimpo = telefoneCliente.replaceAll("\\D", "");

        // Validação básica
        if (telefoneLimpo.length() < 10 || telefoneLimpo.length() > 11) {
            redirectAttributes.addFlashAttribute("erroTelefone", "Telefone inválido. Informe um número com DDD.");
            return "redirect:/barbearia/cadastro";
        }

        if (nomeCliente.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("erroNome", "Nome não pode ser vazio.");
            return "redirect:/barbearia/cadastro";
        }

        if (!emailCliente.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            redirectAttributes.addFlashAttribute("erroEmail", "E-mail inválido.");
            return "redirect:/barbearia/cadastro";
        }

        // Verifica se já existe cliente com mesmo telefone ou email
        if (clienteRepository.findByTelefoneCliente(telefoneCliente).isPresent()) {
            redirectAttributes.addFlashAttribute("erroTelefoneExistente", "Telefone já cadastrado!");
            return "redirect:/barbearia/cadastro";
        }

        if (clienteRepository.findByEmailCliente(emailCliente).isPresent()) {
            redirectAttributes.addFlashAttribute("erroEmailExistente", "E-mail já cadastrado!");
            return "redirect:/barbearia/cadastro";
        }

        // Cria e salva cliente
        Cliente novoCliente = new Cliente();
        novoCliente.setNomeCliente(nomeCliente);
        novoCliente.setTelefoneCliente(telefoneCliente);
        novoCliente.setEmailCliente(emailCliente);

        clienteRepository.save(novoCliente);

        redirectAttributes.addFlashAttribute("sucessoCadastro", "Cadastro realizado com sucesso! Faça login.");
        return "redirect:/barbearia/login";
    }

    @GetMapping("/index")
    public String mostrarIndex() {
        return "index";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String verificarCliente(@RequestParam String nomeCliente,
            @RequestParam String telefoneCliente,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        String telefoneLimpo = telefoneCliente.replaceAll("\\D", "");

        // Valida formato do telefone
        if (telefoneLimpo.length() < 10 || telefoneLimpo.length() > 11) {
            redirectAttributes.addFlashAttribute("erroTelefone", "Telefone inválido. Informe um número com DDD.");
            return "redirect:/barbearia/login";
        }

        // Busca cliente por telefone
        Optional<Cliente> clienteComTelefone = clienteRepository.findByTelefoneCliente(telefoneCliente);

        if (clienteComTelefone.isPresent()) {
            Cliente clienteExistente = clienteComTelefone.get();
            // Verifica se o nome bate
            if (!clienteExistente.getNomeCliente().equalsIgnoreCase(nomeCliente)) {
                redirectAttributes.addFlashAttribute("erroTelefoneExistente", "Telefone já cadastrado com outro nome.");
                return "redirect:/barbearia/login";
            }

            // Apenas armazenar para reconhecer sessão
            session.setAttribute("clienteLogado", clienteExistente);
            return "redirect:/barbearia/servicos";
        }

        // Se não existir, apenas erro (fica no login)
        redirectAttributes.addFlashAttribute("erroNaoEncontrado", "Cliente não encontrado. Cadastre-se para acessar.");
        return "redirect:/barbearia/login";
    }

    @GetMapping("/servicos")
    public String listarServicos(Model model) {
        model.addAttribute("servicos", servicoRepository.findAll());
        return "servicos";
    }

    @GetMapping("/agendamentos")
    public String exibirAgendamentos(@RequestParam(required = false) Integer servicoId, Model model) {
        model.addAttribute("servicos", servicoRepository.findAll());
        model.addAttribute("servicoSelecionadoId", servicoId);
        return "agendamentos";
    }

    @GetMapping("/horarios")
    public String mostrarHorarios() {
        return "horarios";
    }

    @PostMapping("/agendar")
    @ResponseBody
    public ResponseEntity<String> realizarAgendamento(@RequestParam Integer idServico,
            @RequestParam String dataAgendamento,
            @RequestParam String horaAgendamento,
            HttpSession session) {

        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");
        if (cliente == null)
            return ResponseEntity.badRequest().body("Cliente não logado.");

        LocalDate data = LocalDate.parse(dataAgendamento);
        LocalTime hora = LocalTime.parse(horaAgendamento);

        if (agendamentoRepository.existsByDataAgendamentoAndHoraAgendamento(data, hora)) {
            return ResponseEntity.badRequest().body("Horário já ocupado.");
        }

        Servico servico = servicoRepository.findById(idServico)
                .orElseThrow(() -> new IllegalArgumentException("Serviço inválido."));

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setServico(servico);
        agendamento.setDataAgendamento(data);
        agendamento.setHoraAgendamento(hora);
        agendamentoRepository.save(agendamento);

        return ResponseEntity.ok("Agendamento realizado com sucesso.");
    }

    @GetMapping("/agendamentos-do-usuario")
    @ResponseBody
    public ResponseEntity<String> listarAgendamentosDoUsuario(
            HttpSession session,
            @RequestParam(value = "idServico", required = false) Long idServico) {

        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ListaOrdenadaAgendamento fila = new ListaOrdenadaAgendamento();
        List<Agendamento> agendamentos = agendamentoRepository.findByCliente(cliente);

        for (Agendamento ag : agendamentos) {
            if (idServico == null || ag.getServico().getIdCorte().equals(idServico)) {
                fila.inserirOrdenado(ag);
            }
        }

        return ResponseEntity.ok(fila.viewCliente()); // método novo
    }

    @PostMapping("/excluir-agendamento")
    @ResponseBody
    public ResponseEntity<String> excluirAgendamento(@RequestParam Integer idAgendamento, HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");
        if (cliente == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cliente não logado.");

        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(idAgendamento);
        if (agendamentoOpt.isEmpty())
            return ResponseEntity.badRequest().body("Agendamento não encontrado.");

        Agendamento agendamento = agendamentoOpt.get();
        if (!agendamento.getCliente().getIdCliente().equals(cliente.getIdCliente())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Não autorizado.");
        }

        agendamentoRepository.delete(agendamento);
        return ResponseEntity.ok("Agendamento excluído com sucesso.");
    }

    @GetMapping("/horarios-ocupados")
    @ResponseBody
    public List<String> obterHorariosOcupados(@RequestParam String data,
            @RequestParam Integer idServico) {
        LocalDate dataAgendamento = LocalDate.parse(data);
        List<Agendamento> agendamentos = agendamentoRepository.findByDataAgendamento(dataAgendamento);
        return agendamentos.stream().map(ag -> ag.getHoraAgendamento().toString()).toList();
    }

    // === ROTAS DE FUNCIONÁRIO (Admin) ===

    @GetMapping("/loginadm")
    public String mostrarTelaLoginAdmin() {
        return "loginAdmin"; // Nome correto do arquivo HTML
    }

    @PostMapping("/loginadm")
    public String processarLoginFuncionario(@RequestParam String nomeFuncionario,
            @RequestParam String telefoneFuncionario,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String telefoneLimpo = telefoneFuncionario.replaceAll("\\D", "");
        if (telefoneLimpo.length() < 10 || telefoneLimpo.length() > 11) {
            redirectAttributes.addFlashAttribute("erroTelefone", "Telefone inválido.");
            return "redirect:/barbearia/loginadm";
        }

        Optional<Funcionario> funcionarioOpt = funcionarioRepository.findByTelefoneFuncionario(telefoneFuncionario);
        if (funcionarioOpt.isPresent()) {
            Funcionario funcionario = funcionarioOpt.get();
            if (!funcionario.getNomeFuncionario().equalsIgnoreCase(nomeFuncionario)) {
                redirectAttributes.addFlashAttribute("erroLogin", "Nome não confere com o telefone.");
                return "redirect:/barbearia/loginadm";
            }

            session.setAttribute("funcionarioLogado", funcionario);
            return "redirect:/barbearia/admin";
        }

        redirectAttributes.addFlashAttribute("erroLogin", "Funcionário não encontrado.");
        return "redirect:/barbearia/loginadm";
    }

    @GetMapping("/admin")
    public String exibirAgendamentosAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("horaAgendamento").ascending());
        LocalDate hoje = LocalDate.now();

        Page<Agendamento> agendamentosPage = agendamentoRepository.findByDataAgendamento(hoje, pageable);

        ListaOrdenadaAgendamento fila = new ListaOrdenadaAgendamento();
        for (Agendamento a : agendamentosPage.getContent()) {
            fila.inserirOrdenado(a);
        }

        // Verifica se há agendamentos
        boolean semAgendamentos = agendamentosPage.isEmpty();

        model.addAttribute("htmlAgendamentos", fila.viewAdmin());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", agendamentosPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("semAgendamentos", semAgendamentos);
        model.addAttribute("dataHoje", hoje);

        return "admin";
    }

    @PostMapping("/admin/{id}/confirmar")
    public ResponseEntity<String> confirmarAgendamento(@PathVariable Integer id) {
        Optional<Agendamento> opt = agendamentoRepository.findById(id);
        if (opt.isPresent()) {
            Agendamento agendamento = opt.get();
            agendamento.setStatusAgendamento(Agendamento.StatusAgendamento.Concluído);
            agendamentoRepository.save(agendamento);
            return ResponseEntity.ok("Agendamento confirmado com sucesso");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Agendamento não encontrado");
    }

    @PostMapping("/admin/{id}/cancelar")
    public ResponseEntity<String> cancelarAgendamento(@PathVariable Integer id) {
        Optional<Agendamento> opt = agendamentoRepository.findById(id);
        if (opt.isPresent()) {
            Agendamento agendamento = opt.get();
            agendamento.setStatusAgendamento(Agendamento.StatusAgendamento.Cancelado);
            agendamentoRepository.save(agendamento);
            return ResponseEntity.ok("Agendamento cancelado com sucesso");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Agendamento não encontrado");
    }

    @GetMapping("/relatorio")
    public String relatorio(
            @RequestParam(required = false) String ordenar,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        if (page == null)
            page = 0;

        Pageable pageable = PageRequest.of(page, size);
        Page<Agendamento> pagina;

        // ✅ Caso o usuário queira ordenar pelos cortes mais feitos
        if ("corte".equalsIgnoreCase(ordenar)) {
            // busca o ranking dos serviços mais populares
            List<Object[]> ranking = agendamentoRepository.findServicosMaisFeitos();
            List<String> ordemServicos = ranking.stream()
                    .map(obj -> (String) obj[0])
                    .toList();

            // busca todos os agendamentos
            List<Agendamento> todos = agendamentoRepository.findAll();

            // aplica os filtros antes de ordenar
            if (status != null && !status.isEmpty()) {
                todos = todos.stream()
                        .filter(a -> a.getStatusAgendamento().name().equalsIgnoreCase(status))
                        .toList();
            }

            if (nome != null && !nome.isEmpty()) {
                todos = todos.stream()
                        .filter(a -> a.getCliente().getNomeCliente().toLowerCase().contains(nome.toLowerCase()))
                        .toList();
            }

            // ordena conforme a frequência do corte
            List<Agendamento> ordenados = todos.stream()
                    .sorted((a, b) -> {
                        int idxA = ordemServicos.indexOf(a.getServico().getNomeCorte());
                        int idxB = ordemServicos.indexOf(b.getServico().getNomeCorte());
                        // se algum serviço não estiver no ranking (sem ocorrências)
                        if (idxA == -1)
                            idxA = ordemServicos.size();
                        if (idxB == -1)
                            idxB = ordemServicos.size();
                        // mantém a ordem por data dentro do mesmo corte
                        int cmp = Integer.compare(idxA, idxB);
                        if (cmp == 0) {
                            cmp = a.getDataAgendamento().compareTo(b.getDataAgendamento());
                            if (cmp == 0) {
                                cmp = a.getHoraAgendamento().compareTo(b.getHoraAgendamento());
                            }
                        }
                        return cmp;
                    })
                    .toList();

            // pagina manualmente (após ordenar)
            int start = Math.min(page * size, ordenados.size());
            int end = Math.min(start + size, ordenados.size());
            List<Agendamento> pageContent = ordenados.subList(start, end);

            pagina = new PageImpl<>(pageContent, pageable, ordenados.size());
        } else {
            // ✅ Ordenação padrão por data e hora
            Sort sort = Sort.by("dataAgendamento").ascending().and(Sort.by("horaAgendamento"));
            pagina = agendamentoRepository.findAll(PageRequest.of(page, size, sort));

            if (status != null && !status.isEmpty()) {
                pagina = new PageImpl<>(
                        pagina.getContent().stream()
                                .filter(a -> a.getStatusAgendamento().name().equalsIgnoreCase(status))
                                .toList(),
                        pageable,
                        pagina.getTotalElements());
            }

            if (nome != null && !nome.isEmpty()) {
                pagina = new PageImpl<>(
                        pagina.getContent().stream()
                                .filter(a -> a.getCliente().getNomeCliente().toLowerCase().contains(nome.toLowerCase()))
                                .toList(),
                        pageable,
                        pagina.getTotalElements());
            }
        }

        // envia os dados pra view
        model.addAttribute("agendamentos", pagina.getContent());
        model.addAttribute("paginaAtual", page);
        model.addAttribute("totalPaginas", pagina.getTotalPages());
        model.addAttribute("totalElementos", pagina.getTotalElements());
        model.addAttribute("ordenar", ordenar);
        model.addAttribute("status", status);
        model.addAttribute("nome", nome);

        return "relatorio";
    }

}