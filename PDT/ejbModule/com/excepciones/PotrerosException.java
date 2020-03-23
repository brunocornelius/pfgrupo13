package com.excepciones;

public class PotrerosException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	private String titulo="";
	private String cabecera = "";

	public PotrerosException (String titulo, String cabecera, String mensaje){
		super(mensaje);
		this.titulo = titulo;
		this.cabecera = cabecera;
	}
	public PotrerosException (String titulo, String cabecera, String mensaje, Exception e){
		super(mensaje,e);
		this.titulo = titulo;
		this.cabecera = cabecera;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getCabecera() {
		return cabecera;
	}

	public void setCabecera(String cabecera) {
		this.cabecera = cabecera;
	}
	

}
