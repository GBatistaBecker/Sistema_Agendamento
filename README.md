# 💈 Sistema de Agendamento de Barbearia

Este projeto é um sistema web de **agendamento de horários em uma barbearia**, permitindo que clientes escolham serviços, visualizem horários disponíveis e realizem reservas de forma prática. Desenvolvido para a matéria de **Projeto Aplicado** da faculdade **UNISenai** de Tuabarão, Santa Catarina.

---

## 📌 Funcionalidades

- Cadastro de clientes (com autenticação por e-mail e senha).  
- Login e gerenciamento de sessão.  
- Listagem de serviços oferecidos pela barbearia.  
- Agendamento de horários (com verificação de horários ocupados).  
- Exclusão e consulta de agendamentos.  
- Painel administrativo para gerenciar clientes e serviços.  
- Interface amigável e responsiva.  

---

## 🛠️ Tecnologias Utilizadas

- **Java 24+**  
- **Spring Boot** (MVC, Data JPA)  
- **Thymeleaf** (renderização das páginas HTML)  
- **Bootstrap** (ajustes de design responsivo)  
- **Banco de Dados**: PostgreSQL (pode ser adaptado para MySQL/H2)  

---

## 📂 Estrutura do Projeto
```
src/
├── main/
│ ├── java/br/com/barbearia/agendamento
│ │ ├── controller/ → Lógica de controle (endpoints e rotas)
│ │ ├── model/ → Entidades JPA
│ │ ├── repository/ → Interfaces para persistência
│ │ ├── service/ → Regras de negócio
│ │ └── util/ → Lista para ordenamento
│ └── resources/
│ ├── static/ → CSS, JS, imagens
│ ├── templates/ → Páginas HTML (Thymeleaf)
│ └── application.properties → Configurações do projeto
```
