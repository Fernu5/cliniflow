package controller;

import java.io.IOException;
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
import jakarta.servlet.http.HttpSession;

// Importando o Model que criamos!
import model.Especialidade;
import util.ConexaoDB;

@WebServlet("/agendamento")
public class AgendamentoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		// Segurança
		if (session.getAttribute("usuarioLogadoId") == null) {
			response.sendRedirect("index.html");
			return;
		}
		
		try (Connection con = ConexaoDB.conectar()) {
			
			// Busca todas as especialidades cadastradas no banco
			String sql = "SELECT * FROM especialidades ORDER BY nome_especialidade ASC";
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			
			List<Especialidade> listaEspecialidades = new ArrayList<>();
			
			while (rs.next()) {
				// Usando o Model Especialidade na prática!
				Especialidade esp = new Especialidade();
				esp.setIdEspecialidade(rs.getInt("id_especialidade"));
				esp.setTipoEspecialidade(rs.getString("tipo_especialidade"));
				esp.setNomeEspecialidade(rs.getString("nome_especialidade"));
				
				listaEspecialidades.add(esp);
			}
			
			// Envia a lista de objetos para a tela
			request.setAttribute("especialidades", listaEspecialidades);
			
			request.getRequestDispatcher("agendamento.jsp").forward(request, response);
			
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("home?erro=carregar_agendamento");
		}
	}
}