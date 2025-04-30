package com.barbearia.agendamento.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.barbearia.agendamento.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByNomeClienteAndTelefoneCliente(String nomeCliente, String telefoneCliente);
}
