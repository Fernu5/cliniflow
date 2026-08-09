<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.List, model.Especialidade" %>
<%
    if (session.getAttribute("usuarioLogadoId") == null) {
        response.sendRedirect("index.html");
        return;
    }
    String nomeUsuario = (String) session.getAttribute("usuarioLogadoNome");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>CliniFlow - Novo Agendamento</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        .form-header { display: flex; align-items: center; margin-bottom: 24px; color: #2D3748; }
        .btn-voltar { background: none; border: none; color: #12A388; cursor: pointer; font-size: 20px; margin-right: 12px; text-decoration: none; }
        
        .select-group { background-color: #FFFFFF; border: 1px solid #E2E8F0; border-radius: 8px; padding: 12px 16px; margin-bottom: 16px; display: flex; flex-direction: column; }
        .select-group label { font-size: 12px; color: #A0AEC0; margin-bottom: 4px; }
        .select-group select { border: none; font-size: 16px; color: #2D3748; background: transparent; outline: none; cursor: pointer; width: 100%; font-weight: bold; }
        
        .agendamento-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 40px; margin-top: 32px; }
        
        /* Estilos do Calendário Dinâmico */
        .calendar-container { text-align: center; color: #4A5568; }
        .calendar-grid { display: grid; grid-template-columns: repeat(7, 1fr); gap: 8px; font-size: 14px; margin-top: 16px; }
        .day-header { font-weight: bold; color: #A0AEC0; margin-bottom: 8px; }
        .day { padding: 8px; border-radius: 50%; cursor: pointer; background-color: #F7FAFC; border: 1px solid transparent; }
        .day:hover:not(.disabled) { background-color: #E6FFFA; color: #12A388; }
        .day.selected { background-color: #FFFFFF; border: 2px solid #12A388; color: #12A388; font-weight: bold; }
        .day.disabled { color: #CBD5E0; cursor: not-allowed; background-color: transparent; }
        .day.has-agenda { background-color: #C6F6D5; color: #22543D; font-weight: bold; } /* Dia com agenda disponível */
        
        /* Estilos dos Horários */
        .slots-container { text-align: center; }
        .slots-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; margin-bottom: 32px; margin-top: 16px; }
        .slot-btn { padding: 12px; border-radius: 8px; font-weight: bold; font-size: 14px; cursor: pointer; text-align: center; border: 2px solid #A7F3D0; background-color: #ECFDF5; color: #047857; transition: all 0.2s; }
        .slot-btn:hover { background-color: #A7F3D0; }
        .slot-btn.selected { background-color: #047857; color: white; border-color: #047857; }
        
        .btn-confirmar { background-color: #12A388; color: white; border: none; padding: 16px; border-radius: 8px; font-size: 16px; font-weight: bold; cursor: pointer; width: 100%; transition: background-color 0.2s; }
        .btn-confirmar:hover { background-color: #0e826c; }
        .btn-confirmar:disabled { background-color: #A0AEC0; cursor: not-allowed; }
    </style>
</head>
<body class="home-body">

<div class="dashboard-layout">
    
    <aside class="sidebar">
        <div class="sidebar-logo">Clini<span>Flow</span></div>
        <ul class="nav-menu">
            <a href="home" class="nav-item"><i class="fa-solid fa-house"></i> Início</a>
            <a href="#" class="nav-item active"><i class="fa-solid fa-notes-medical"></i> Consultas</a>
            <a href="#" class="nav-item"><i class="fa-solid fa-user"></i> Perfil</a>
            <a href="#" class="nav-item"><i class="fa-solid fa-circle-question"></i> Ajuda</a>
        </ul>
        <a href="index.html" class="nav-item" style="margin-bottom: 24px; color: #E53E3E;"><i class="fa-solid fa-arrow-right-from-bracket"></i> Sair</a>
    </aside>

    <main class="main-content">
        <!-- CABEÇALHO VERDE (Fundo limpo e transparente para o texto) -->
        <header class="topbar" style="background-color: #12A388; border: none;">
            <div class="topbar-user" style="background: transparent;">
                <p style="color: #E6FFFA; margin: 0;">Novo Agendamento,</p>
                <h3 style="color: white; margin: 0;"><%= nomeUsuario %></h3>
            </div>
        </header>

        <div class="content-area" style="display: block; padding: 40px;">
            <div class="content-card">
                
                <!-- TÍTULO LIMPO, SEM SETA E SEM FUNDO CINZA -->
                <div class="form-header" style="margin-bottom: 24px; color: #2D3748; background: transparent;">
                    <h3 style="font-size: 24px; margin: 0;">Agendar Consulta</h3>
                </div>
                
                <form action="ConfirmarAgendamentoServlet" method="POST" id="formAgendamento">
                    <input type="hidden" name="data_escolhida" id="data_escolhida">
                    <input type="hidden" name="horario_escolhido" id="horario_escolhido">

                    <div class="select-group">
                        <label for="especialidade">Especialidade</label>
                        <select id="especialidade" name="especialidade" onchange="buscarMedicos()" required>
                            <option value="">Selecione uma especialidade...</option>
                            <%
                                List<Especialidade> lista = (List<Especialidade>) request.getAttribute("especialidades");
                                if (lista != null) {
                                    for (Especialidade esp : lista) {
                            %>
                                        <option value="<%= esp.getIdEspecialidade() %>"><%= esp.getTipoEspecialidade() %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>

                    <div class="select-group">
                        <label for="medico">Médico</label>
                        <!-- Agora ele chama a busca de datas ao ser selecionado -->
                        <select id="medico" name="medico" onchange="buscarDatas()" required disabled>
                            <option value="">Selecione primeiro a especialidade...</option>
                        </select>
                    </div>

                    <div class="agendamento-grid">
                        
                        <!-- Lado Esquerdo: Calendário que será gerado pelo JS -->
                        <div class="calendar-container">
                            <h4 id="mes-atual">Carregando calendário...</h4>
                            <div class="calendar-grid" id="calendario-dias">
                                <!-- Os dias entram aqui via JavaScript -->
                            </div>
                        </div>

                        <!-- Lado Direito: Horários que virão do Banco -->
                        <div class="slots-container">
                            <h4 id="titulo-horarios">Selecione um dia</h4>
                            <div class="slots-grid" id="grade-horarios">
                                <!-- Os horários entram aqui via JavaScript -->
                            </div>
                            
                            <button type="submit" class="btn-confirmar" id="btn-submit" disabled>Agendar</button>
                        </div>
                    </div>

                </form>
            </div>
        </div>
    </main>
</div>

<script>
    // 1. Busca os Médicos (Essa já estava funcionando)
    function buscarMedicos() {
        var especialidadeId = document.getElementById("especialidade").value;
        var selectMedico = document.getElementById("medico");
        
        limparTudo(); // Limpa as telas de baixo se trocar a especialidade

        if (especialidadeId === "") {
            selectMedico.innerHTML = '<option value="">Selecione primeiro a especialidade...</option>';
            selectMedico.disabled = true;
            return;
        }

        selectMedico.innerHTML = '<option value="">Buscando médicos...</option>';
        selectMedico.disabled = true;

        fetch('carregarMedicos?id_especialidade=' + especialidadeId)
            .then(response => response.text())
            .then(html => {
                selectMedico.innerHTML = html;
                selectMedico.disabled = false; 
            });
    }

    // 2. Busca as Datas Disponíveis na tabela 'agenda_medico'
    function buscarDatas() {
        var medicoId = document.getElementById("medico").value;
        if(medicoId === "") {
            limparTudo();
            return;
        }

        document.getElementById("mes-atual").innerText = "Buscando agenda no banco...";
        
        fetch('carregarAgenda?acao=datas&id_medico=' + medicoId)
            .then(response => response.json())
            .then(datasDoBanco => {
                // Desenha um calendário simples do mês de Maio de 2026 e pinta as datas que vieram do banco
                desenharCalendario(5, 2026, datasDoBanco);
            });
    }

    // 3. Busca os horários de um dia específico
    function selecionarDia(dia, mes, ano) {
        var dataFormatada = ano + "-" + mes.toString().padStart(2, '0') + "-" + dia.toString().padStart(2, '0');
        document.getElementById("data_escolhida").value = dataFormatada;
        
        // Remove a classe selected de todos e bota no clicado
        document.querySelectorAll('.day').forEach(d => d.classList.remove('selected'));
        event.target.classList.add('selected');

        var medicoId = document.getElementById("medico").value;
        document.getElementById("titulo-horarios").innerText = "Buscando horários...";
        document.getElementById("grade-horarios").innerHTML = "";
        document.getElementById("btn-submit").disabled = true;

        // Vai no banco buscar os intervalos (08:00, 08:30...)
        fetch('carregarAgenda?acao=horarios&id_medico=' + medicoId + '&data=' + dataFormatada)
            .then(response => response.text())
            .then(html => {
                document.getElementById("titulo-horarios").innerText = "Horários — Dia " + dia;
                document.getElementById("grade-horarios").innerHTML = html;
            });
    }

    // Função de apoio para selecionar o horário e liberar o botão Agendar
    function selecionarHorario(elemento, horario) {
        document.getElementById("horario_escolhido").value = horario;
        document.querySelectorAll('.slot-btn').forEach(b => b.classList.remove('selected'));
        elemento.classList.add('selected');
        document.getElementById("btn-submit").disabled = false;
    }

    // Função que desenha o grid do calendário
    function desenharCalendario(mes, ano, datasDisponiveis) {
        document.getElementById("mes-atual").innerText = "Maio 2026";
        var grid = document.getElementById("calendario-dias");
        
        var cabecalho = '<div class="day-header">D</div><div class="day-header">S</div><div class="day-header">T</div><div class="day-header">Q</div><div class="day-header">Q</div><div class="day-header">S</div><div class="day-header">S</div>';
        var diasHtml = "";
        
        // Simplificando o preenchimento para o mês de Maio de 2026 (Começa na sexta-feira)
        for(let i=0; i<5; i++) diasHtml += '<div class="day disabled"></div>'; // Espaços vazios iniciais
        
        for(let dia = 1; dia <= 31; dia++) {
            let dataString = ano + "-" + mes.toString().padStart(2, '0') + "-" + dia.toString().padStart(2, '0');
            
            // Verifica se esse dia existe no array que veio do Banco de Dados
            if(datasDisponiveis.includes(dataString)) {
                diasHtml += '<div class="day has-agenda" onclick="selecionarDia(' + dia + ', ' + mes + ', ' + ano + ')">' + dia + '</div>';
            } else {
                diasHtml += '<div class="day disabled">' + dia + '</div>';
            }
        }
        
        grid.innerHTML = cabecalho + diasHtml;
    }

    function limparTudo() {
        document.getElementById("calendario-dias").innerHTML = "";
        document.getElementById("mes-atual").innerText = "Selecione o médico";
        document.getElementById("grade-horarios").innerHTML = "";
        document.getElementById("titulo-horarios").innerText = "Selecione um dia";
        document.getElementById("btn-submit").disabled = true;
    }
</script>

</body>
</html>