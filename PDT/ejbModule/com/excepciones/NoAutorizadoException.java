package com.excepciones;

public class NoAutorizadoException extends PotrerosException {
	
	private static final long serialVersionUID = 1L;

	public NoAutorizadoException (String mensaje, Exception e){
		super("No autorizado","Usted no se encuentra autorizado para realizar la acción solicitada",mensaje,e);
	}
	
	public NoAutorizadoException (String mensaje){
		super("No Autorizado","Usted no se encuentra autorizado para realizar la acción solicitada",mensaje);
	}

}
