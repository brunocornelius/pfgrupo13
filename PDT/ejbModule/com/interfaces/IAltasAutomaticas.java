package com.interfaces;

	import com.entidades.Indicador;
import com.entidades.Usuario;
import com.excepciones.IndicadorConPotrerosException;
import com.excepciones.YaExisteElementoException;

	public interface IAltasAutomaticas {
		
		public Usuario crearAdmin() throws YaExisteElementoException;	//aca ya crea los roles
		
		public  Indicador altaAutomaticaIndicadores() throws IndicadorConPotrerosException;
		
		public  void altaAutomaticaTipoZonas();
		
		public void altaAutomaticaPropietarios();

		void altaAutomaticaPredios();

}