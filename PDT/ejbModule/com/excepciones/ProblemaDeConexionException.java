package com.excepciones;

public class ProblemaDeConexionException extends PotrerosException {
	
	private static final long serialVersionUID = 1L;

	public ProblemaDeConexionException (String mensaje, Exception e){
		super("Error de conexion", "No se pudo conectar al servidor", mensaje , e);
	}
	
	public ProblemaDeConexionException (String mensaje){
		super("Error de conexion", "No se pudo conectar al servidor", mensaje);
	}

}
