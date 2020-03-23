package com.interfaces;

import java.util.List;
import com.entidades.Predio;
import com.excepciones.DatosInvalidosException;

//Se define totalmente "limpia" la interfaz, para que no esté acoplada a nada mas que el Predio
public interface IPredios {
	
	//ABM
	public Predio altaPredio(Predio predio) throws DatosInvalidosException;
	public Predio editarPredio(Predio predio) throws DatosInvalidosException;
	public Predio borrarPredio(Predio predio) throws DatosInvalidosException;
	public Predio activarPredio(Predio predio) throws DatosInvalidosException;
	
	//Listados
	public List<Predio> obtenerListaTodos();
	//No se definirán mas listados hasta que se necesiten
	public boolean formaEditable(int idPredio);
	public boolean existeNombre(String nombre);

}
