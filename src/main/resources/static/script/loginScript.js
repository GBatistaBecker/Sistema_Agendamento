// /script/loginScript.js

document.addEventListener("DOMContentLoaded", function () {
  const senhaInput = document.getElementById("senhaUsuario");
  const nomeInput = document.getElementById("nomeCliente");
  const form = document.getElementById("login-form");

  // --- FUN��O PARA MOSTRAR ALERTAS DE ERRO ---
  function mostrarAlertaErro(mensagem) {
    const alertaExistente = document.querySelector(".custom-alert-error");
    if (alertaExistente) {
      alertaExistente.remove();
    }

    const alertaDiv = document.createElement("div");
    alertaDiv.className = "custom-alert-error";
    alertaDiv.textContent = mensagem;

    const container = document.querySelector(".login-container");
    container.prepend(alertaDiv);
  }

  // --- FUN��O PARA MOSTRAR ALERTA DE SUCESSO ---
  function mostrarAlertaSucesso(mensagem) {
    const alertaDiv = document.createElement("div");
    alertaDiv.className = "custom-alert-success";
    alertaDiv.textContent = mensagem;

    const container = document.querySelector(".login-container");
    container.prepend(alertaDiv);

    setTimeout(() => {
      alertaDiv.style.opacity = '0';
      setTimeout(() => alertaDiv.remove(), 600);
    }, 5000);
  }

  // --- CAMPO SENHA - SEM M�SCARA ---
  senhaInput.addEventListener("input", (e) => {
    // Sem m�scara para senha
  });

  // --- VALIDA��O NO ENVIO DO FORMUL�RIO ---
  form.addEventListener("submit", function (e) {
    const senha = senhaInput.value.trim();
    const nome = nomeInput.value.trim();

    if (senha.length < 6) {
      e.preventDefault();
      mostrarAlertaErro("Senha deve ter pelo menos 6 caracteres.");
      return;
    }

    if (nome === "") {
      e.preventDefault();
      mostrarAlertaErro("O campo Nome � obrigat�rio.");
      return;
    }
  });

  // --- LIDANDO COM ERROS VINDOS DO BACKEND ---
  const serverErrorDiv = document.getElementById("server-error-message");
  if (serverErrorDiv && serverErrorDiv.textContent.trim() !== "") {
    mostrarAlertaErro(serverErrorDiv.textContent);
  }

  // --- EXIBIR MENSAGEM DE SUCESSO DO CADASTRO ---
  const successDiv = document.getElementById("success-message");
  if (successDiv && successDiv.textContent.trim() !== "") {
    mostrarAlertaSucesso(successDiv.textContent);
  }
});
