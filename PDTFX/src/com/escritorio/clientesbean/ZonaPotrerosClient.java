package com.escritorio.clientesbean;

import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.entidades.Predio;
import com.entidades.ZonaPotrero;
import com.excepciones.ProblemaDeConexionException;
import com.interfaces.IZonaPotrero;

public class ZonaPotrerosClient {
	
	private static ZonaPotrerosClient instancia = new ZonaPotrerosClient();
	
	private ZonaPotrerosClient(){
		
	}
	
	public static ZonaPotrerosClient getInstancia() {
		return instancia;
	}
	
	public List<ZonaPotrero> obtenerZonaPotrerosPredio(Predio predio) throws ProblemaDeConexionException{
		IZonaPotrero zonaPotreroBean;
		
		try {
			zonaPotreroBean = (IZonaPotrero) InitialContext.doLookup("PDT/ZonaPotrerosBeanRemote!com.interfaces.IZonaPotrero");
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		return zonaPotreroBean.obtenerListaTodosActivas(predio.getId());
	}

}
