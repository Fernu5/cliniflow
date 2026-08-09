<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.List, model.Consulta" %>
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
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CliniFlow - Dashboard</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body class="home-body">

<div class="dashboard-layout">
    
    <!-- BARRA LATERAL -->
    <aside class="sidebar">
        <div class="sidebar-logo">Clini<span>Flow</span></div>
        <ul class="nav-menu">
            <a href="#" class="nav-item active"><i class="fa-solid fa-house"></i> Início</a>
            <a href="#" class="nav-item"><i class="fa-solid fa-notes-medical"></i> Consultas</a>
            <a href="#" class="nav-item"><i class="fa-solid fa-user"></i> Perfil</a>
            <a href="#" class="nav-item"><i class="fa-solid fa-circle-question"></i> Ajuda</a>
        </ul>
        <a href="index.html" class="nav-item" style="margin-bottom: 24px; color: #E53E3E;"><i class="fa-solid fa-arrow-right-from-bracket"></i> Sair</a>
    </aside>

    <!-- ÁREA PRINCIPAL -->
    <main class="main-content">
        
        <header class="topbar">
            <div class="topbar-user">
                <p>Bem-vindo,</p>
                <h3><%= nomeUsuario %></h3>
            </div>
            <div class="next-appointment">
                <h4>Próxima consulta</h4>
                <h2><%= request.getAttribute("proxMedico") %></h2>
                <p><%= request.getAttribute("proxData") %> - <%= request.getAttribute("proxEspecialidade") %></p>
            </div>
            <div style="font-size: 24px; cursor: pointer;"><i class="fa-regular fa-bell"></i></div>
        </header>

        <div class="stats-grid">
            <div class="stat-card">
                <h2><%= request.getAttribute("consultasDia") %></h2>
                <p>Consultas no Dia</p>
            </div>
            <div class="stat-card">
                <h2><%= request.getAttribute("consultasMes") %></h2>
                <p>Consultas no Mês</p>
            </div>
            <div class="stat-card">
                <h2><%= request.getAttribute("totalConsultas") %></h2>
                <p>Total Agendadas</p>
            </div>
        </div>

        <div class="content-area">
            
            <div class="content-card">
                
                <!-- Botão Agendar e Título lado a lado -->
                <div class="header-consultas">
                    <h3 class="section-title" style="margin-bottom: 0;">Minhas Consultas</h3>
                    <button class="btn-agendar" onclick="window.location.href='agendamento'">
                        <i class="fa-solid fa-plus"></i> Agendar Consulta
                    </button>
                </div>

                <!-- Loop de Consultas -->
                <%
                    List<Consulta> lista = (List<Consulta>) request.getAttribute("listaConsultas");
                    if (lista != null && !lista.isEmpty()) {
                        for (Consulta c : lista) {
                            String classeBadge = "badge-pendente"; 
                            if ("Cancelada".equals(c.getStatus())) classeBadge = "badge-cancelada";
                            if ("Concluída".equals(c.getStatus()) || "Concluida".equals(c.getStatus())) classeBadge = "badge-concluida";
                %>
                        <div class="card-consulta">
                            <h4 class="medico-nome"><%= c.getMedico() %></h4>
                            <p class="especialidade"><%= c.getEspecialidade() != null ? c.getEspecialidade() : "Clínico Geral" %></p>
                            <div class="rodape-consulta">
                                <span class="data"><%= c.getDataHora() %></span>
                                <span class="<%= classeBadge %>"><%= c.getStatus() %></span>
                            </div>
                        </div>
                <%
                        } 
                    } else {
                %>
                    <p style="color: #A0AEC0; font-size: 14px; margin-top: 16px;">Você não possui consultas registradas.</p>
                <%
                    } 
                %>
            </div>

            <div class="content-card">
                <h3 class="section-title">Calendário</h3>
                <p style="color: #A0AEC0; text-align: center; margin-top: 40px;">(Módulo de calendário será inserido aqui)</p>
            </div>
            
        </div>
    </main>
</div>

</body>
</html>