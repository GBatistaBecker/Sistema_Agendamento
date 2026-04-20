const dataSelecionada = localStorage.getItem('dataSelecionada');
const diaSemanaSelecionado = localStorage.getItem('diaSemanaSelecionado');
const nomeCliente = localStorage.getItem('nomeCliente');
const telefoneCliente = localStorage.getItem('telefoneCliente');
const idServico = localStorage.getItem('idServico');

const container = document.getElementById('lista-horas');

// Todos os serviços agora têm duração de 30 minutos
const duracao = 30;

// Gera horários das 14:00 às 21:30
const horarios = [];
let hora = 14;
let minuto = 0;
while (hora < 22) {
  horarios.push(`${String(hora).padStart(2, '0')}:${String(minuto).padStart(2, '0')}`);
  minuto += 30;
  if (minuto === 60) {
    minuto = 0;
    hora++;
  }
}

function atualizarVisualHorario(horaDiv, disponivel) {
  const btn = horaDiv.querySelector('button');
  const span = horaDiv.querySelector('span');

  if (disponivel) {
    horaDiv.classList.remove('indisponivel');
    btn.disabled = false;
    btn.textContent = '+';
    span.textContent = 'Disponível';
  } else {
    horaDiv.classList.add('indisponivel');
    btn.disabled = true;
    btn.textContent = 'X';
    span.textContent = 'Indisponível';
  }
}

fetch(`/barbearia/horarios-ocupados?data=${dataSelecionada}&idServico=${idServico}`)
  .then(res => res.json())
  .then(horariosOcupados => {
    const ocupados = new Set(horariosOcupados);
    const divsPorHora = {};

    horarios.forEach((horaAtual) => {
      const disponivel = !ocupados.has(horaAtual);

      const horaDiv = document.createElement('div');
      horaDiv.classList.add('hora');
      horaDiv.innerHTML = `
        <strong>${horaAtual}</strong>
        <span></span>
        <button data-hora="${horaAtual}"></button>
      `;

      atualizarVisualHorario(horaDiv, disponivel);
      divsPorHora[horaAtual] = horaDiv;

      if (disponivel) {
        const btn = horaDiv.querySelector('button');
        btn.addEventListener('click', () => {
          const horaSelecionada = btn.dataset.hora;

          atualizarVisualHorario(horaDiv, false);

          fetch('/barbearia/agendar', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
              nomeCliente,
              telefoneCliente,
              idServico,
              dataAgendamento: dataSelecionada,
              horaAgendamento: horaSelecionada
            })
          })
            .then(response => {
              if (!response.ok) throw new Error("Erro no agendamento.");
              return response.text();
            })
            .then(() => {
              const partesData = dataSelecionada.split('-');
              const dataFormatada = `${partesData[2]}/${partesData[1]}/${partesData[0]}`;
              const mensagem = `
                ✅ Agendamento realizado com sucesso!<br>
                📅 Data: ${diaSemanaSelecionado}, ${dataFormatada}<br>
                🕒 Horário: ${horaSelecionada}
              `;

              document.getElementById("mensagemModal").innerHTML = mensagem;

              const modal = new bootstrap.Modal(document.getElementById('modalSucesso'));
              modal.show();

              document.getElementById("botaoConfirmar").onclick = () => {
                modal.hide();
                window.location.href = "/barbearia/servicos";
              };
            })
            .catch(erro => {
              alert("Erro ao agendar: " + erro.message);
            });
        });
      }

      container.appendChild(horaDiv);
    });
  })
  .catch(erro => {
    console.error("Erro ao carregar horários ocupados:", erro);
    container.innerHTML = "<p>Erro ao carregar horários disponíveis.</p>";
  });