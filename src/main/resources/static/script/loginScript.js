document.addEventListener("DOMContentLoaded", function () {
  const telefoneInput = document.getElementById("telefoneCliente");

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
});
  