package com.interfaces;

	import java.util.List;
	import com.entidades.Usuario;
	import com.excepciones.DatosInvalidosException;

	public interface IUsuarios {
		
		//ABM
		public Usuario altaUsuario(Usuario usuario) throws DatosInvalidosException;
		public Usuario editarUsuario(Usuario usuario) throws DatosInvalidosException;
		public boolean borrarUsuario(Integer id) throws DatosInvalidosException;
		
		//Listados
		public List<Usuario> obtenerListaTodos();
		
		//Busquedas
		public Usuario obtenerUsuario(String username, String password) throws DatosInvalidosException;

}