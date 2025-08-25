// Variável global para armazenar o ID do agendamento a ser excluído
let idParaExcluir = null;

// Modal de confirmação e botão OK
const modal = new bootstrap.Modal(document.getElementById('modalConfirmarExclusao'));
const confirmarBtn = document.getElementById('btnConfirmarExclusao');

// Função chamada pelo botão "🗑️" do HTML injetado
function confirmarExclusao(id) {
  idParaExcluir = id;
  modal.show();
}

// Evento de clique para confirmar a exclusão
confirmarBtn.addEventListener('click', async () => {
  if (idParaExcluir === null) return;

  try {
    const res = await fetch('/barbearia/excluir-agendamento?idAgendamento=' + idParaExcluir, {
      method: 'POST'
    });

    if (!res.ok) throw new Error('Erro ao excluir agendamento');

    // Remove o <li> da interface
    const itemRemover = document.querySelector(`li[data-id="${idParaExcluir}"]`);
    if (itemRemover) itemRemover.remove();

  } catch (e) {
    alert('Falha ao excluir agendamento.');
    console.error(e);
  } finally {
    idParaExcluir = null;
    modal.hide();
  }
});

// Abre a sidebar e carrega os agendamentos HTML do back-end
document.querySelector('.menu-btn').addEventListener('click', async () => {
  const sidebar = document.getElementById('sidebar');
  sidebar.classList.add('open');

  const lista = document.getElementById('listaAgendamentos');
  lista.innerHTML = '<li>Carregando...</li>';

  try {
    const res = await fetch('/barbearia/agendamentos-do-usuario', {
      credentials: 'include'
    });

    if (res.status === 401) {
      alert('Sessão expirada ou não autenticado. Faça login novamente.');
      window.location.href = '/barbearia/login';
      return;
    }

    if (!res.ok) throw new Error('Erro ao buscar agendamentos');

    const html = await res.text();

    if (!html || html.trim() === '') {
      lista.innerHTML = '<li>Nenhum agendamento encontrado.</li>';
    } else {
      lista.innerHTML = html;
    }

  } catch (e) {
    lista.innerHTML = '<li>Erro ao carregar agendamentos.</li>';
    console.error(e);
  }
});

// Fecha a sidebar
document.getElementById('closeSidebar').addEventListener('click', () => {
  document.getElementById('sidebar').classList.remove('open');
});

// Botões "+" para selecionar serviço e redirecionar
document.querySelectorAll('.add-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    const idServico = btn.getAttribute('data-id-servico');
    localStorage.setItem('idServico', idServico);
    window.location.href = '/barbearia/agendamentos';
  });
});