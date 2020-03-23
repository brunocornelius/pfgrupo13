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

public class UsuariosClient {
	
//	java:global/PDT/UsuariosBeanRemote!com.interfaces.IUsuarios
//	java:app/PDT/UsuariosBeanRemote!com.interfaces.IUsuarios
//	java:module/UsuariosBeanRemote!com.interfaces.IUsuarios
//	java:jboss/exported/PDT/UsuariosBeanRemote!com.interfaces.IUsuarios
//	ejb:PDT/UsuariosBeanRemote!com.interfaces.IUsuarios
//	java:global/PDT/UsuariosBeanRemote
//	java:app/PDT/UsuariosBeanRemote
//	java:module/UsuariosBeanRemote
	
	private static UsuariosClient instancia = new UsuariosClient();
	
	private UsuariosClient(){
		
	}
	
	public static UsuariosClient getInstancia() {
		return instancia;
	}
	
	public ObservableList<UsuarioFx> obtenerTodos() throws ProblemaDeConexionException {
		IUsuarios prediosBean;
		ObservableList<UsuarioFx> listado = FXCollections.observableArrayList();
		try {
			prediosBean = (IUsuarios) InitialContext.doLookup("PDT/UsuariosBeanRemote!com.interfaces.IUsuarios");
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		List<Usuario> listaUsuarios = prediosBean.obtenerListaTodos();
		for (Usuario p : listaUsuarios) {
			UsuarioFx usuarioFx = new UsuarioFx(p); 
			listado.add(usuarioFx);
		}
		return listado;	//Paso la lista de UsuarioFx
	}
	
	public UsuarioFx altaDeUsuario(UsuarioFx usuarioFx) throws ProblemaDeConexionException, DatosInvalidosException {
		System.out.println("altaDeUsuario:");
		IUsuarios prediosBean = null;
		System.out.println(usuarioFx.getUsuario().getId());
		try {
			prediosBean = (IUsuarios) InitialContext.doLookup("PDT/UsuariosBeanRemote!com.interfaces.IUsuarios");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		Usuario p = prediosBean.altaUsuario(usuarioFx.getUsuario());
		return new UsuarioFx(p);
	}
	
	public UsuarioFx editarUsuario(UsuarioFx usuarioFx) throws ProblemaDeConexionException, DatosInvalidosException {
		System.out.println("Editemos Usuario con " + usuarioFx.getUsuario().getRoles().size() + " roles");
		IUsuarios prediosBean = null;
		try {
			prediosBean = (IUsuarios) InitialContext.doLookup("PDT/UsuariosBeanRemote!com.interfaces.IUsuarios");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		
		
		Usuario p = prediosBean.editarUsuario(usuarioFx.getUsuario());
		System.out.println("Se editó Usuario y ahora tiene " + usuarioFx.getUsuario().getRoles().size() + " roles");
		
		return new UsuarioFx(p);
	}
	
	public boolean borrarUsuario(UsuarioFx predioFx) throws ProblemaDeConexionException, DatosInvalidosException {
		IUsuarios prediosBean = null;
		try {
			prediosBean = (IUsuarios) InitialContext.doLookup("PDT/UsuariosBeanRemote!com.interfaces.IUsuarios");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		return prediosBean.borrarUsuario(predioFx.getUsuario().getId());
	}
	
	public Usuario obtenerUsuario(String username, String password) throws ProblemaDeConexionException, DatosInvalidosException {
		IUsuarios prediosBean = null;
		try {
			prediosBean = (IUsuarios) InitialContext.doLookup("PDT/UsuariosBeanRemote!com.interfaces.IUsuarios");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		return prediosBean.obtenerUsuario(username, password);
	}
	
	public Usuario cargarAdmindePrueba() throws ProblemaDeConexionException, YaExisteElementoException, IndicadorConPotrerosException {
		IAltasAutomaticas altasAutomaticas = null;
		try {
			altasAutomaticas = (IAltasAutomaticas) InitialContext.doLookup("PDT/AltasAutomaticasBeanRemote!com.interfaces.IAltasAutomaticas");
			
		} catch (NamingException e) {
			throw new ProblemaDeConexionException("No se pudo conectar al servidor");
		}
		altasAutomaticas.altaAutomaticaIndicadores();
		return altasAutomaticas.crearAdmin();
	}
	
	

}
