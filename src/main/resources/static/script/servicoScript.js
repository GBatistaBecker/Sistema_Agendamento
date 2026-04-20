/* ===============================
   DASHBOARD BARBEARIA - SCRIPT UNIFICADO
   =============================== */

let servicoSelecionado = null;
let dataSelecionada = null;
let horaSelecionada = null;

const step1 = document.getElementById("step1");
const step2 = document.getElementById("step2");
const step3 = document.getElementById("step3");

const etapaData = document.getElementById("etapaData");
const etapaHorario = document.getElementById("etapaHorario");

const inputData = document.getElementById("inputData");
const listaHoras = document.getElementById("lista-horas");
const btnConfirmar = document.getElementById("btnConfirmarAgendamento");

/* ===============================
   BLOQUEAR DATAS PASSADAS E HOJE
   =============================== */

const hoje = new Date();
hoje.setDate(hoje.getDate() + 1);
inputData.min = hoje.toISOString().split("T")[0];

/* ===============================
   ETAPA 1 - SELEÇÃO DE SERVIÇO
   =============================== */

document.querySelectorAll(".servico-selecao-card").forEach(card => {
  card.addEventListener("click", () => {

    document.querySelectorAll(".servico-selecao-card")
      .forEach(c => c.classList.remove("selected"));

    card.classList.add("selected");

    servicoSelecionado = card.getAttribute("data-id-servico");

    step1.classList.remove("active");
    step1.classList.add("completed");

    step2.classList.add("active");

    etapaData.classList.add("show");
  });
});

/* ===============================
   ETAPA 2 - SELEÇÃO DE DATA
   =============================== */

inputData.addEventListener("change", () => {

  if (!servicoSelecionado) return;

  dataSelecionada = inputData.value;

  step2.classList.remove("active");
  step2.classList.add("completed");

  step3.classList.add("active");

  etapaHorario.classList.add("show");

  carregarHorarios();
});

/* ===============================
   CARREGAR HORÁRIOS
   =============================== */

function carregarHorarios() {

  listaHoras.innerHTML = "";
  horaSelecionada = null;
  btnConfirmar.disabled = true;

  fetch(`/barbearia/horarios-ocupados?data=${dataSelecionada}&idServico=${servicoSelecionado}`)
    .then(res => res.json())
    .then(horariosOcupados => {

      const ocupados = new Set(horariosOcupados);

      let hora = 14;
      let minuto = 0;

      while (hora < 22) {

        const horaFormatada = `${String(hora).padStart(2,'0')}:${String(minuto).padStart(2,'0')}`;
        const disponivel = !ocupados.has(horaFormatada);

        const div = document.createElement("div");
        div.classList.add("hora-card");
        div.innerHTML = `<strong>${horaFormatada}</strong>`;

        if (!disponivel) {
          div.classList.add("indisponivel");
        } else {
          div.addEventListener("click", () => {

            document.querySelectorAll(".hora-card")
              .forEach(h => h.classList.remove("selected"));

            div.classList.add("selected");
            horaSelecionada = horaFormatada;

            btnConfirmar.disabled = false;
          });
        }

        listaHoras.appendChild(div);

        minuto += 30;
        if (minuto === 60) {
          minuto = 0;
          hora++;
        }
      }
    })
    .catch(err => {
      console.error("Erro ao carregar horários:", err);
      listaHoras.innerHTML = "<p>Erro ao carregar horários.</p>";
    });
}

/* ===============================
   CONFIRMAR AGENDAMENTO
   =============================== */

btnConfirmar.addEventListener("click", () => {

  if (!servicoSelecionado || !dataSelecionada || !horaSelecionada) return;

  fetch("/barbearia/agendar", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded"
    },
    body: new URLSearchParams({
      idServico: servicoSelecionado,
      dataAgendamento: dataSelecionada,
      horaAgendamento: horaSelecionada
    })
  })
  .then(res => {
    if (!res.ok) throw new Error("Erro no agendamento");
    return res.text();
  })
  .then(() => {

    const partesData = dataSelecionada.split("-");
    const dataFormatada = `${partesData[2]}/${partesData[1]}/${partesData[0]}`;

    document.getElementById("mensagemModal").innerHTML = `
      ✅ Agendamento realizado com sucesso!<br>
      📅 Data: ${dataFormatada}<br>
      🕒 Horário: ${horaSelecionada}
    `;

    const modalSucesso = new bootstrap.Modal(document.getElementById("modalSucesso"));
    modalSucesso.show();

    resetarModal();

  })
  .catch(err => {
    alert("Erro ao agendar: " + err.message);
  });
});

/* ===============================
   RESETAR MODAL APÓS SUCESSO
   =============================== */

function resetarModal() {

  servicoSelecionado = null;
  dataSelecionada = null;
  horaSelecionada = null;

  btnConfirmar.disabled = true;

  document.querySelectorAll(".servico-selecao-card")
    .forEach(c => c.classList.remove("selected"));

  document.querySelectorAll(".hora-card")
    .forEach(h => h.classList.remove("selected"));

  etapaData.classList.remove("show");
  etapaHorario.classList.remove("show");

  step1.classList.remove("completed");
  step1.classList.add("active");

  step2.classList.remove("active", "completed");
  step3.classList.remove("active", "completed");

  inputData.value = "";
  listaHoras.innerHTML = "";
}

/* ===============================
   MODAL HISTÓRICO
   =============================== */

const modalHistorico = document.getElementById("modalHistorico");

modalHistorico.addEventListener("show.bs.modal", () => {

  const lista = document.getElementById("listaAgendamentos");
  lista.innerHTML = "<li>Carregando...</li>";

  fetch("/barbearia/agendamentos-do-usuario", {
    credentials: "include"
  })
  .then(res => {
    if (res.status === 401) {
      window.location.href = "/barbearia/login";
      return;
    }
    if (!res.ok) throw new Error("Erro ao buscar histórico");
    return res.text();
  })
  .then(html => {
    lista.innerHTML = html || "<li>Nenhum agendamento encontrado.</li>";
  })
  .catch(err => {
    lista.innerHTML = "<li>Erro ao carregar histórico.</li>";
    console.error(err);
  });
});

/* ===============================
   EXCLUSÃO DE AGENDAMENTO
   =============================== */

let idParaExcluir = null;
const modalExcluir = new bootstrap.Modal(document.getElementById('modalConfirmarExclusao'));
const confirmarBtn = document.getElementById('btnConfirmarExclusao');

function confirmarExclusao(id) {
  idParaExcluir = id;
  modalExcluir.show();
}

confirmarBtn.addEventListener('click', async () => {

  if (!idParaExcluir) return;

  try {

    const res = await fetch('/barbearia/excluir-agendamento?idAgendamento=' + idParaExcluir, {
      method: 'POST'
    });

    if (!res.ok) throw new Error();

    const item = document.querySelector(`li[data-id="${idParaExcluir}"]`);
    if (item) item.remove();

    if (item) {

      item.classList.add("removing");

      setTimeout(() => {
        item.remove();
      }, 350);

    }

    const toast = new bootstrap.Toast(
      document.getElementById('toastExclusao')
    );

    toast.show();

  } catch {

    alert("Erro ao excluir agendamento.");

  } finally {

    idParaExcluir = null;
    modalExcluir.hide();

  }

});