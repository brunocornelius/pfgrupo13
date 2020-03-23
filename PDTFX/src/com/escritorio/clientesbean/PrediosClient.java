package com.escritorio.clientesbean;

import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.entidades.Predio;
import com.escritorio.entidadesFx.PredioFx;
import com.excepciones.DatosInvalidosException;
import com.excepciones.PotrerosException;
import com.excepciones.ProblemaDeConexionException;
import com.interfaces.IPredios;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PrediosClient {
	
//	java:global/PDT/PrediosBeanRemote!com.interfaces.IPredios
//	java:app/PDT/PrediosBeanRemote!com.interfaces.IPredios
//	java:module/PrediosBeanRemote!com.interfaces.IPredios
//	java:jboss/exported/PDT/PrediosBeanRemote!com.interfaces.IPredios
//	ejb:PDT/PrediosBeanRemote!com.interfaces.IPredios
//	java:global/PDT/PrediosBeanRemote
//	java:app/PDT/PrediosBeanRemote
//	java:module/PrediosBeanRemote
	
	private static PrediosClient instancia = new PrediosClient();
	
	private PrediosClient(){
		
	}
	
	public static PrediosClient getInstancia() {
		return instancia;
	}
	
	public ObservableList<PredioFx> obtenerTodos() throws ProblemaDeConexionException {
		IPredios prediosBean;
		
		ObservableList<PredioFx> listado = FXCollections.observableArrayList();
		try {
			prediosBean = (IPredios) InitialContext.doLookup("PDT/PrediosBeanRemote!com.interfaces.IPredios");
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		List<Predio> listaPredios = prediosBean.obtenerListaTodos();
		for (Predio p : listaPredios) {
			listado.add(new PredioFx(p));
		}
		return listado;	//Paso la lista de PredioFx
	}
	
	public PredioFx altaDePredio(PredioFx predioFx) throws ProblemaDeConexionException, DatosInvalidosException {
		IPredios prediosBean = null;
		try {
			prediosBean = (IPredios) InitialContext.doLookup("PDT/PrediosBeanRemote!com.interfaces.IPredios");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		Predio p = prediosBean.altaPredio(predioFx.getPredio());
//		predioFx.setPredio(p);
		return new PredioFx(p);
	}
	
	public PredioFx editarPredio(PredioFx predioFx) throws ProblemaDeConexionException, DatosInvalidosException {
		System.out.println("Editemos predio");
		IPredios prediosBean = null;
		try {
			prediosBean = (IPredios) InitialContext.doLookup("PDT/PrediosBeanRemote!com.interfaces.IPredios");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		Predio p = prediosBean.editarPredio(predioFx.getPredio());
		return new PredioFx(p);
	}
	
	public PredioFx borrarPredio(PredioFx predioFx) throws ProblemaDeConexionException, DatosInvalidosException {
		IPredios prediosBean = null;
		try {
			prediosBean = (IPredios) InitialContext.doLookup("PDT/PrediosBeanRemote!com.interfaces.IPredios");
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		Predio p = prediosBean.borrarPredio(predioFx.getPredio());
		if (p == null) {
			System.out.println("Se borró el predio de la BD");
			return null;
		}
		System.out.println("Se desactivó predio");
		predioFx = new PredioFx(p);
		
		return predioFx;
	}
	
	public PredioFx activarPredio(PredioFx predioFx) throws ProblemaDeConexionException, DatosInvalidosException {
		IPredios prediosBean = null;
		try {
			prediosBean = (IPredios) InitialContext.doLookup("PDT/PrediosBeanRemote!com.interfaces.IPredios");
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		Predio p = prediosBean.activarPredio(predioFx.getPredio());

		return new PredioFx(p);
	}
	
	public boolean formaEditable(PredioFx predioFx) throws ProblemaDeConexionException, DatosInvalidosException {
		IPredios prediosBean = null;
		try {
			prediosBean = (IPredios) InitialContext.doLookup("PDT/PrediosBeanRemote!com.interfaces.IPredios");
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		return prediosBean.formaEditable(predioFx.getPredio().getId());
	}
	
	public boolean existeNombre(String nombre) throws PotrerosException {
		
		IPredios prediosBean = null;
		try {
			prediosBean = (IPredios) InitialContext.doLookup("PDT/PrediosBeanRemote!com.interfaces.IPredios");
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		if (prediosBean.existeNombre(nombre)) {
			throw new DatosInvalidosException("Ya existe el nombre del predio");
		}
		return false;
	}

}
