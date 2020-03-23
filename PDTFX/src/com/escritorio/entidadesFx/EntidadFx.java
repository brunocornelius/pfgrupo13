package com.escritorio.entidadesFx;

import com.excepciones.DatosInvalidosException;
import com.excepciones.ProblemaDeConexionException;

//Interface para definir los métodos que tienen que implementar si o si las entidades reconvertidas para la interfaz JavaFX
public interface EntidadFx {

	public boolean losCamposSonValidos() throws ProblemaDeConexionException, DatosInvalidosException;
	
}
