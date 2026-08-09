package controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.ConsultaDAO;
import model.Consulta;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		if (session.getAttribute("usuarioLogadoId") == null) {
			response.sendRedirect("index.html");
			return;
		}
		
		int idUsuario = (int) session.getAttribute("usuarioLogadoId");
		
		// Instancia o nosso DAO (A classe especialista em banco de dados)
		ConsultaDAO dao = new ConsultaDAO();
		
		// 1. Próxima Consulta
		Consulta proxConsulta = dao.buscarProximaConsultaPaciente(idUsuario);
		if (proxConsulta != null) {
			request.setAttribute("proxMedico", proxConsulta.getMedico());
			request.setAttribute("proxEspecialidade", proxConsulta.getEspecialidade());
			request.setAttribute("proxData", proxConsulta.getDataHora());
		} else {
			request.setAttribute("proxMedico", "Nenhuma consulta");
			request.setAttribute("proxEspecialidade", "-");
			request.setAttribute("proxData", "-");
		}
		
		// 2. Estatísticas
		request.setAttribute("consultasDia", dao.contarConsultas(idUsuario, "DIA"));
		request.setAttribute("consultasMes", dao.contarConsultas(idUsuario, "MES"));
		request.setAttribute("totalConsultas", dao.contarConsultas(idUsuario, "TOTAL"));
		
		// 3. Histórico de Consultas
		List<Consulta> minhasConsultas = dao.listarHistoricoPaciente(idUsuario);
		request.setAttribute("listaConsultas", minhasConsultas);

		// Encaminha para o Visual (JSP)
		request.getRequestDispatcher("home.jsp").forward(request, response);
	}
}