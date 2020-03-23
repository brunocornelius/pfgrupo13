package com.servicios;

import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import com.entidades.Propietario;
import com.excepciones.DatosInvalidosException;
import com.interfaces.IPropietarios;


@Stateless
@Remote
public class PropietariosBeanRemote implements IPropietarios {

	@PersistenceContext
	private EntityManager em;
	
	
    public PropietariosBeanRemote() {
        
    }

	@Override
	public Propietario altaPropietario(Propietario propietario) throws DatosInvalidosException {
		try {
			propietario.setActivo(true);
			propietario.setDesde(new Date());
			this.em.persist(propietario);
			this.em.flush();
		}
		catch (PersistenceException e) {
//			System.out.println("SQL Error Type : " + e.getClass().getName());
//            System.out.println("Error Message  : " + e.getMessage());
//            System.out.println("Error Code     : " + e.getErrorCode());
//            System.out.println("SQL State      : " + e.getSQLState());
			throw new DatosInvalidosException("No se pudo dar el Alta",e);
		}
		
		return propietario;
	}

	@Override
	public Propietario editarPropietario(Propietario propietario) throws DatosInvalidosException {
		try {
			this.em.merge(propietario);
			this.em.flush();
		}
		catch (PersistenceException e) {
			throw new DatosInvalidosException("No se pudo grabar los cambios",e);
		}
		
		return propietario;
	}

	@Override
	public Propietario desactivarPropietario(Propietario propietario) throws DatosInvalidosException {
		try {
			
//			this.em.merge(propietario);
			propietario = this.em.find(Propietario.class, propietario.getId());
			propietario.setActivo(false);
			propietario.setHasta(new Date());
			this.em.flush();
		}
		catch (PersistenceException e) {
			throw new DatosInvalidosException("No se pudo grabar los cambios",e);
		}
		
		return propietario;
	}
	
	@Override
	public List<Propietario> obtenerListaTodos() {
		TypedQuery<Propietario> query = this.em.createQuery("SELECT p FROM Propietario p",Propietario.class); 
		List<Propietario> resultado = query.getResultList();
//		resultado.size();
		return resultado;
	}

	@Override
	public Propietario activarPropietario(Propietario propietario) throws DatosInvalidosException {
		try {
			propietario = this.em.find(Propietario.class, propietario.getId());
			propietario.setActivo(true);
			propietario.setDesde(new Date());
			this.em.flush();
		}
		catch (PersistenceException e) {
			throw new DatosInvalidosException("No se pudo grabar los cambios",e);
		}
		
		return propietario;
	}
}
