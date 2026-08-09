package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.ConexaoDB;

@WebServlet("/carregarMedicos")
public class CarregarMedicosServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Avisa o navegador que vamos devolver texto HTML simples, com acentos normais
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		String idEspecialidade = request.getParameter("id_especialidade");
		
		if (idEspecialidade == null || idEspecialidade.isEmpty()) {
			out.println("<option value=''>Selecione uma especialidade válida</option>");
			return;
		}
		
		try (Connection con = ConexaoDB.conectar()) {
			// Busca todos os médicos que possuem a especialidade selecionada
			String sql = "SELECT p.id_perfil, u.nome_usuario, u.sobrenome_usuario "
					   + "FROM perfis p "
					   + "JOIN usuarios u ON p.usuario = u.id_usuario "
					   + "JOIN especialidades_medico em ON p.id_perfil = em.medico "
					   + "WHERE em.especialidade = ? AND p.tipo_perfil = 'Medico'";
			
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setInt(1, Integer.parseInt(idEspecialidade));
			ResultSet rs = stmt.executeQuery();
			
			boolean temMedico = false;
			out.println("<option value=''>Selecione o médico...</option>");
			
			while (rs.next()) {
				temMedico = true;
				int idPerfilMedico = rs.getInt("id_perfil");
				String nomeCompleto = "Dr(a). " + rs.getString("nome_usuario") + " " + rs.getString("sobrenome_usuario");
				
				// Cria a tag <option> que vai ser encaixada direto no HTML do select
				out.println("<option value='" + idPerfilMedico + "'>" + nomeCompleto + "</option>");
			}
			
			if (!temMedico) {
				out.println("<option value=''>Nenhum médico cadastrado para esta especialidade</option>");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			out.println("<option value=''>Erro ao carregar médicos</option>");
		}
	}
}