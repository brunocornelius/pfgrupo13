package com.escritorio.clientesbean;

import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.entidades.Predio;
import com.entidades.Indicador;
import com.entidades.Potrero;
import com.escritorio.entidadesFx.PotreroFx;
import com.excepciones.DatosInvalidosException;
import com.excepciones.PotrerosException;
import com.excepciones.ProblemaDeConexionException;
import com.interfaces.IIndicadors;
import com.interfaces.IPotreros;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PotrerosClient {
	//Este cliente consume un Bean con estado, así que no me sirve que sea singleton
	private IPotreros potreroBean;
	private Predio predio;

	public PotrerosClient(Predio p) throws NamingException {
		super();
		this.predio = p;
		System.out.println("Carguemos potreros bean");
		this.potreroBean = (IPotreros) InitialContext.doLookup("PDT/PotrerosBeanRemote!com.interfaces.IPotreros");
		System.out.println("Se cargo PotrerosBean correctamente");
	}
	
	public void cargarListaIndicadores(ObservableList<Indicador> listado) throws ProblemaDeConexionException {
		IIndicadors indicadoresBean;
		
		try {
			indicadoresBean = (IIndicadors) InitialContext.doLookup("PDT/IndicadorsBeanRemote!com.interfaces.IIndicadors");
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		List<Indicador> listaIndicadores = indicadoresBean.obtenerListaSinHijosActivos();
		for (Indicador tipoZona : listaIndicadores) {
			listado.add(tipoZona);
		}
	}
	
	public void altaPotrero(PotreroFx potreroFx) throws DatosInvalidosException, PotrerosException {
		int id = this.potreroBean.altaPotrero(potreroFx.getPotrero());
		System.out.println("IdAnterior: " + potreroFx.getId()  + "\t idNuevo: " + id);
		potreroFx.setId(id);
	}
	
	public void editarPotrero(PotreroFx potreroFx) throws DatosInvalidosException, PotrerosException {
		int id = this.potreroBean.editarPotrero(potreroFx.getPotrero());
		System.out.println("IdAnterior: " + potreroFx.getId()  + "\t idNuevo: " + id);
		potreroFx.setId(id);
	}
	
	public boolean borrarPotrero(PotreroFx potreroFx) throws DatosInvalidosException {
		System.out.println("Borrado " + potreroFx.getId());
		return this.potreroBean.borrarPotrero(potreroFx.getPotrero());
	}
	
	
	public ObservableList<PotreroFx> obtenerTodos() {
		System.out.println("Se obtuvieron todas las Potrero");
		ObservableList<PotreroFx> listadoFx = FXCollections.observableArrayList();
		List<Potrero> potreros = this.potreroBean.obtenerListaTodos(this.predio.getId());
		for (Potrero potrero : potreros) {
			listadoFx.add(new PotreroFx(potrero));
		}
		
		return listadoFx;
	}
	
	public List<Potrero> obtenerTodosSinFx() {
		return this.potreroBean.obtenerListaTodos(this.predio.getId());
		
	}
	
	public void guardarTodo() throws DatosInvalidosException {
		this.potreroBean.guardarTodo();
		//si no salta excepcion
		this.potreroBean.finalizarBean();
	}
	
	
	
}
