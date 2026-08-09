package model;

public class Consulta {
	private String medico;
	private String especialidade;
	private String dataHora;
	private String status;
	
	public Consulta(String medico, String especialidade, String dataHora, String status) {
		this.medico = medico;
		this.especialidade = especialidade;
		this.dataHora = dataHora;
		this.status = status;
	}

	public String getMedico() { return medico; }
	public String getEspecialidade() { return especialidade; }
	public String getDataHora() { return dataHora; }
	public String getStatus() { return status; }
}