package com.interfaces;

import java.util.List;

import javax.ejb.Remote;

import com.entidades.ZonaPotrero;

@Remote
public interface IZonaPotrero {
	
	//Listados
	public List<ZonaPotrero> obtenerListaTodosActivas(int idPredio);

}