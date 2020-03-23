package com.excepciones;

public class NoExisteElementoException extends PotrerosException {
	
	private static final long serialVersionUID = 1L;

	public NoExisteElementoException (String mensaje, Exception e){
		super("Elemento incorrecto","No existe el elemento en la colección",mensaje,e);
	}

}
