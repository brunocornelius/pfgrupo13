package com.escritorio.clientesbean;

import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.entidades.Rol;
import com.escritorio.entidadesFx.RolFx;
import com.excepciones.DatosInvalidosException;
import com.excepciones.ProblemaDeConexionException;
import com.interfaces.IRols;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RolsClient {
	
	private static RolsClient instancia = new RolsClient();
	
	private RolsClient(){
		
	}
	
	public static RolsClient getInstancia() {
		return instancia;
	}
	
	public ObservableList<RolFx> obtenerTodos() throws ProblemaDeConexionException {
		IRols rolsBean;
		
		ObservableList<RolFx> listado = FXCollections.observableArrayList();
		try {
			rolsBean = (IRols) InitialContext.doLookup("PDT/RolsBeanRemote!com.interfaces.IRols");
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		List<Rol> listaRols = rolsBean.obtenerListaTodos();
		for (Rol rol : listaRols) {
			listado.add(new RolFx(rol));
		}
		return listado;	//Paso la lista de RolFx
	}
	
	public List<Rol> obtenerTodosSinMapear() throws ProblemaDeConexionException {
		IRols rolsBean=null;
		try {
			rolsBean = (IRols) InitialContext.doLookup("PDT/RolsBeanRemote!com.interfaces.IRols");
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		return rolsBean.obtenerListaTodos();	//Paso la lista de RolFx
	}
	
	public RolFx altaDeRol(RolFx rolFx) throws ProblemaDeConexionException, DatosInvalidosException {
		IRols rolsBean = null;
		try {
			rolsBean = (IRols) InitialContext.doLookup("PDT/RolsBeanRemote!com.interfaces.IRols");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		Rol p = rolsBean.altaRol(rolFx.getRol());
		rolFx.setRol(p);
		return rolFx;
	}
	
	public RolFx editarRol(RolFx rolFx) throws ProblemaDeConexionException, DatosInvalidosException {
		System.out.println("Editemos propietario");
		IRols rolsBean = null;
		try {
			rolsBean = (IRols) InitialContext.doLookup("PDT/RolsBeanRemote!com.interfaces.IRols");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		Rol p = rolsBean.editarRol(rolFx.getRol());
		rolFx.setRol(p);
		System.out.println("Se editó propietario");
		return rolFx;
	}
	
	public boolean borrarRol(RolFx rolFx) throws ProblemaDeConexionException, DatosInvalidosException {
		IRols rolsBean = null;
		try {
			rolsBean = (IRols) InitialContext.doLookup("PDT/RolsBeanRemote!com.interfaces.IRols");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		return rolsBean.borrarRol(rolFx.getRol().getId());
	}

}
