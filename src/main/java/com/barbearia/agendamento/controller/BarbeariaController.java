package com.barbearia.agendamento.controller;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.barbearia.agendamento.service.EmailService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.barbearia.agendamento.model.*;
import com.barbearia.agendamento.repository.*;
import com.barbearia.agendamento.model.Permissao.TipoPermissao;
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
    EmailService emailService;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    FuncionarioRepository funcionarioRepository;

    @Autowired
    ServicoRepository servicoRepository;

    @Autowired
    AgendamentoRepository agendamentoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // === Páginas principais ===
    @GetMapping("/cadastro")
    public String mostrarCadastro() {
        return "cadastro";
    }

    @Operation(summary = "Cadastrar novo cliente", description = "Cria um novo cliente na base de dados da barbearia.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cadastro realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou cliente já existente")
    })
    @PostMapping("/cadastro")
    public String cadastrarCliente(
            @RequestParam String nomeCliente,
            @RequestParam String telefoneCliente,
            @RequestParam String emailCliente,
            @RequestParam String senhaUsuario,
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

        if (senhaUsuario.length() < 6) {
            redirectAttributes.addFlashAttribute("erroSenha", "A senha deve ter pelo menos 6 caracteres.");
            return "redirect:/barbearia/cadastro";
        }

        Cliente novoCliente = new Cliente();
        novoCliente.setNomeCliente(nomeCliente);
        novoCliente.setTelefoneCliente(telefoneCliente);
        novoCliente.setEmailCliente(emailCliente);
        clienteRepository.save(novoCliente);

        Usuario usuario = new Usuario();
        usuario.setEmailUsuario(emailCliente);
        usuario.setSenhaUsuario(passwordEncoder.encode(senhaUsuario));
        usuario.setCliente(novoCliente);

        Permissao permissao = new Permissao();
        permissao.setIdPermissao(1);
        usuario.setPermissao(permissao);

        usuarioRepository.save(usuario);

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

    @Operation(summary = "Login de cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou cliente não encontrado")
    })
    @PostMapping("/login")
    public String verificarCliente(
            @RequestParam String emailUsuario,
            @RequestParam String senhaUsuario,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            HttpServletRequest request) {

        if (emailUsuario.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("erroLogin", "Email não pode ser vazio.");
            return "redirect:/barbearia/login";
        }

        if (senhaUsuario.length() < 6) {
            redirectAttributes.addFlashAttribute("erroLogin", "Senha inválida.");
            return "redirect:/barbearia/login";
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailUsuario(emailUsuario.trim());

        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("erroLogin", "Usuário não encontrado.");
            return "redirect:/barbearia/login";
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(senhaUsuario, usuario.getSenhaUsuario())) {
            redirectAttributes.addFlashAttribute("erroLogin", "Email ou senha incorretos.");
            return "redirect:/barbearia/login";
        }

        // Registra no Spring Security
        var authorities = List.of(new SimpleGrantedAuthority(
                "ROLE_" + usuario.getPermissao().getTipoPermissao().name()
        ));
        var auth = new UsernamePasswordAuthenticationToken(usuario, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.saveContext(SecurityContextHolder.getContext(), request,
                (HttpServletResponse) request.getServletContext()
                        .getAttribute("javax.servlet.http.HttpServletResponse"));

        session.setAttribute("usuarioLogado", usuario);

        if (usuario.getPermissao().getTipoPermissao() == TipoPermissao.ADMIN) {
            return "redirect:/barbearia/admin";
        }
        return "redirect:/barbearia/servicos";
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

    @Operation(summary = "Realizar agendamento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Horário já ocupado ou dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Cliente não logado")
    })
    @PostMapping("/agendar")
    @ResponseBody
    public ResponseEntity<String> realizarAgendamento(
            @RequestParam Integer idServico,
            @RequestParam String dataAgendamento,
            @RequestParam String horaAgendamento,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null)
            return ResponseEntity.badRequest().body("Usuário não logado.");

        Cliente cliente = usuario.getCliente();

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

        String email = cliente.getEmailCliente();
        String nome = cliente.getNomeCliente();

        String mensagem = "Olá " + nome + "!\n\n" +
                "Seu agendamento foi realizado com sucesso.\n" +
                "Data: " + agendamento.getDataAgendamento() + "\n" +
                "Hora: " + agendamento.getHoraAgendamento() + "\n\n" +
                "Aguardamos você!";

        try {
            emailService.enviarEmail(email, "Agendamento Realizado", mensagem);
            System.out.println("EMAIL ENVIADO PARA: " + email);
        } catch (Exception e) {
            System.out.println("ERRO AO ENVIAR EMAIL:");
            e.printStackTrace();
        }

        return ResponseEntity.ok("Agendamento realizado com sucesso.");
    }

    @Operation(summary = "Listar agendamentos do cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Cliente não autenticado")
    })
    @GetMapping("/agendamentos-do-usuario")
    @ResponseBody
    public ResponseEntity<String> listarAgendamentosDoUsuario(
            HttpSession session,
            @RequestParam(value = "idServico", required = false) Long idServico) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não logado.");
        }

        Cliente cliente = usuario.getCliente();

        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cliente não encontrado.");
        }

        ListaOrdenadaAgendamento fila = new ListaOrdenadaAgendamento();

        List<Agendamento> agendamentos =
                agendamentoRepository.findByClienteId(cliente.getIdCliente());

        for (Agendamento ag : agendamentos) {
            if (idServico == null || ag.getServico().getIdCorte().equals(idServico)) {
                fila.inserirOrdenado(ag);
            }
        }

        return ResponseEntity.ok(fila.viewCliente());
    }

    @Operation(summary = "Excluir agendamento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento excluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Cliente não logado"),
            @ApiResponse(responseCode = "403", description = "Tentativa de excluir agendamento de outro cliente"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @PostMapping("/excluir-agendamento")
    @ResponseBody
    public ResponseEntity<String> excluirAgendamento(
            @RequestParam Integer idAgendamento,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não logado.");
        }

        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(idAgendamento);

        if (agendamentoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Agendamento não encontrado.");
        }

        Agendamento agendamento = agendamentoOpt.get();

        if (!agendamento.getCliente().getIdCliente()
                .equals(usuario.getCliente().getIdCliente())) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Não autorizado.");
        }

        agendamentoRepository.delete(agendamento);

        return ResponseEntity.ok("Agendamento excluído com sucesso.");
    }

    @Operation(summary = "Obter horários ocupados")
    @ApiResponse(responseCode = "200", description = "Lista de horários ocupados retornada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @GetMapping("/horarios-ocupados")
    @ResponseBody
    public List<String> obterHorariosOcupados(
            @RequestParam String data,
            @RequestParam Integer idServico) {

        LocalDate dataAgendamento = LocalDate.parse(data);
        List<Agendamento> agendamentos = agendamentoRepository.findByDataAgendamento(dataAgendamento);
        return agendamentos.stream().map(ag -> ag.getHoraAgendamento().toString()).toList();
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/barbearia/login";
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

    @PreAuthorize("hasRole('ADMIN')") // ← adicionado
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

    @Operation(summary = "Cancelar agendamento (Admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento cancelado"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
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

    @PreAuthorize("hasRole('ADMIN')") // ← adicionado
    @GetMapping("/relatorio")
    public String relatorio(
            @RequestParam(required = false) String ordenar,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        if (page == null) page = 0;

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
                        if (idxA == -1) idxA = ordemServicos.size();
                        if (idxB == -1) idxB = ordemServicos.size();
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

    @PreAuthorize("hasRole('ADMIN')") // ← adicionado
    @Operation(summary = "Exportar relatório em PDF")
    @GetMapping("/relatorio/exportar")
    public ResponseEntity<byte[]> exportarRelatorioPdf(
            @RequestParam(required = false) String ordenar,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String nome) {

        List<Agendamento> agendamentos = buscarAgendamentosFiltrados(ordenar, status, nome);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            Font fontTitulo = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(0, 0, 0));
            Paragraph titulo = new Paragraph("Relatório de Agendamentos - Pitbull Barber", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            Font fontFiltro = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(100, 100, 100));
            StringBuilder filtroInfo = new StringBuilder("Filtros aplicados: ");
            if (status != null && !status.isEmpty()) filtroInfo.append("Status: ").append(status).append(" | ");
            if (nome != null && !nome.isEmpty()) filtroInfo.append("Cliente: ").append(nome).append(" | ");
            if (ordenar != null && !ordenar.isEmpty()) filtroInfo.append("Ordenação: ").append(ordenar).append(" | ");

            Paragraph filtros = new Paragraph(filtroInfo.toString(), fontFiltro);
            filtros.setSpacingAfter(10);
            document.add(filtros);

            Paragraph dataGeracao = new Paragraph("Gerado em: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fontFiltro);
            dataGeracao.setSpacingAfter(20);
            document.add(dataGeracao);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            Font fontHeader = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(255, 255, 255));
            Color headerColor = new Color(33, 37, 41);

            String[] headers = {"#", "Cliente", "Serviço", "Data", "Hora", "Status"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
                cell.setBackgroundColor(headerColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                table.addCell(cell);
            }

            Font fontData = new Font(Font.HELVETICA, 10, Font.NORMAL);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            int contador = 1;
            for (Agendamento a : agendamentos) {
                Color rowColor = (contador % 2 == 0) ? new Color(248, 249, 250) : new Color(255, 255, 255);

                PdfPCell cellNum = new PdfPCell(new Phrase(String.valueOf(contador), fontData));
                cellNum.setBackgroundColor(rowColor);
                cellNum.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellNum);

                PdfPCell cellCliente = new PdfPCell(new Phrase(a.getCliente().getNomeCliente(), fontData));
                cellCliente.setBackgroundColor(rowColor);
                table.addCell(cellCliente);

                PdfPCell cellServico = new PdfPCell(new Phrase(a.getServico().getNomeCorte(), fontData));
                cellServico.setBackgroundColor(rowColor);
                cellServico.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellServico);

                PdfPCell cellData = new PdfPCell(new Phrase(a.getDataAgendamento().format(dateFormatter), fontData));
                cellData.setBackgroundColor(rowColor);
                cellData.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellData);

                PdfPCell cellHora = new PdfPCell(new Phrase(a.getHoraAgendamento().toString(), fontData));
                cellHora.setBackgroundColor(rowColor);
                cellHora.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellHora);

                PdfPCell cellStatus = new PdfPCell(new Phrase(a.getStatusAgendamento().name(), fontData));
                cellStatus.setBackgroundColor(rowColor);
                cellStatus.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellStatus);

                contador++;
            }

            document.add(table);

            Font fontTotal = new Font(Font.HELVETICA, 12, Font.BOLD);
            Paragraph total = new Paragraph("Total de agendamentos: " + agendamentos.size(), fontTotal);
            total.setSpacingBefore(20);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.close();

            HttpHeaders headersPdf = new HttpHeaders();
            headersPdf.setContentType(MediaType.APPLICATION_PDF);
            headersPdf.setContentDispositionFormData("attachment", "relatorio-agendamentos.pdf");

            return new ResponseEntity<>(baos.toByteArray(), headersPdf, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private List<Agendamento> buscarAgendamentosFiltrados(String ordenar, String status, String nome) {
        List<Agendamento> agendamentos;

        if ("corte".equalsIgnoreCase(ordenar)) {
            List<Object[]> ranking = agendamentoRepository.findServicosMaisFeitos();
            List<String> ordemServicos = ranking.stream()
                    .map(obj -> (String) obj[0])
                    .toList();

            agendamentos = agendamentoRepository.findAll();

            if (status != null && !status.isEmpty()) {
                agendamentos = agendamentos.stream()
                        .filter(a -> a.getStatusAgendamento().name().equalsIgnoreCase(status))
                        .toList();
            }

            if (nome != null && !nome.isEmpty()) {
                agendamentos = agendamentos.stream()
                        .filter(a -> a.getCliente().getNomeCliente().toLowerCase().contains(nome.toLowerCase()))
                        .toList();
            }

            agendamentos = agendamentos.stream()
                    .sorted((a, b) -> {
                        int idxA = ordemServicos.indexOf(a.getServico().getNomeCorte());
                        int idxB = ordemServicos.indexOf(b.getServico().getNomeCorte());
                        if (idxA == -1) idxA = ordemServicos.size();
                        if (idxB == -1) idxB = ordemServicos.size();
                        int cmp = Integer.compare(idxA, idxB);
                        if (cmp == 0) {
                            cmp = a.getDataAgendamento().compareTo(b.getDataAgendamento());
                            if (cmp == 0) cmp = a.getHoraAgendamento().compareTo(b.getHoraAgendamento());
                        }
                        return cmp;
                    })
                    .toList();
        } else {
            Sort sort = Sort.by("dataAgendamento").ascending().and(Sort.by("horaAgendamento"));
            agendamentos = agendamentoRepository.findAll(sort);

            if (status != null && !status.isEmpty()) {
                agendamentos = agendamentos.stream()
                        .filter(a -> a.getStatusAgendamento().name().equalsIgnoreCase(status))
                        .toList();
            }

            if (nome != null && !nome.isEmpty()) {
                agendamentos = agendamentos.stream()
                        .filter(a -> a.getCliente().getNomeCliente().toLowerCase().contains(nome.toLowerCase()))
                        .toList();
            }
        }

        return agendamentos;
    }
}