package com.barbearia.agendamento.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "tbl_usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    @Column(nullable = false, length = 80, unique = true)
    private String emailUsuario;

    @Column(nullable = false, length = 255)
    private String senhaUsuario;

    @ManyToOne
    @JoinColumn(name = "id_permissao", nullable = false)
    private Permissao permissao;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_funcionario")
    private Funcionario funcionario;

    public Usuario() {}

    public Usuario(Integer idUsuario, String emailUsuario, String senhaUsuario,
                   Permissao permissao, Cliente cliente, Funcionario funcionario) {
        this.idUsuario = idUsuario;
        this.emailUsuario = emailUsuario;
        this.senhaUsuario = senhaUsuario;
        this.permissao = permissao;
        this.cliente = cliente;
        this.funcionario = funcionario;
    }

    // ==============================
    // MÉTODOS DO USERDETAILS
    // ==============================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(
                "ROLE_" + permissao.getTipoPermissao().name()
        ));
    }

    @Override
    public String getPassword() {
        return senhaUsuario;
    }

    @Override
    public String getUsername() {
        return emailUsuario;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    // ==============================
    // GETTERS E SETTERS (mantém os que já tinha)
    // ==============================

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getEmailUsuario() { return emailUsuario; }
    public void setEmailUsuario(String emailUsuario) { this.emailUsuario = emailUsuario; }

    public String getSenhaUsuario() { return senhaUsuario; }
    public void setSenhaUsuario(String senhaUsuario) { this.senhaUsuario = senhaUsuario; }

    public Permissao getPermissao() { return permissao; }
    public void setPermissao(Permissao permissao) { this.permissao = permissao; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }
}