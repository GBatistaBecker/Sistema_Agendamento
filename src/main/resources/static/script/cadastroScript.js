document.addEventListener("DOMContentLoaded", function () {
  // --- SELEÇÃO DOS ELEMENTOS ---
  const form = document.getElementById("cadastro-form"); // ATENÇÃO: Use o ID correto do form!
  const nomeInput = document.getElementById("nomeCliente");
  const telefoneInput = document.getElementById("telefoneCliente");
  const emailInput = document.getElementById("emailCliente");
    const senhaInput = document.getElementById("senhaUsuario");

  // --- FUNÇÃO PARA MOSTRAR ALERTAS (AGORA DEFINIDA) ---
  function mostrarAlerta(mensagem) {
    // Remove qualquer alerta anterior para não acumular
    const alertaExistente = document.querySelector(".custom-alert");
    if (alertaExistente) {
      alertaExistente.remove();
    }

    // Cria o elemento do alerta
    const alertaDiv = document.createElement("div");
    alertaDiv.className = "custom-alert";
    alertaDiv.textContent = mensagem;

    // Adiciona o alerta no início do container
    const container = document.querySelector(".cadastro-container");
    container.prepend(alertaDiv);

    // Opcional: faz o alerta desaparecer depois de 5 segundos
    setTimeout(() => {
      alertaDiv.style.opacity = '0';
      setTimeout(() => alertaDiv.remove(), 500); // Remove da tela após a transição
    }, 5000);
  }

  // --- MÁSCARA DE TELEFONE ---
  telefoneInput.addEventListener("input", function (e) {
    let valor = e.target.value.replace(/\D/g, ""); // Remove tudo que não é dígito

    if (valor.length > 11) {
      valor = valor.substring(0, 11);
    }

    let formatado = "";
    if (valor.length > 10) {
      // Formato para (XX) XXXXX-XXXX
      formatado = valor.replace(/(\d{2})(\d{5})(\d{4})/, "($1) $2-$3");
    } else if (valor.length > 6) {
      // Formato para (XX) XXXX-XXXX
      formatado = valor.replace(/(\d{2})(\d{4})(\d{4})/, "($1) $2-$3");
    } else if (valor.length > 2) {
      formatado = valor.replace(/(\d{2})(\d+)/, "($1) $2");
    } else {
      formatado = valor.replace(/(\d*)/, "($1");
    }

    e.target.value = formatado;
  });

  // --- FILTRO PARA CAMPO NOME (PERMITE APENAS LETRAS E ESPAÇOS) ---
  nomeInput.addEventListener("input", function (e) {
    e.target.value = e.target.value.replace(/[^a-zA-ZÀ-ÿ\s]/g, "");
  });

  // --- VALIDAÇÃO NO ENVIO DO FORMULÁRIO ---
  form.addEventListener("submit", function (e) {
    const telefone = telefoneInput.value.replace(/\D/g, "");
    const nome = nomeInput.value.trim();
    const email = emailInput.value.trim();

    // Validação corrigida para aceitar 10 ou 11 dígitos
    if (telefone.length < 10 || telefone.length > 11) {
      e.preventDefault(); // Impede o envio do formulário
      mostrarAlerta("Telefone inválido. Informe um número com 10 ou 11 dígitos, incluindo DDD.");
      return; // Para a execução para não mostrar múltiplos alertas
    }

    if (nome === "") {
      e.preventDefault();
      mostrarAlerta("Nome inválido. O campo não pode ser vazio.");
      return;
    }

    const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!regexEmail.test(email)) {
      e.preventDefault();
      mostrarAlerta("E-mail inválido. Informe um endereço de e-mail válido.");
      return;
    }
  });

  // --- EXIBIR ERROS VINDOS DO SERVIDOR (com Thymeleaf) ---
  // Esta parte funciona se você adicionar os elementos no seu HTML, como explicado abaixo.
  const erroServidor = document.getElementById("server-error-message");
  if (erroServidor && erroServidor.textContent.trim() !== "") {
    mostrarAlerta(erroServidor.textContent);
  }

  // --- VALIDAÇAO SENHA
  const senha = senhaInput.value.trim();
  if (senha.length < 8) {
    e.preventDefault();
    mostrarAlerta("A senha deve ter pelo menos 8 caracteres.");
    return;
  }
});