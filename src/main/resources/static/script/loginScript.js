document.addEventListener("DOMContentLoaded", function () {
  const telefoneInput = document.getElementById("telefoneCliente");
  const form = document.getElementById("login-form");

  // Máscara para telefone (XX) XXXXX-XXXX
  telefoneInput.addEventListener("input", function (e) {
    let valor = e.target.value.replace(/\D/g, "");

    if (valor.length > 11) valor = valor.substring(0, 11);

    if (valor.length > 6) {
      valor = `(${valor.substring(0, 2)}) ${valor.substring(2, 7)}-${valor.substring(7)}`;
    } else if (valor.length > 2) {
      valor = `(${valor.substring(0, 2)}) ${valor.substring(2)}`;
    } else if (valor.length > 0) {
      valor = `(${valor}`;
    }

    e.target.value = valor;
  });

  // Validação do telefone no envio do formulário
form.addEventListener("submit", function (e) {
  const telefone = telefoneInput.value.replace(/\D/g, ""); // Remove tudo que não é número

  if (telefone.length !== 11) {
    alert("Telefone inserido incorretamente. Informe um número válido com DDD.");
    e.preventDefault(); // Impede o envio do formulário
  }
  });

  document.addEventListener("DOMContentLoaded", function () {
  const telefoneInput = document.getElementById("telefoneCliente");
  const form = document.getElementById("login-form");

  // Máscara para telefone (XX) XXXXX-XXXX
  telefoneInput.addEventListener("input", function (e) {
    let valor = e.target.value.replace(/\D/g, "");

    if (valor.length > 11) valor = valor.substring(0, 11);

    if (valor.length > 6) {
      valor = `(${valor.substring(0, 2)}) ${valor.substring(2, 7)}-${valor.substring(7)}`;
    } else if (valor.length > 2) {
      valor = `(${valor.substring(0, 2)}) ${valor.substring(2)}`;
    } else if (valor.length > 0) {
      valor = `(${valor}`;
    }

    e.target.value = valor;
  });

  // Validação do telefone no envio do formulário
  form.addEventListener("submit", function (e) {
    const telefone = telefoneInput.value.replace(/\D/g, "");

    if (telefone.length !== 11) {
      alert("Telefone inserido incorretamente. Informe um número válido com DDD.");
      e.preventDefault(); // Impede o envio do formulário
    }
  });

  // Alertas de erro repassados pelo backend (via Thymeleaf no HTML)
  const erroTelefone = document.getElementById("erroTelefone");
  const erroTelefoneExistente = document.getElementById("erroTelefoneExistente");

  if (erroTelefone) {
    alert(erroTelefone.value);
  }

  if (erroTelefoneExistente) {
    alert(erroTelefoneExistente.value);
  }
});
});