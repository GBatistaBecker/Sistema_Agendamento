document.addEventListener("DOMContentLoaded", function () {
  // --- SELEÇÃO DOS ELEMENTOS ---
  const form = document.getElementById("cadastro-form");
  const nomeInput = document.getElementById("nomeCliente");
  const telefoneInput = document.getElementById("telefoneCliente");
  const emailInput = document.getElementById("emailCliente");
  const senhaInput = document.getElementById("senhaUsuario");
  if (form) {
      form.reset();
    } // limpa as linhas ao carregar

  // --- FUNÇÃO PARA MOSTRAR ALERTAS ---
  function mostrarAlerta(mensagem) {
    const alertaExistente = document.querySelector(".custom-alert-error, .custom-alert-success");
    if (alertaExistente) {
      alertaExistente.remove();
    }

    // Cria o elemento do alerta
    const alertaDiv = document.createElement("div");
    alertaDiv.className = "custom-alert-error";
    alertaDiv.textContent = mensagem;

    // Adiciona o alerta no início do container
    const container = document.querySelector(".cadastro-container");
    container.prepend(alertaDiv);

    // Opcional: faz o alerta desaparecer depois de 5 segundos
    setTimeout(() => {
      alertaDiv.style.opacity = '0';
      setTimeout(() => alertaDiv.remove(), 500);
    }, 5000);
  }

  // --- FUNÇÃO PARA MOSTRAR MENSAGEM DE SUCESSO ---
  function mostrarSucesso(mensagem) {
    const alertaExistente = document.querySelector(".custom-alert-error, .custom-alert-success");
    if (alertaExistente) {
      alertaExistente.remove();
    }

    const alertaDiv = document.createElement("div");
    alertaDiv.className = "custom-alert-success";
    alertaDiv.textContent = mensagem;

    const container = document.querySelector(".cadastro-container");
    container.prepend(alertaDiv);
  }

  // --- MÁSCARA DE TELEFONE ---
  telefoneInput.addEventListener("input", function (e) {
    let valor = e.target.value.replace(/\D/g, "");
    if (valor.length > 11) valor = valor.substring(0, 11);

    let formatado = "";
    if (valor.length > 0) {
        formatado = "(" + valor;
        if (valor.length > 2) {
            formatado = "(" + valor.substring(0, 2) + ") " + valor.substring(2);
        }
        if (valor.length > 7) {
            if (valor.length === 11) {
                formatado = "(" + valor.substring(0, 2) + ") " + valor.substring(2, 7) + "-" + valor.substring(7);
            } else {
                formatado = "(" + valor.substring(0, 2) + ") " + valor.substring(2, 6) + "-" + valor.substring(6);
            }
        }
    }
    e.target.value = formatado;
  });

  // --- FILTRO PARA CAMPO NOME ---
  nomeInput.addEventListener("input", function (e) {
    e.target.value = e.target.value.replace(/[^a-zA-ZÀ-ÿ\s]/g, "");
  });

  // --- VALIDAÇÃO NO ENVIO DO FORMULÁRIO ---
  form.addEventListener("submit", function (e) {
    const telefone = telefoneInput.value.replace(/\D/g, "");
    const nome = nomeInput.value.trim();
    const email = emailInput.value.trim();
    const senha = senhaInput.value;

    // 1. Validação de Telefone
    if (telefone.length < 10 || telefone.length > 11) {
      e.preventDefault();
      mostrarAlerta("Telefone inválido. Informe DDD + número.");
      return;
    }

    // 2. Validação de Nome
    if (nome === "") {
      e.preventDefault();
      mostrarAlerta("Nome inválido. O campo não pode ser vazio.");
      return;
    }

    // 3. Validação de Email
    const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!regexEmail.test(email)) {
      e.preventDefault();
      mostrarAlerta("E-mail inválido. Informe um endereço de e-mail válido.");
      return;
    }

    // 4. VALIDAÇÃO DE SENHA (Modificada) realizada para a sprint
    // Exigências: 1 Maiúscula (?=.*[A-Z]), 1 Número (?=.*\d), 1 Especial (?=.*[@$!%*?&])
    const regexSenha = /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{6,}$/;

    if (!regexSenha.test(senha)) {
      e.preventDefault();
      mostrarAlerta("Senha inválida! Use pelo menos 1 letra maiúscula, 1 número e 1 caractere especial.");
      return;
    }

    // Todas as validações passaram - mostrar mensagem de sucesso
    e.preventDefault();
    mostrarSucesso("Cadastro realizado com sucesso! Redirecionando...");

    // Desabilitar botão de cadastro para evitar duplo clique
    const botaoSubmit = form.querySelector('button[type="submit"]');
    if (botaoSubmit) {
      botaoSubmit.disabled = true;
      botaoSubmit.textContent = "Processando...";
    }

    // Aguardar 2 segundos para o usuário ver a mensagem antes de enviar
    setTimeout(() => {
      form.submit();
    }, 2000);
  });

  // --- EXIBIR ERROS DO SERVIDOR ---
  const erroServidor = document.getElementById("server-error-message");
  if (erroServidor && erroServidor.textContent.trim() !== "") {
    mostrarAlerta(erroServidor.textContent);
  }
});
