package com.excepciones;

public class SinSeleccionException extends PotrerosException {
	
	private static final long serialVersionUID = 1L;

	public SinSeleccionException (String mensaje, Exception e){
		super("Error de selección", "Debe seleccionar un elemento", mensaje,e);
	}
	public SinSeleccionException (String mensaje){
		super("Error de selección", "Debe seleccionar un elemento", mensaje);
	}

}
