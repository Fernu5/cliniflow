package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.ConexaoDB;

// Essa anotação é o link que conecta o HTML ao Java. Bate com o action="login" do form!
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 1. Pega o que o usuário digitou na tela HTML
		String email = request.getParameter("email_usuario");
		String senha = request.getParameter("senha_usuario");
		
		// 2. Abre a porta do banco de dados
		try (Connection con = ConexaoDB.conectar()) {
			
			// 3. Escreve o SELECT para buscar o usuário e também juntar com a tabela de perfis
			String sql = "SELECT u.id_usuario, u.nome_usuario, p.tipo_perfil " +
					     "FROM usuarios u " +
					     "LEFT JOIN perfis p ON u.id_usuario = p.usuario " +
					     "WHERE u.email_usuario = ? AND u.senha_usuario = ? AND u.status_usuario = 'Ativo'";
			
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, email);
			stmt.setString(2, senha);
			
			ResultSet rs = stmt.executeQuery();
			
			// 4. Se encontrou alguém no banco...
			if (rs.next()) {
				HttpSession session = request.getSession();
				session.setAttribute("usuarioLogadoId", rs.getInt("id_usuario"));
				session.setAttribute("usuarioLogadoNome", rs.getString("nome_usuario"));
				session.setAttribute("usuarioLogadoPerfil", rs.getString("tipo_perfil"));
				
				String perfil = rs.getString("tipo_perfil");
				
				if ("Medico".equals(perfil)) {
					response.sendRedirect("home-medico.jsp");
				} else if ("Recepcionista".equals(perfil)) {
					response.sendRedirect("home-adm.jsp"); 
				} else {
					response.sendRedirect("home"); 
				}
			} else {
				response.sendRedirect("index.html?erro=credenciais");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro no login: " + e.getMessage());
			response.sendRedirect("index.html?erro=banco");
		}
	}
}