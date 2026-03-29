package com.barbearia.agendamento.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barbearia.agendamento.model.Permissao;

public interface PermissaoRepository extends JpaRepository<Permissao, Integer> {

    Optional<Permissao> findByTipoPermissao(String tipoPermissao);

}