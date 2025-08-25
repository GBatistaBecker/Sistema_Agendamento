document.addEventListener("DOMContentLoaded", function () {
  const telefoneInput = document.getElementById("telefoneCliente");
  const nomeInput = document.getElementById("nomeCliente");
  const form = document.getElementById("login-form");

  let backspacePressionado = false;

  telefoneInput.addEventListener("keydown", function (e) {
    backspacePressionado = e.key === "Backspace";
  });

  telefoneInput.addEventListener("input", function (e) {
    let valor = telefoneInput.value.replace(/\D/g, "");
    let pos = telefoneInput.selectionStart;

    if (valor.length > 11) valor = valor.substring(0, 11);

    // Aplicar a máscara apenas se o usuário não estiver deletando um formatador
    if (!backspacePressionado || valor.length === 11) {
      let formatado = valor;
      if (valor.length > 6) {
        formatado = `(${valor.substring(0, 2)}) ${valor.substring(2, 7)}-${valor.substring(7)}`;
      } else if (valor.length > 2) {
        formatado = `(${valor.substring(0, 2)}) ${valor.substring(2)}`;
      } else if (valor.length > 0) {
        formatado = `(${valor}`;
      }

      telefoneInput.value = formatado;
      telefoneInput.setSelectionRange(formatado.length, formatado.length);
    }

    backspacePressionado = false;
  });

  // Máscara para nome
  nomeInput.addEventListener("input", function (e) {
    e.target.value = e.target.value.replace(/[^a-zA-Z\s]/g, "");
  });

  // Validação no envio do formulário
  form.addEventListener("submit", function (e) {
    const telefone = telefoneInput.value.replace(/\D/g, "");
    const nome = nomeInput.value.trim();
    const regexNome = /^[a-zA-Z\s]+$/;

    if (telefone.length !== 11) {
      alert("Telefone inserido incorretamente. Informe um número válido com DDD.");
      e.preventDefault();
    }

    if (!regexNome.test(nome) || nome === "") {
      alert("Nome inválido. Informe apenas letras e espaços.");
      e.preventDefault();
    }
  });

  // Alertas do backend
  const erroTelefone = document.getElementById("erroTelefone");
  const erroTelefoneExistente = document.getElementById("erroTelefoneExistente");

  if (erroTelefone) alert(erroTelefone.value);
  if (erroTelefoneExistente) alert(erroTelefoneExistente.value);
});