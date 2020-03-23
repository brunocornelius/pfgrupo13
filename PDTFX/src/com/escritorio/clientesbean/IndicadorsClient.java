package com.escritorio.clientesbean;

import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.entidades.Indicador;
import com.entidades.Predio;
import com.escritorio.entidadesFx.IndicadorFx;
import com.escritorio.entidadesFx.PredioFx;
import com.excepciones.DatosInvalidosException;
import com.excepciones.NoExisteElementoException;
import com.excepciones.ProblemaDeConexionException;
import com.interfaces.IIndicadors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class IndicadorsClient {
	
	private static IndicadorsClient instancia = new IndicadorsClient();
	
	private IndicadorsClient(){
		
	}
	
	public static IndicadorsClient getInstancia() {
		return instancia;
	}
	
	public ObservableList<IndicadorFx> obtenerTodos() throws ProblemaDeConexionException {
		IIndicadors indicadorsBean;
		
		ObservableList<IndicadorFx> listado = FXCollections.observableArrayList();
		try {
			indicadorsBean = (IIndicadors) InitialContext.doLookup("PDT/IndicadorsBeanRemote!com.interfaces.IIndicadors");
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		List<Indicador> listaIndicadors = indicadorsBean.obtenerListaTodos();
		for (Indicador indicador : listaIndicadors) {
			System.out.println("Parseando indicador " + indicador.getNombre());
			listado.add(new IndicadorFx(indicador));
		}
		return listado;	//Paso la lista de IndicadorFx
	}
	
	
	public IndicadorFx altaDeIndicador(IndicadorFx indicadorFx) throws ProblemaDeConexionException, DatosInvalidosException {
		IIndicadors indicadorsBean = null;
		try {
			indicadorsBean = (IIndicadors) InitialContext.doLookup("PDT/IndicadorsBeanRemote!com.interfaces.IIndicadors");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		Indicador p = indicadorsBean.altaIndicador(indicadorFx.getIndicador());
		indicadorFx.setIndicador(p);
		return indicadorFx;
	}
	
	public IndicadorFx editarIndicador(IndicadorFx indicadorFx) throws ProblemaDeConexionException, DatosInvalidosException {
		System.out.println("Editemos indicador");
		IIndicadors indicadorsBean = null;
		try {
			indicadorsBean = (IIndicadors) InitialContext.doLookup("PDT/IndicadorsBeanRemote!com.interfaces.IIndicadors");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		Indicador p = indicadorsBean.editarIndicador(indicadorFx.getIndicador());
		indicadorFx.setIndicador(p);
		System.out.println("Se editó indicador");
		return indicadorFx;
	}
	
	public boolean borrarIndicador(IndicadorFx indicadorFx) throws ProblemaDeConexionException, DatosInvalidosException {
		IIndicadors indicadorsBean = null;
		try {
			indicadorsBean = (IIndicadors) InitialContext.doLookup("PDT/IndicadorsBeanRemote!com.interfaces.IIndicadors");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		return indicadorsBean.borrarIndicador(indicadorFx.getIndicador().getId());
	}
	
	public TreeItem<Indicador> obtenerIndicadorRaiz() throws ProblemaDeConexionException, NoExisteElementoException, DatosInvalidosException {
		IIndicadors indicadorsBean;
		
		try {
			indicadorsBean = (IIndicadors) InitialContext.doLookup("PDT/IndicadorsBeanRemote!com.interfaces.IIndicadors");
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		Indicador indicadorRaiz = indicadorsBean.obtenerIndicador("TOTAL PREDIO");
		return cargarIndicador(indicadorRaiz);
		
	}
	
	public TreeItem<Indicador> obtenerIndicadorRaizParaPredio(PredioFx predio) throws ProblemaDeConexionException, NoExisteElementoException, DatosInvalidosException {
		IIndicadors indicadorsBean;
		
		try {
			indicadorsBean = (IIndicadors) InitialContext.doLookup("PDT/IndicadorsBeanRemote!com.interfaces.IIndicadors");
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		Indicador indicadorRaiz = indicadorsBean.obtenerIndicadorParaPredio("TOTAL PREDIO", predio.getPredio().getId());
		return cargarIndicador(indicadorRaiz);
		
	}
	
	private TreeItem<Indicador> cargarIndicador(Indicador indicador){
		TreeItem<Indicador> treeItem = new TreeItem<Indicador>();
		treeItem.setValue(indicador);
		
		if (indicador.getHijos().isEmpty()) {
			//Si no tiene hijos...
		}else {
			//Si tiene hijos...
			for (Indicador indicadorHijo : indicador.getHijos()) {
				treeItem.getChildren().add(cargarIndicador(indicadorHijo));
			}
		}
		
		return treeItem;
		
	}

}
