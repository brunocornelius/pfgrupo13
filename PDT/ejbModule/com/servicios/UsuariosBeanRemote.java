package com.servicios;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import com.entidades.Rol;
import com.entidades.Usuario;
import com.excepciones.DatosInvalidosException;
import com.interfaces.IUsuarios;

@Stateless
@Remote
public class UsuariosBeanRemote implements IUsuarios {

	@PersistenceContext
	private EntityManager em;
	
	
    public UsuariosBeanRemote() {
        
    }

	@Override
	public Usuario altaUsuario(Usuario usuario) throws DatosInvalidosException {
		try {
			usuario.setActivo(true);
			
			//Tenemos que reemplazar los roles con Roles traidos de la bd, 
			//porque hacer un loop y merge a los roles no funcionó.
			Set<Rol> rolesQueTiene = usuario.getRoles();
			usuario.setRoles(new HashSet<Rol>());
			//mergear los roles
			for (Rol rolQueTiene : rolesQueTiene) {
				Rol rolBd = this.em.find(Rol.class, rolQueTiene.getId());
				usuario.addRol(rolBd);
			}
			this.em.flush();
			this.em.persist(usuario);
			this.em.flush();
		}
		catch (PersistenceException e) {
			
			throw new DatosInvalidosException("No se pudo dar de Alta",e);
		}
		
		return usuario;
	}

	@Override
	public Usuario editarUsuario(Usuario usuario) throws DatosInvalidosException {
		try {
			this.em.merge(usuario);
			this.em.flush();
		}
		catch (PersistenceException e) {
			throw new DatosInvalidosException("No se pudo guardar los cambios",e);
		}
		
		return usuario;
	}

	@Override
	public boolean borrarUsuario(Integer id) throws DatosInvalidosException {
	
		try {
			Usuario usuario = this.em.find(Usuario.class, id);
			usuario.setActivo(false);
//			usuario.setNombres(toString());	
			this.em.flush();
		}
		catch (PersistenceException e) {
			throw new DatosInvalidosException("No se pudo guardar los cambios",e);
		}
		
		return true;
	}
	
	@Override
	public List<Usuario> obtenerListaTodos() {
		TypedQuery<Usuario> query = this.em.createQuery("SELECT p FROM Usuario p",Usuario.class); 
		List<Usuario> resultado = query.getResultList();
		return resultado;
	}

	@Override
	public Usuario obtenerUsuario(String username, String password) throws DatosInvalidosException {
		TypedQuery<Usuario> query = this.em.createQuery("SELECT u FROM Usuario u where u.username=:username",Usuario.class);
		query.setParameter("username", username);
		Usuario  resultado = null;
		try {
			resultado = query.getSingleResult();
		}catch(NoResultException e){
			throw new DatosInvalidosException("No existe el usuario");
		}
		//Si llega aca es porque existe el usuario
		if (resultado.getPassword().equals(password) ) {
			return resultado;
		}else {
			throw new DatosInvalidosException("La contraseña es incorrecta");
		}
		
		
	}
	
}
