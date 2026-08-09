package model;

import java.util.Date;

public class Usuario {
    private int idUsuario;
    private String nomeUsuario;
    private String sobrenomeUsuario;
    private Date dataNascimento;
    private String cpf;
    private String email;
    private String senha;
    private String status;
    private String sexo;
    private boolean isAdm;
    private String crm;

    // Construtor vazio (obrigatório pelas boas práticas do Java)
    public Usuario() {}

    // Getters e Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getSobrenomeUsuario() { return sobrenomeUsuario; }
    public void setSobrenomeUsuario(String sobrenomeUsuario) { this.sobrenomeUsuario = sobrenomeUsuario; }

    public Date getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(Date dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public boolean isAdm() { return isAdm; }
    public void setAdm(boolean adm) { this.isAdm = adm; }

    public String getCrm() { return crm; }
    public void setCrm(String crm) { this.crm = crm; }
}