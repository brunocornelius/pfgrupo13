package com.servicios;

import java.util.List;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import com.entidades.Rol;
import com.excepciones.DatosInvalidosException;
import com.interfaces.IRols;

@Stateless
@Remote
public class RolsBeanRemote implements IRols {

	@PersistenceContext
	private EntityManager em;
	
	
    public RolsBeanRemote() {
        
    }

	@Override
	public Rol altaRol(Rol rol) throws DatosInvalidosException {
		try {
			this.em.persist(rol);
			this.em.flush();
		}
		catch (PersistenceException excepcion) {
			throw new DatosInvalidosException("No se pudo dar de Alta",excepcion);
		}
		
		return rol;
	}

	@Override
	public Rol editarRol(Rol rol) throws DatosInvalidosException {
		try {
			this.em.merge(rol);
			this.em.flush();
		}
		catch (PersistenceException e) {
			throw new DatosInvalidosException("No se pudo guardar los cambios",e);
		}
		
		return rol;
	}

	@Override
	public boolean borrarRol(Integer id) throws DatosInvalidosException {
	
		try {
			Rol rol = this.em.find(Rol.class, id);
//			rol.setNombre(toString());
			this.em.remove(rol);	//Esta línea falla si hay usuarios con ese rol asignado.
			this.em.flush();
		}
		catch (PersistenceException excepcion) {
			throw new DatosInvalidosException("No se pudo guardar los cambios",excepcion);
		}
		
		return true;
	}
	
	@Override
	public List<Rol> obtenerListaTodos() {
		TypedQuery<Rol> query = this.em.createQuery("SELECT p FROM Rol p",Rol.class); 
		List<Rol> resultado = query.getResultList();
		return resultado;
	}
}
