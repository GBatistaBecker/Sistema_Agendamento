// Abre a sidebar e busca os agendamentos
document.querySelector('.menu-btn').addEventListener('click', async () => {
  const sidebar = document.getElementById('sidebar');
  sidebar.classList.add('open');

  const lista = document.getElementById('listaAgendamentos');
  lista.innerHTML = '<li>Carregando...</li>';

  try {
    const res = await fetch('/barbearia/agendamentos-do-usuario', {
      credentials: 'include' // Garante que o cookie de sessão seja enviado
    });

    if (res.status === 401) {
      alert('Sessão expirada ou não autenticado. Faça login novamente.');
      window.location.href = '/barbearia/login';
      return;
    }

    if (!res.ok) throw new Error('Erro ao buscar agendamentos');

    const agendamentos = await res.json();

    if (agendamentos.length === 0) {
      lista.innerHTML = '<li>Nenhum agendamento encontrado.</li>';
      return;
    }

    lista.innerHTML = '';
   agendamentos.forEach(a => {
  const item = document.createElement('li');

  // Formata a data sem usar Date (evita problemas de fuso)
  const [ano, mes, dia] = a.dataAgendamento.split('-');
  const dataFormatada = `${dia}/${mes}/${ano}`;

  const horaFormatada = a.horaAgendamento;

  item.textContent = `${dataFormatada} às ${horaFormatada} - ${a.servico.nomeCorte}`;

  // Botão de exclusão
  const btnExcluir = document.createElement('button');
  btnExcluir.textContent = '🗑️';
  btnExcluir.classList.add('btn-excluir');
  btnExcluir.addEventListener('click', async () => {
    if (confirm("Deseja realmente excluir este agendamento?")) {
      try {
        const res = await fetch('/barbearia/excluir-agendamento?idAgendamento=' + a.idAgendamento, {
          method: 'POST'
        });

        if (!res.ok) throw new Error('Erro ao excluir agendamento');

        item.remove();
      } catch (e) {
        alert('Falha ao excluir agendamento.');
        console.error(e);
      }
    }
  });

  item.appendChild(btnExcluir);
  lista.appendChild(item);
});
  } catch (e) {
    lista.innerHTML = '<li>Erro ao carregar agendamentos.</li>';
    console.error(e);
  }
});

// Fecha a sidebar
document.getElementById('closeSidebar').addEventListener('click', () => {
  document.getElementById('sidebar').classList.remove('open');
});

// Redireciona ao clicar no botão "+"
document.querySelectorAll('.add-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    const idServico = btn.getAttribute('data-id-servico');
    localStorage.setItem('idServico', idServico);
    window.location.href = '/barbearia/agendamentos';
  });
});