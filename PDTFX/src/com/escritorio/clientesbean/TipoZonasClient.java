package com.escritorio.clientesbean;

import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.entidades.TipoZona;
import com.escritorio.entidadesFx.TipoZonaFx;
import com.excepciones.DatosInvalidosException;
import com.excepciones.ProblemaDeConexionException;
import com.interfaces.ITipoZonas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TipoZonasClient {
	
//	java:global/PDT/TipoZonasBeanRemote!com.interfaces.ITipoZonas
//	java:app/PDT/TipoZonasBeanRemote!com.interfaces.ITipoZonas
//	java:module/TipoZonasBeanRemote!com.interfaces.ITipoZonas
//	java:jboss/exported/PDT/TipoZonasBeanRemote!com.interfaces.ITipoZonas
//	ejb:PDT/TipoZonasBeanRemote!com.interfaces.ITipoZonas
//	java:global/PDT/TipoZonasBeanRemote
//	java:app/PDT/TipoZonasBeanRemote
//	java:module/TipoZonasBeanRemote
	
	private static TipoZonasClient instancia = new TipoZonasClient();
	
	private TipoZonasClient(){
		
	}
	
	public static TipoZonasClient getInstancia() {
		return instancia;
	}
	
	public ObservableList<TipoZonaFx> obtenerTodos() throws ProblemaDeConexionException {
		ITipoZonas propietariosBean;
		
		ObservableList<TipoZonaFx> listado = FXCollections.observableArrayList();
		try {
			propietariosBean = (ITipoZonas) InitialContext.doLookup("PDT/TipoZonasBeanRemote!com.interfaces.ITipoZonas");
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		List<TipoZona> listaTipoZonas = propietariosBean.obtenerListaTodos();
		for (TipoZona tipoZona : listaTipoZonas) {
			listado.add(new TipoZonaFx(tipoZona));
		}
		return listado;	//Paso la lista de TipoZonaFx
	}
	
	
	public TipoZonaFx altaDeTipoZona(TipoZonaFx tipoZonaFx) throws ProblemaDeConexionException, DatosInvalidosException {
		ITipoZonas propietariosBean = null;
		try {
			propietariosBean = (ITipoZonas) InitialContext.doLookup("PDT/TipoZonasBeanRemote!com.interfaces.ITipoZonas");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		TipoZona p = propietariosBean.altaTipoZona(tipoZonaFx.getTipoZona());
		tipoZonaFx.setTipoZona(p);
		return tipoZonaFx;
	}
	
	public TipoZonaFx editarTipoZona(TipoZonaFx tipoZonaFx) throws ProblemaDeConexionException, DatosInvalidosException {
		System.out.println("Editemos propietario");
		ITipoZonas propietariosBean = null;
		try {
			propietariosBean = (ITipoZonas) InitialContext.doLookup("PDT/TipoZonasBeanRemote!com.interfaces.ITipoZonas");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		TipoZona p = propietariosBean.editarTipoZona(tipoZonaFx.getTipoZona());
		tipoZonaFx.setTipoZona(p);
		System.out.println("Se editó propietario");
		return tipoZonaFx;
	}
	
	public boolean borrarTipoZona(TipoZonaFx tipoZonaFx) throws ProblemaDeConexionException, DatosInvalidosException {
		ITipoZonas propietariosBean = null;
		try {
			propietariosBean = (ITipoZonas) InitialContext.doLookup("PDT/TipoZonasBeanRemote!com.interfaces.ITipoZonas");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		return propietariosBean.borrarTipoZona(tipoZonaFx.getTipoZona().getId());
	}

}
