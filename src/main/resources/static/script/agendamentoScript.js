const diasSemana = ['Domingo', 'Segunda-feira', 'Terça-feira', 'Quarta-feira', 'Quinta-feira', 'Sexta-feira', 'Sábado'];
const container = document.getElementById('lista-dias');

// Recupera o idServico salvo anteriormente no localStorage
const idServico = localStorage.getItem('idServico');

for (let i = 0; i < 7; i++) {
  const hoje = new Date();
  hoje.setDate(hoje.getDate() + i);

  const dia = String(hoje.getDate()).padStart(2, '0');
  const mes = String(hoje.getMonth() + 1).padStart(2, '0');
  const ano = hoje.getFullYear();
  const data = `${ano}-${mes}-${dia}`;  // Formato compatível com LocalDate.parse no Java

  const diaSemana = diasSemana[hoje.getDay()];

  const horarios = i === 0 ? 0 : Math.floor(Math.random() * 6 + 2);
  const textoHorario = horarios === 0 ? 'Nenhum horário' : `${horarios} horário${horarios > 1 ? 's' : ''}`;

  const diaDiv = document.createElement('div');
  diaDiv.classList.add('dia');
  if (horarios === 0) {
    diaDiv.classList.add('indisponivel');
  }

  diaDiv.innerHTML = `
    ${dia}/${mes}
    <span>${textoHorario}</span>
    ${diaSemana}
    <button ${horarios === 0 ? 'disabled' : ''} onclick="selecionarDia('${data}', '${diaSemana}')">${horarios === 0 ? 'X' : '+'}</button>
  `;

  container.appendChild(diaDiv);
}

function selecionarDia(data, diaSemana) {
  // Salva os dados selecionados no localStorage
  localStorage.setItem('dataSelecionada', data);
  localStorage.setItem('diaSemanaSelecionado', diaSemana);
  
  // Redireciona para a tela de horários
  window.location.href = '/barbearia/horarios';
}