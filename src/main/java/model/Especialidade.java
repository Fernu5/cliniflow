package model;

public class Especialidade {
    private int idEspecialidade;
    private String tipoEspecialidade;
    private String nomeEspecialidade;

    public Especialidade() {}

    public Especialidade(int idEspecialidade, String tipoEspecialidade, String nomeEspecialidade) {
        this.idEspecialidade = idEspecialidade;
        this.tipoEspecialidade = tipoEspecialidade;
        this.nomeEspecialidade = nomeEspecialidade;
    }

    public int getIdEspecialidade() { return idEspecialidade; }
    public void setIdEspecialidade(int idEspecialidade) { this.idEspecialidade = idEspecialidade; }

    public String getTipoEspecialidade() { return tipoEspecialidade; }
    public void setTipoEspecialidade(String tipoEspecialidade) { this.tipoEspecialidade = tipoEspecialidade; }

    public String getNomeEspecialidade() { return nomeEspecialidade; }
    public void setNomeEspecialidade(String nomeEspecialidade) { this.nomeEspecialidade = nomeEspecialidade; }
}