package com.excepciones;

public class IndicadorConPotrerosException extends PotrerosException {
	
	private static final long serialVersionUID = 1L;

	public IndicadorConPotrerosException (String mensaje, Exception e){
		super("Datos invalidos","El indicador contiene potreros activos",mensaje,e);
	}
	
	public IndicadorConPotrerosException (String mensaje){
		super("Datos invalidos","El indicador contiene potreros activos",mensaje);
	}

}
