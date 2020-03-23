package com.escritorio.clientesbean;

import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.entidades.Propietario;
import com.escritorio.entidadesFx.PropietarioFx;
import com.excepciones.DatosInvalidosException;
import com.excepciones.ProblemaDeConexionException;
import com.interfaces.IPropietarios;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PropietariosClient {
	
//	java:global/PDT/PropietariosBeanRemote!com.interfaces.IPropietarios
//	java:app/PDT/PropietariosBeanRemote!com.interfaces.IPropietarios
//	java:module/PropietariosBeanRemote!com.interfaces.IPropietarios
//	java:jboss/exported/PDT/PropietariosBeanRemote!com.interfaces.IPropietarios
//	ejb:PDT/PropietariosBeanRemote!com.interfaces.IPropietarios
//	java:global/PDT/PropietariosBeanRemote
//	java:app/PDT/PropietariosBeanRemote
//	java:module/PropietariosBeanRemote
	
	private static PropietariosClient instancia = new PropietariosClient();
	
	private PropietariosClient(){
		
	}
	
	public static PropietariosClient getInstancia() {
		return instancia;
	}
	
	public ObservableList<PropietarioFx> obtenerTodos() throws ProblemaDeConexionException {
		IPropietarios propietariosBean;
		
		ObservableList<PropietarioFx> listado = FXCollections.observableArrayList();
		try {
			propietariosBean = (IPropietarios) InitialContext.doLookup("PDT/PropietariosBeanRemote!com.interfaces.IPropietarios");
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		List<Propietario> listaPropietarios = propietariosBean.obtenerListaTodos();
		for (Propietario p : listaPropietarios) {
			listado.add(new PropietarioFx(p));
		}
		return listado;	//Paso la lista de PropietarioFx
	}
	
	public PropietarioFx altaDePropietario(PropietarioFx propietarioFx) throws ProblemaDeConexionException, DatosInvalidosException {
		IPropietarios propietariosBean = null;
		try {
			propietariosBean = (IPropietarios) InitialContext.doLookup("PDT/PropietariosBeanRemote!com.interfaces.IPropietarios");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		Propietario p = propietariosBean.altaPropietario(propietarioFx.getPropietario());
		propietarioFx.setPropietario(p);
		return propietarioFx;
	}
	
	public PropietarioFx editarPropietario(PropietarioFx propietarioFx) throws ProblemaDeConexionException, DatosInvalidosException {
		System.out.println("Editemos propietario");
		IPropietarios propietariosBean = null;
		try {
			propietariosBean = (IPropietarios) InitialContext.doLookup("PDT/PropietariosBeanRemote!com.interfaces.IPropietarios");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		Propietario p = propietariosBean.editarPropietario(propietarioFx.getPropietario());
		propietarioFx.setPropietario(p);
		System.out.println("Se editó propietario");
		return propietarioFx;
	}
	
	public PropietarioFx desactivarPropietario(PropietarioFx propietarioFx) throws ProblemaDeConexionException, DatosInvalidosException {
		IPropietarios propietariosBean = null;
		try {
			propietariosBean = (IPropietarios) InitialContext.doLookup("PDT/PropietariosBeanRemote!com.interfaces.IPropietarios");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		Propietario p = propietariosBean.desactivarPropietario(propietarioFx.getPropietario());
		
		System.out.println("Se desactivó propietario " + p.getNombre());
		return new PropietarioFx(p);
	}
	
	public PropietarioFx activarPropietario(PropietarioFx propietarioFx) throws ProblemaDeConexionException, DatosInvalidosException {
		IPropietarios propietariosBean = null;
		try {
			propietariosBean = (IPropietarios) InitialContext.doLookup("PDT/PropietariosBeanRemote!com.interfaces.IPropietarios");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		Propietario p = propietariosBean.activarPropietario(propietarioFx.getPropietario());
		System.out.println("Se activó propietario " + p.getNombre());
		return new PropietarioFx(p);
	}

}
