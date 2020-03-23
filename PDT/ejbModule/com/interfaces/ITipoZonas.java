package com.interfaces;

import java.util.List;
import com.entidades.TipoZona;
import com.excepciones.DatosInvalidosException;

//Se define totalmente "limpia" la interfaz, para que no est� acoplada a nada mas que el Propietario
public interface ITipoZonas {
	
	//ABM
	public TipoZona altaTipoZona(TipoZona tz) throws DatosInvalidosException;
	public TipoZona editarTipoZona(TipoZona tz) throws DatosInvalidosException;
	public boolean borrarTipoZona(Integer id) throws DatosInvalidosException;
	
	//Listados
	public List<TipoZona> obtenerListaTodos();
	//No se definir�n mas listados hasta que se necesiten

}
