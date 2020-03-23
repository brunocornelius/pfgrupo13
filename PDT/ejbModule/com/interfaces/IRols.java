package com.interfaces;

	import java.util.List;
	import com.entidades.Rol;
	import com.excepciones.DatosInvalidosException;

	public interface IRols {
		
		//ABM
		public Rol altaRol(Rol rol) throws DatosInvalidosException;
		public Rol editarRol(Rol rol) throws DatosInvalidosException;
		public boolean borrarRol(Integer id) throws DatosInvalidosException;
		
		//Listados
		public List<Rol> obtenerListaTodos();
		

}