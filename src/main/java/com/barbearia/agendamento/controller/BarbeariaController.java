package com.barbearia.agendamento.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.barbearia.agendamento.model.Cliente;
import com.barbearia.agendamento.repository.ClienteRepository;

@Controller
public class BarbeariaController {

    @Autowired
    ClienteRepository repository;

    @GetMapping("/barbearia/login")
    public String mostrarLogin() {
        return "index";
    }

    @PostMapping("/barbearia/login")
public String processarLogin(@RequestParam String nomeCliente, @RequestParam String telefoneCliente) {
    Optional<Cliente> usuarioOpt = repository.findByNomeClienteAndTelefoneCliente(nomeCliente, telefoneCliente);

    if (usuarioOpt.isPresent()) {
        return "redirect:/barbearia/agendamento";
    } else {
        Cliente novoUsuario = new Cliente();
        novoUsuario.setNomeCliente(nomeCliente);
        novoUsuario.setTelefoneCliente(telefoneCliente);
        repository.save(novoUsuario);
        return "redirect:/barbearia/agendamento";
        }
    }

    @GetMapping("/barbearia/agendamento")
    public String mostrarAgendamento() {
        return "agendamento";
    }

}