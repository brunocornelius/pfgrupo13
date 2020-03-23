package com.escritorio.clientesbean;

import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.entidades.Usuario;
import com.escritorio.entidadesFx.UsuarioFx;
import com.excepciones.DatosInvalidosException;
import com.excepciones.IndicadorConPotrerosException;
import com.excepciones.ProblemaDeConexionException;
import com.excepciones.YaExisteElementoException;
import com.interfaces.IAltasAutomaticas;
import com.interfaces.IUsuarios;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AltasAutomaticasClient {

	
	private static AltasAutomaticasClient instancia = new AltasAutomaticasClient();
	
	private AltasAutomaticasClient(){
		
	}
	
	public static AltasAutomaticasClient getInstancia() {
		return instancia;
	}
	
	public void altaAutomaticaIndicadores() throws ProblemaDeConexionException, IndicadorConPotrerosException {
		IAltasAutomaticas altasAutomaticas = null;
		try {
			altasAutomaticas = (IAltasAutomaticas) InitialContext.doLookup("PDT/AltasAutomaticasBeanRemote!com.interfaces.IAltasAutomaticas");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		altasAutomaticas.altaAutomaticaIndicadores();
	}
	
	public void altaAutomaticaTipoZonas() throws ProblemaDeConexionException {
		IAltasAutomaticas altasAutomaticas = null;
		try {
			altasAutomaticas = (IAltasAutomaticas) InitialContext.doLookup("PDT/AltasAutomaticasBeanRemote!com.interfaces.IAltasAutomaticas");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		altasAutomaticas.altaAutomaticaTipoZonas();
	}
	
	public void altaAutomaticaPropietarios() throws ProblemaDeConexionException {
		IAltasAutomaticas altasAutomaticas = null;
		try {
			altasAutomaticas = (IAltasAutomaticas) InitialContext.doLookup("PDT/AltasAutomaticasBeanRemote!com.interfaces.IAltasAutomaticas");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		altasAutomaticas.altaAutomaticaPropietarios();
	}
	
	public void altaAutomaticaPredios() throws ProblemaDeConexionException {
		IAltasAutomaticas altasAutomaticas = null;
		try {
			altasAutomaticas = (IAltasAutomaticas) InitialContext.doLookup("PDT/AltasAutomaticasBeanRemote!com.interfaces.IAltasAutomaticas");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		altasAutomaticas.altaAutomaticaPredios();
	}
	

}
