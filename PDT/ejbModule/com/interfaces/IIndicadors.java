package com.interfaces;

import java.util.List;
import com.entidades.Indicador;
import com.excepciones.DatosInvalidosException;
import com.excepciones.NoExisteElementoException;

//Se define totalmente "limpia" la interfaz, para que no esté acoplada a nada mas que el Propietario
public interface IIndicadors {
	
	//ABM
	public Indicador altaIndicador(Indicador tz) throws DatosInvalidosException;
	public Indicador editarIndicador(Indicador tz) throws DatosInvalidosException;
	public boolean borrarIndicador(Integer id) throws DatosInvalidosException;
	
	//Listados
	public List<Indicador> obtenerListaTodos();
	public List<Indicador> obtenerListaSinHijosActivos();
	public Indicador obtenerIndicador(String nombre) throws NoExisteElementoException, DatosInvalidosException;
	public Indicador obtenerIndicadorParaPredio(String nombre, int idPredio) throws NoExisteElementoException, DatosInvalidosException;
	
	//No se definirán mas listados hasta que se necesiten

}
