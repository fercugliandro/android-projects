package com.fercugliandro.lyrics.datatype;

public class Artista {
	
	private String nomeArtista;
	private String dns;
	
	public String getNomeArtista() {
		return nomeArtista;
	}
	public void setNomeArtista(String nomeArtista) {
		this.nomeArtista = nomeArtista;
	}
	public String getDns() {
		return dns;
	}
	public void setDns(String dns) {
		this.dns = dns;
	}
	@Override
	public String toString() {
		
		return this.dns + " - " + this.nomeArtista;
	}
}
