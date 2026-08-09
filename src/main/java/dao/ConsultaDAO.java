package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Consulta;
import util.ConexaoDB;

public class ConsultaDAO {

    // 1. Busca a próxima consulta e devolve preenchida no MODEL Consulta
    public Consulta buscarProximaConsultaPaciente(int idUsuario) {
        String sql = "SELECT u_medico.nome_usuario AS nome_medico, "
                + "u_medico.sobrenome_usuario AS sobrenome_medico, "
                + "e.nome_especialidade, "
                + "DATE_FORMAT(c.data_hora_consulta_inicio, '%d/%m/%Y às %H:%i') as data_formatada "
                + "FROM consultas c "
                + "JOIN perfis p_paciente ON c.paciente = p_paciente.id_perfil "
                + "JOIN perfis p_medico ON c.medico = p_medico.id_perfil "
                + "JOIN usuarios u_medico ON p_medico.usuario = u_medico.id_usuario "
                + "LEFT JOIN especialidades_medico em ON p_medico.id_perfil = em.medico "
                + "LEFT JOIN especialidades e ON em.especialidade = e.id_especialidade "
                + "WHERE p_paciente.usuario = ? AND c.status_consulta = 'Agendada' AND c.data_hora_consulta_inicio >= NOW() "
                + "ORDER BY c.data_hora_consulta_inicio ASC LIMIT 1";

        try (Connection con = ConexaoDB.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String medico = "Dr(a). " + rs.getString("nome_medico") + " " + rs.getString("sobrenome_medico");
                    String especialidade = rs.getString("nome_especialidade");
                    String data = rs.getString("data_formatada");
                    
                    // Retorna o Objeto/Model preenchido!
                    return new Consulta(medico, especialidade, data, "Agendada");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 2. Busca as estatísticas baseadas em um filtro
    public int contarConsultas(int idUsuario, String filtro) {
        String sql = "SELECT COUNT(*) AS total FROM consultas c JOIN perfis p ON c.paciente = p.id_perfil WHERE p.usuario = ? AND c.status_consulta = 'Agendada'";

        if ("DIA".equals(filtro)) {
            sql += " AND DATE(c.data_hora_consulta_inicio) = CURDATE()";
        } else if ("MES".equals(filtro)) {
            sql += " AND MONTH(c.data_hora_consulta_inicio) = MONTH(CURDATE()) AND YEAR(c.data_hora_consulta_inicio) = YEAR(CURDATE())";
        }

        try (Connection con = ConexaoDB.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 3. Busca a lista completa de consultas e devolve uma Lista de MODELS
    public List<Consulta> listarHistoricoPaciente(int idUsuario) {
        List<Consulta> lista = new ArrayList<>();
        String sql = "SELECT u_medico.nome_usuario AS nome_medico, "
                + "u_medico.sobrenome_usuario AS sobrenome_medico, "
                + "e.nome_especialidade, "
                + "DATE_FORMAT(c.data_hora_consulta_inicio, '%d/%m/%Y - %H:%i') as data_formatada, "
                + "c.status_consulta "
                + "FROM consultas c "
                + "JOIN perfis p_paciente ON c.paciente = p_paciente.id_perfil "
                + "JOIN perfis p_medico ON c.medico = p_medico.id_perfil "
                + "JOIN usuarios u_medico ON p_medico.usuario = u_medico.id_usuario "
                + "LEFT JOIN especialidades_medico em ON p_medico.id_perfil = em.medico "
                + "LEFT JOIN especialidades e ON em.especialidade = e.id_especialidade "
                + "WHERE p_paciente.usuario = ? "
                + "ORDER BY c.data_hora_consulta_inicio DESC";

        try (Connection con = ConexaoDB.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String medico = "Dr(a). " + rs.getString("nome_medico") + " " + rs.getString("sobrenome_medico");
                    String especialidade = rs.getString("nome_especialidade");
                    String data = rs.getString("data_formatada");
                    String status = rs.getString("status_consulta");
                    
                    lista.add(new Consulta(medico, especialidade, data, status));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}