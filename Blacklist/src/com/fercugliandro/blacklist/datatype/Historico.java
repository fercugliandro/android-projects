package com.fercugliandro.blacklist.datatype;

public class Historico {
	
	private Integer id;
	private String numeroTelefone;
	private Integer qtdeLigacao;
	private String ultimaLigacao;
	private String motivo;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNumeroTelefone() {
		return numeroTelefone;
	}
	public void setNumeroTelefone(String numeroTelefone) {
		this.numeroTelefone = numeroTelefone;
	}
	public Integer getQtdeLigacao() {
		return qtdeLigacao;
	}
	public void setQtdeLigacao(Integer qtdeLigacao) {
		this.qtdeLigacao = qtdeLigacao;
	}
	public String getUltimaLigacao() {
		return ultimaLigacao;
	}
	public void setUltimaLigacao(String ultimaLigacao) {
		this.ultimaLigacao = ultimaLigacao;
	}
	public String getMotivo() {
		return motivo;
	}
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
}
