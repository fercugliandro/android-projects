package com.fercugliandro.blacklist.datatype;

public class Calendar {

	private Integer id;
	private String dataEvento;
	private String horaInicio;
	private String horaTermino;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDataEvento() {
		return dataEvento;
	}
	public void setDataEvento(String dataEvento) {
		this.dataEvento = dataEvento;
	}
	public String getHoraInicio() {
		return horaInicio;
	}
	public void setHoraInicio(String horaInicio) {
		this.horaInicio = horaInicio;
	}
	public String getHoraTermino() {
		return horaTermino;
	}
	public void setHoraTermino(String horaTermino) {
		this.horaTermino = horaTermino;
	}
}
