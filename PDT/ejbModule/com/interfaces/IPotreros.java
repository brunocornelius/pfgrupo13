package com.interfaces;

import java.util.List;

import javax.ejb.Remote;

import com.entidades.Potrero;
import com.excepciones.DatosInvalidosException;
import com.excepciones.PotrerosException;

@Remote
public interface IPotreros {
	
	//Listados
	public List<Potrero> obtenerListaTodos(int idPredio);
	//ABM
	public int altaPotrero(Potrero potrero) throws DatosInvalidosException, PotrerosException;
	public int editarPotrero(Potrero potrero) throws DatosInvalidosException, PotrerosException;
	public boolean borrarPotrero(Potrero potreroEditado) throws DatosInvalidosException;

	public boolean guardarTodo() throws DatosInvalidosException;
	public boolean finalizarBean ();

}

