package com.interfaces;

import java.util.List;
import com.entidades.Propietario;
import com.excepciones.DatosInvalidosException;

//Se define totalmente "limpia" la interfaz, para que no esté acoplada a nada mas que el Propietario
public interface IPropietarios {
	
	//ABM
	public Propietario altaPropietario(Propietario propietario) throws DatosInvalidosException;
	public Propietario editarPropietario(Propietario propietario) throws DatosInvalidosException;
	public Propietario desactivarPropietario(Propietario propietario) throws DatosInvalidosException;
	public Propietario activarPropietario(Propietario propietario) throws DatosInvalidosException;
	
	//Listados
	public List<Propietario> obtenerListaTodos();
	//No se definirán mas listados hasta que se necesiten

}
