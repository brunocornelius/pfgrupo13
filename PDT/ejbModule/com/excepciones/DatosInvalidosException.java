package com.excepciones;

public class DatosInvalidosException extends PotrerosException {
	
	private static final long serialVersionUID = 1L;

	public DatosInvalidosException (String mensaje, Exception e){
		super("Datos invalidos","Los datos ingresados no son válidos",mensaje,e);
	}
	
	public DatosInvalidosException (String mensaje){
		super("Datos invalidos","Los datos ingresados no son válidos",mensaje);
	}

}
