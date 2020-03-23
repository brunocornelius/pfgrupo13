package com.excepciones;

public class YaExisteElementoException extends PotrerosException {
	
	private static final long serialVersionUID = 1L;

	public YaExisteElementoException (String mensaje, Exception e){
		super("Elemento incorrecto","Ya existe el elemento en la colección",mensaje,e);
	}
	public YaExisteElementoException (String mensaje){
		super("Elemento incorrecto","Ya existe el elemento en la colección",mensaje);
	}

}
