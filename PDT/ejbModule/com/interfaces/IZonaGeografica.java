package com.interfaces;

import java.util.List;

import javax.ejb.Remote;

import com.entidades.ZonaGeografica;
import com.excepciones.DatosInvalidosException;
import com.excepciones.PotrerosException;

@Remote
public interface IZonaGeografica {
	
	//Listados
	public List<ZonaGeografica> obtenerListaTodosActivas(int idPredio);
	//ABM
	public boolean altaZonaGeografica(ZonaGeografica zonaGeografica) throws DatosInvalidosException, PotrerosException;
	public boolean editarZonaGeografica(ZonaGeografica zonaGeografica) throws DatosInvalidosException, PotrerosException;
	public boolean borrarZonaGeografica(ZonaGeografica zonaEditada) throws DatosInvalidosException;

	public boolean guardarTodo() throws DatosInvalidosException;
	public boolean finalizarBean ();
}
