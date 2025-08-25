const modalConfirmacao = new bootstrap.Modal(document.getElementById('modalConfirmacao'));

function mostrarConfirmacao(callbackConfirm, callbackCancelar, mensagem) {
  const textoModal = document.getElementById('modalConfirmacaoTexto');
  textoModal.textContent = mensagem || "Você confirma esta ação?";

  modalConfirmacao.show();

  const btnOk = document.getElementById('btnConfirmar');
  const btnCancel = document.getElementById('btnCancelar');

  btnOk.onclick = null;
  btnCancel.onclick = null;

  btnOk.onclick = () => {
    modalConfirmacao.hide();
    if (callbackConfirm) callbackConfirm();
  };

  btnCancel.onclick = () => {
    modalConfirmacao.hide();
    if (callbackCancelar) callbackCancelar();
  };
}

function confirmar(id) {
  mostrarConfirmacao(
    () => {
      const item = document.getElementById("agendamento-" + id);
      item.classList.remove("cancelado");
      item.classList.add("confirmado");
      desativarBotoes(item);

      const modal = new bootstrap.Modal(document.getElementById('modalConfirmado'));
      modal.show();

      setTimeout(() => modal.hide(), 2500);
    },
    () => {
      console.log("Confirmação cancelada.");
    },
    "Deseja confirmar este agendamento?"
  );
}

function cancelar(id) {
  mostrarConfirmacao(
    () => {
      const item = document.getElementById("agendamento-" + id);
      item.classList.remove("confirmado");
      item.classList.add("cancelado");
      desativarBotoes(item);

      const modal = new bootstrap.Modal(document.getElementById('modalCancelado'));
      modal.show();

      setTimeout(() => modal.hide(), 2500);
    },
    () => {
      console.log("Cancelamento abortado.");
    },
    "Deseja cancelar este agendamento?"
  );
}

function desativarBotoes(item) {
  const botoes = item.querySelectorAll("button");
  botoes.forEach(botao => botao.disabled = true);
}