package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {
    
    // Caminho do seu banco de dados
    private static final String URL = "jdbc:mysql://localhost:3306/clinica?useTimezone=true&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String SENHA = "12345"; 
    
    public static Connection conectar() {
        try {
            // Força o carregamento do driver do MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (ClassNotFoundException e) {
            System.out.println("Driver do MySQL não encontrado! " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.out.println("Erro ao conectar com o banco de dados! " + e.getMessage());
            return null;
        }
    }
    
    public static void main(String[] args) {
        Connection con = conectar();
        if (con != null) {
            System.out.println("SUCESSO! O Java encontrou o MySQL e conectou no banco clinica!");
        } else {
            System.out.println("FALHA! Verifique a senha ou se o MySQL Workbench está aberto.");
        }
    }
    
}