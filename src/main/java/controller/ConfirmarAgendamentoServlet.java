package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.ConexaoDB;

@WebServlet("/ConfirmarAgendamentoServlet")
public class ConfirmarAgendamentoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		if (session.getAttribute("usuarioLogadoId") == null) {
			response.sendRedirect("index.html");
			return;
		}
		
		int idUsuarioLogado = (int) session.getAttribute("usuarioLogadoId");
		String medicoPerfilStr = request.getParameter("medico");
		String dataEscolhida = request.getParameter("data_escolhida");
		String horarioEscolhido = request.getParameter("horario_escolhido");
		
		if (medicoPerfilStr == null || dataEscolhida == null || horarioEscolhido == null) {
			response.sendRedirect("agendamento?erro=dados_incompletos");
			return;
		}
		
		try (Connection con = ConexaoDB.conectar()) {
			
			// 1. Descobrir o id_perfil do paciente logado
			String sqlPerfil = "SELECT id_perfil FROM perfis WHERE usuario = ? AND tipo_perfil = 'Paciente'";
			PreparedStatement stmtPerfil = con.prepareStatement(sqlPerfil);
			stmtPerfil.setInt(1, idUsuarioLogado);
			ResultSet rsPerfil = stmtPerfil.executeQuery();
			
			int idPerfilPaciente = 0;
			if (rsPerfil.next()) {
				idPerfilPaciente = rsPerfil.getInt("id_perfil");
			} else {
				response.sendRedirect("agendamento?erro=paciente_nao_encontrado");
				return;
			}
			
			int idMedicoPerfil = Integer.parseInt(medicoPerfilStr);
			String inicioStr = dataEscolhida + " " + horarioEscolhido + ":00";
			
			// Calcula o horário fim (30 minutos depois, padrão do sistema)
			LocalTime horaInicioTime = LocalTime.parse(horarioEscolhido);
			LocalTime horaFimTime = horaInicioTime.plusMinutes(30);
			String fimStr = dataEscolhida + " " + horaFimTime.toString() + ":00";
			
			// 2. Verificar se o horário já está ocupado (Prevenção de Conflitos)
			String sqlConfere = "SELECT id_consulta FROM consultas WHERE medico = ? AND data_hora_consulta_inicio = ? AND status_consulta = 'Agendada'";
			PreparedStatement stmtConfere = con.prepareStatement(sqlConfere);
			stmtConfere.setInt(1, idMedicoPerfil);
			stmtConfere.setString(2, inicioStr);
			ResultSet rsConfere = stmtConfere.executeQuery();
			
			if (rsConfere.next()) {
				// Horário Ocupado: Insere na Lista de Espera que você desenhou no banco!
				int idConsultaExistente = rsConfere.getInt("id_consulta");
				
				// Descobre a próxima posição na lista de espera
				String sqlPosicao = "SELECT COUNT(*) + 1 AS proxima_posicao FROM listas_espera WHERE consulta = ?";
				PreparedStatement stmtPos = con.prepareStatement(sqlPosicao);
				stmtPos.setInt(1, idConsultaExistente);
				ResultSet rsPos = stmtPos.executeQuery();
				int posicao = 1;
				if (rsPos.next()) {
					posicao = rsPos.getInt("proxima_posicao");
				}
				
				String sqlInsereEspera = "INSERT INTO listas_espera (consulta, paciente, posicao_lista_espera, status_lista_espera) VALUES (?, ?, ?, 'Ativa')";
				PreparedStatement stmtInsereEsp = con.prepareStatement(sqlInsereEspera);
				stmtInsereEsp.setInt(1, idConsultaExistente);
				stmtInsereEsp.setInt(2, idPerfilPaciente);
				stmtInsereEsp.setInt(3, posicao);
				stmtInsereEsp.executeUpdate();
				
				response.sendRedirect("home?sucesso=lista_espera");
			} else {
				// Horário Livre: Agenda a consulta normalmente
				String sqlInsere = "INSERT INTO consultas (paciente, medico, status_consulta, data_hora_consulta_inicio, data_hora_consulta_fim) VALUES (?, ?, 'Agendada', ?, ?)";
				PreparedStatement stmtInsere = con.prepareStatement(sqlInsere);
				stmtInsere.setInt(1, idPerfilPaciente);
				stmtInsere.setInt(2, idMedicoPerfil);
				stmtInsereEspParams(stmtInsere, inicioStr, fimStr); // método auxiliar abaixo
				stmtInsere.executeUpdate();
				
				response.sendRedirect("home?sucesso=agendado");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("agendamento?erro=sistema");
		}
	}
	
	// Pequeno helper para setar os parâmetros de data string
	private void stmtInsereEspParams(PreparedStatement stmt, String inicio, String fim) throws Exception {
		stmt.setString(3, inicio);
		stmt.setString(4, fim);
	}
}