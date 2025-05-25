// Recupera os dados da data e cliente/serviço selecionados
const dataSelecionada = localStorage.getItem('dataSelecionada');
const diaSemanaSelecionado = localStorage.getItem('diaSemanaSelecionado');
const nomeCliente = localStorage.getItem('nomeCliente');
const telefoneCliente = localStorage.getItem('telefoneCliente');
const idServico = localStorage.getItem('idServico');

const container = document.getElementById('lista-horas');

for (let hora = 7; hora <= 19; hora++) {
  const horaFormatada = `${hora.toString().padStart(2, '0')}:00`;
  const vagas = Math.floor(Math.random() * 6); // de 0 a 5

  const horaDiv = document.createElement('div');
  horaDiv.classList.add('hora');
  if (vagas === 0) {
    horaDiv.classList.add('indisponivel');
  }

  horaDiv.innerHTML = `
    ${horaFormatada}
    <span>${vagas === 0 ? 'Nenhuma vaga' : `${vagas} vaga${vagas > 1 ? 's' : ''}`}</span>
    <button ${vagas === 0 ? 'disabled' : ''}>${vagas === 0 ? 'X' : '+'}</button>
  `;

  if (vagas > 0) {
    const btn = horaDiv.querySelector('button');
    btn.addEventListener('click', () => {
      // Envia o POST pro controller
      fetch('/barbearia/agendar', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
          nomeCliente: nomeCliente,
          telefoneCliente: telefoneCliente,
          idServico: idServico,
          dataAgendamento: dataSelecionada,
          horaAgendamento: horaFormatada
        })
      })
      .then(response => response.text())
      .then(mensagem => {
        alert(`✅ ${mensagem}
📅 Dia: ${diaSemanaSelecionado}, ${dataSelecionada}
🕒 Horário: ${horaFormatada}`);
        window.location.href = "/barbearia/servicos";
      });
    });
  }

  container.appendChild(horaDiv);
}