package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.ConexaoDB;

@WebServlet("/carregarAgenda")
public class CarregarAgendaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String acao = request.getParameter("acao");
		String idMedico = request.getParameter("id_medico");
		
		try (Connection con = ConexaoDB.conectar()) {
			
			// AÇÃO 1: Buscar os DIAS que o médico tem agenda cadastrada
			if ("datas".equals(acao)) {
				response.setContentType("application/json;charset=UTF-8");
				PrintWriter out = response.getWriter();
				
				String sql = "SELECT data_agenda FROM agenda_medico WHERE medico = ?";
				PreparedStatement stmt = con.prepareStatement(sql);
				stmt.setInt(1, Integer.parseInt(idMedico));
				ResultSet rs = stmt.executeQuery();
				
				List<String> datas = new ArrayList<>();
				while (rs.next()) {
					datas.add("\"" + rs.getString("data_agenda") + "\"");
				}
				
				// Devolve um Array JSON (ex: ["2026-05-15"]) para o JavaScript ler
				out.print("[" + String.join(",", datas) + "]");
			}
			
			// AÇÃO 2: Buscar os HORÁRIOS do dia selecionado (Simplificado para gerar botões a cada 30min)
			else if ("horarios".equals(acao)) {
				response.setContentType("text/html;charset=UTF-8");
				PrintWriter out = response.getWriter();
				String data = request.getParameter("data");
				
				String sql = "SELECT hora_inicio, hora_fim FROM agenda_medico WHERE medico = ? AND data_agenda = ?";
				PreparedStatement stmt = con.prepareStatement(sql);
				stmt.setInt(1, Integer.parseInt(idMedico));
				stmt.setString(2, data);
				ResultSet rs = stmt.executeQuery();
				
				if (rs.next()) {
					// Pega os horários do banco (ex: 08:00:00 e 12:00:00)
					String inicioStr = rs.getString("hora_inicio").substring(0, 5); 
					String fimStr = rs.getString("hora_fim").substring(0, 5);
					
					int horaAtual = Integer.parseInt(inicioStr.split(":")[0]);
					int horaFim = Integer.parseInt(fimStr.split(":")[0]);
					
					// Gera os botões HTML de 30 em 30 minutos (Isso substitui o Mock!)
					while (horaAtual < horaFim) {
						String h1 = String.format("%02d:00", horaAtual);
						String h2 = String.format("%02d:30", horaAtual);
						
						out.print("<div class='slot-btn' onclick=\"selecionarHorario(this, '" + h1 + "')\">" + h1 + "</div>");
						out.print("<div class='slot-btn' onclick=\"selecionarHorario(this, '" + h2 + "')\">" + h2 + "</div>");
						horaAtual++;
					}
				} else {
					out.print("<p>Nenhum horário encontrado no banco.</p>");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}