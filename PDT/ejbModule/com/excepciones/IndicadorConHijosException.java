package com.excepciones;

public class IndicadorConHijosException extends PotrerosException {
	
	private static final long serialVersionUID = 1L;

	public IndicadorConHijosException (String mensaje, Exception e){
		super("Datos invalidos","El indicador contiene hijos activos",mensaje,e);
	}
	
	public IndicadorConHijosException (String mensaje){
		super("Datos invalidos","El indicador contiene hijos activos",mensaje);
	}

}
