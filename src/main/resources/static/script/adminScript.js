function confirmar(id) {
  const confirmacao = confirm("Deseja realmente confirmar este agendamento?");
  if (confirmacao) {
    const item = document.getElementById("agendamento-" + id);
    item.classList.remove("cancelado");
    item.classList.add("confirmado");
    alert("Agendamento confirmado com sucesso!");
    desativarBotoes(item);
  }
}

function cancelar(id) {
  const confirmacao = confirm("Tem certeza que deseja cancelar este agendamento?");
  if (confirmacao) {
    const item = document.getElementById("agendamento-" + id);
    item.classList.remove("confirmado");
    item.classList.add("cancelado");
    alert("Agendamento cancelado.");
    desativarBotoes(item);
  }
}

function desativarBotoes(item) {
  const botoes = item.querySelectorAll("button");
  botoes.forEach(botao => botao.disabled = true);
}