package com.escritorio.clientesbean;

import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.entidades.Predio;
import com.entidades.TipoZona;
import com.entidades.ZonaGeografica;
import com.escritorio.entidadesFx.ZonaGeograficaFx;
import com.excepciones.DatosInvalidosException;
import com.excepciones.PotrerosException;
import com.excepciones.ProblemaDeConexionException;
import com.interfaces.ITipoZonas;
import com.interfaces.IZonaGeografica;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ZonaGeograficasClient {
	//Este cliente consume un Bean con estado, así que no me sirve que sea singleton
	private IZonaGeografica zonaGeograficaBean;
	private Predio predio;

	public ZonaGeograficasClient(Predio p) throws NamingException {
		super();
		this.predio = p;
		
		this.zonaGeograficaBean = (IZonaGeografica) InitialContext.doLookup("PDT/ZonaGeograficasBeanRemote!com.interfaces.IZonaGeografica");
	}
	
	public void cargarListaTipoZonas(ObservableList<TipoZona> listado) throws ProblemaDeConexionException {
		ITipoZonas tipoZonasBean;
		
		try {
			tipoZonasBean = (ITipoZonas) InitialContext.doLookup("PDT/TipoZonasBeanRemote!com.interfaces.ITipoZonas");
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		List<TipoZona> listaTipoZonas = tipoZonasBean.obtenerListaTodos();
		for (TipoZona tipoZona : listaTipoZonas) {
			listado.add(tipoZona);
		}
	}
	
	public boolean altaZonaGeografica(ZonaGeograficaFx zonaFx) throws DatosInvalidosException, PotrerosException {
		return this.zonaGeograficaBean.altaZonaGeografica(zonaFx.getZonaGeografica());
	}
	
	public boolean editarZonaGeografica(ZonaGeograficaFx zonaFx) throws DatosInvalidosException, PotrerosException {
		return this.zonaGeograficaBean.editarZonaGeografica(zonaFx.getZonaGeografica());
	}
	
	public boolean borrarZonaGeografica(ZonaGeograficaFx zonaFx) throws DatosInvalidosException {
		return this.zonaGeograficaBean.borrarZonaGeografica(zonaFx.getZonaGeografica());
	}
	
	
	public ObservableList<ZonaGeograficaFx> obtenerListaTodosActivas() {
		System.out.println("Se obtuvieron todas las ZonaGeografica");
		ObservableList<ZonaGeograficaFx> listadoFx = FXCollections.observableArrayList();
		List<ZonaGeografica> zonasGeo = this.zonaGeograficaBean.obtenerListaTodosActivas(this.predio.getId());
		for (ZonaGeografica zonaGeografica : zonasGeo) {
			listadoFx.add(new ZonaGeograficaFx(zonaGeografica));
		}
		
		return listadoFx;
	}
	
	public void guardarTodo() throws DatosInvalidosException {
		this.zonaGeograficaBean.guardarTodo();
		//Si no salta excepcion
		this.zonaGeograficaBean.finalizarBean();	//Es necesario esto?
	}
	
	
	
}
