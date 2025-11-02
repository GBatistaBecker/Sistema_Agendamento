package com.barbearia.agendamento.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.barbearia.agendamento.model.*;
import com.barbearia.agendamento.repository.*;
import com.barbearia.agendamento.util.ListaOrdenadaAgendamento;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/barbearia")
@Tag(name = "Barbearia", description = "Endpoints para cadastro, login, agendamento e administração da barbearia")
public class BarbeariaController {

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    FuncionarioRepository funcionarioRepository;

    @Autowired
    ServicoRepository servicoRepository;

    @Autowired
    AgendamentoRepository agendamentoRepository;

    // === Páginas principais ===
    @GetMapping("/cadastro")
    public String mostrarCadastro() {
        return "cadastro";
    }

    @Operation(summary = "Cadastrar novo cliente", description = "Cria um novo cliente na base de dados da barbearia. Retorna redirecionamento para a tela de login em caso de sucesso.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cadastro realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou cliente já existente")
    })
    @PostMapping("/cadastro")
    public String cadastrarCliente(
            @Parameter(description = "Nome completo do cliente", example = "João da Silva") @RequestParam String nomeCliente,

            @Parameter(description = "Telefone com DDD", example = "(11)99999-8888") @RequestParam String telefoneCliente,

            @Parameter(description = "E-mail válido", example = "joao@gmail.com") @RequestParam String emailCliente,

            RedirectAttributes redirectAttributes) {

        String telefoneLimpo = telefoneCliente.replaceAll("\\D", "");

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

        if (clienteRepository.findByTelefoneCliente(telefoneCliente).isPresent()) {
            redirectAttributes.addFlashAttribute("erroTelefoneExistente", "Telefone já cadastrado!");
            return "redirect:/barbearia/cadastro";
        }

        if (clienteRepository.findByEmailCliente(emailCliente).isPresent()) {
            redirectAttributes.addFlashAttribute("erroEmailExistente", "E-mail já cadastrado!");
            return "redirect:/barbearia/cadastro";
        }

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

    @Operation(summary = "Login de cliente", description = "Valida o nome e telefone do cliente para iniciar sessão.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido"),
            @ApiResponse(responseCode = "400", description = "Telefone inválido ou cliente não encontrado")
    })
    @PostMapping("/login")
    public String verificarCliente(
            @Parameter(description = "Nome completo do cliente", example = "João da Silva") @RequestParam String nomeCliente,

            @Parameter(description = "Telefone com DDD", example = "(11)99999-8888") @RequestParam String telefoneCliente,

            RedirectAttributes redirectAttributes,
            HttpSession session) {

        String telefoneLimpo = telefoneCliente.replaceAll("\\D", "");
        if (telefoneLimpo.length() < 10 || telefoneLimpo.length() > 11) {
            redirectAttributes.addFlashAttribute("erroTelefone", "Telefone inválido. Informe um número com DDD.");
            return "redirect:/barbearia/login";
        }

        Optional<Cliente> clienteComTelefone = clienteRepository.findByTelefoneCliente(telefoneCliente);

        if (clienteComTelefone.isPresent()) {
            Cliente clienteExistente = clienteComTelefone.get();
            if (!clienteExistente.getNomeCliente().equalsIgnoreCase(nomeCliente)) {
                redirectAttributes.addFlashAttribute("erroTelefoneExistente", "Telefone já cadastrado com outro nome.");
                return "redirect:/barbearia/login";
            }

            session.setAttribute("clienteLogado", clienteExistente);
            return "redirect:/barbearia/servicos";
        }

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

    @Operation(summary = "Realizar agendamento", description = "Cria um novo agendamento para o cliente logado, vinculando um serviço e horário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Horário já ocupado ou dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Cliente não logado")
    })
    @PostMapping("/agendar")
    @ResponseBody
    public ResponseEntity<String> realizarAgendamento(
            @Parameter(description = "ID do serviço", example = "1") @RequestParam Integer idServico,
            @Parameter(description = "Data no formato YYYY-MM-DD", example = "2025-11-02") @RequestParam String dataAgendamento,
            @Parameter(description = "Hora no formato HH:mm", example = "15:30") @RequestParam String horaAgendamento,
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

    @Operation(summary = "Listar agendamentos do cliente", description = "Retorna uma lista textual dos agendamentos do cliente atualmente logado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Cliente não autenticado")
    })
    @GetMapping("/agendamentos-do-usuario")
    @ResponseBody
    public ResponseEntity<String> listarAgendamentosDoUsuario(
            HttpSession session,
            @Parameter(description = "Filtrar por ID do serviço (opcional)", example = "1") @RequestParam(value = "idServico", required = false) Long idServico) {

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

        return ResponseEntity.ok(fila.viewCliente());
    }

    @Operation(summary = "Excluir agendamento", description = "Permite que o cliente exclua um agendamento existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento excluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Cliente não logado"),
            @ApiResponse(responseCode = "403", description = "Tentativa de excluir agendamento de outro cliente"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @PostMapping("/excluir-agendamento")
    @ResponseBody
    public ResponseEntity<String> excluirAgendamento(
            @Parameter(description = "ID do agendamento", example = "5") @RequestParam Integer idAgendamento,
            HttpSession session) {
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

    @Operation(summary = "Obter horários ocupados", description = "Retorna uma lista de horários já agendados para determinada data e serviço.")
    @ApiResponse(responseCode = "200", description = "Lista de horários ocupados retornada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @GetMapping("/horarios-ocupados")
    @ResponseBody
    public List<String> obterHorariosOcupados(
            @Parameter(description = "Data no formato YYYY-MM-DD", example = "2025-11-05") @RequestParam String data,
            @Parameter(description = "ID do serviço", example = "2") @RequestParam Integer idServico) {

        LocalDate dataAgendamento = LocalDate.parse(data);
        List<Agendamento> agendamentos = agendamentoRepository.findByDataAgendamento(dataAgendamento);
        return agendamentos.stream().map(ag -> ag.getHoraAgendamento().toString()).toList();
    }

    // === ROTAS DE FUNCIONÁRIO (Admin) ===

    @GetMapping("/loginadm")
    public String mostrarTelaLoginAdmin() {
        return "loginAdmin";
    }

    @PostMapping("/loginadm")
    public String processarLoginFuncionario(
            @RequestParam String nomeFuncionario,
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

    @Operation(summary = "Confirmar agendamento (Admin)", description = "Marca um agendamento como concluído.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento confirmado"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })

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
    public ResponseEntity<String> confirmarAgendamento(
            @Parameter(description = "ID do agendamento", example = "3") @PathVariable Integer id) {

        Optional<Agendamento> opt = agendamentoRepository.findById(id);
        if (opt.isPresent()) {
            Agendamento agendamento = opt.get();
            agendamento.setStatusAgendamento(Agendamento.StatusAgendamento.Concluído);
            agendamentoRepository.save(agendamento);
            return ResponseEntity.ok("Agendamento confirmado com sucesso");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Agendamento não encontrado");
    }

    @Operation(summary = "Cancelar agendamento (Admin)", description = "Marca um agendamento como cancelado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento cancelado"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @PostMapping("/admin/{id}/cancelar")
    public ResponseEntity<String> cancelarAgendamento(
            @Parameter(description = "ID do agendamento", example = "3") @PathVariable Integer id) {

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

        if ("corte".equalsIgnoreCase(ordenar)) {
            List<Object[]> ranking = agendamentoRepository.findServicosMaisFeitos();
            List<String> ordemServicos = ranking.stream()
                    .map(obj -> (String) obj[0])
                    .toList();

            List<Agendamento> todos = agendamentoRepository.findAll();

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

            List<Agendamento> ordenados = todos.stream()
                    .sorted((a, b) -> {
                        int idxA = ordemServicos.indexOf(a.getServico().getNomeCorte());
                        int idxB = ordemServicos.indexOf(b.getServico().getNomeCorte());
                        if (idxA == -1)
                            idxA = ordemServicos.size();
                        if (idxB == -1)
                            idxB = ordemServicos.size();
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

            int start = Math.min(page * size, ordenados.size());
            int end = Math.min(start + size, ordenados.size());
            List<Agendamento> pageContent = ordenados.subList(start, end);

            pagina = new PageImpl<>(pageContent, pageable, ordenados.size());
        } else {
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

        model.addAttribute("pagina", pagina);
        model.addAttribute("agendamentos", pagina.getContent());
        model.addAttribute("ordenar", ordenar);
        model.addAttribute("status", status);
        model.addAttribute("nome", nome);
        model.addAttribute("paginaAtual", page);
        model.addAttribute("totalPaginas", pagina.getTotalPages());

        return "relatorio";

    }
}
