package com.servicios;

import java.util.ArrayList;
import java.util.Date;
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
import com.entidades.Predio;
import com.entidades.Propietario;
import com.entidades.ZonaGeografica;
import com.excepciones.DatosInvalidosException;
import com.interfaces.IPredios;


@Stateless
@Remote
public class PrediosBeanRemote implements IPredios {

	@PersistenceContext
	private EntityManager em;
	
	
    public PrediosBeanRemote() {
        
    }

	@Override
	public Predio altaPredio(Predio predio) throws DatosInvalidosException {
		try {
			predio.setActivo(true);
			predio.setDesde(new Date());
			
			//Tenemos que reemplazar los roles con Roles traidos de la bd, 
			//porque hacer un loop y merge a los roles no funcion�.
			Set<Propietario> propietariosQueTiene = predio.getPropietarios();
			predio.setPropietarios(new HashSet<Propietario>());
			//mergear los roles
			for (Propietario propietarioQueTiene : propietariosQueTiene) {
				Propietario propietarioBd = this.em.find(Propietario.class, propietarioQueTiene.getId());
				predio.addPropietario(propietarioBd);
			}
			this.em.flush();
			this.em.persist(predio);
			this.em.flush();
		}
		catch (PersistenceException e) {
			throw new DatosInvalidosException("No se pudo dar el Alta", e);
			
		}
		
		return predio;
	}

	@Override
	public Predio editarPredio(Predio predio) throws DatosInvalidosException {
		try {
			this.em.merge(predio);
			this.em.flush();
		}
		catch (PersistenceException e) {
			throw new DatosInvalidosException("No se pudo grabar los cambios", e);
		}
		
		return predio;
	}

	@Override
	public Predio borrarPredio(Predio predio) throws DatosInvalidosException {
		//TODO no se borra. pasa a inactivo.
		try {
//			this.em.merge(predio);
			predio = this.em.find(Predio.class, predio.getId());
			boolean sinPotreros = predio.getPotreros().isEmpty();
			boolean sinZonas = predio.getZonas().isEmpty();
			if (sinPotreros && sinZonas) {
				//Quito todos los propietarios
				for (Propietario propietario : predio.getPropietarios()) {
					predio.removePropietario(propietario);
				}
				this.em.remove(predio);
				this.em.flush();
				return null;
			}else {
				predio.setActivo(false);
				predio.setHasta(new Date());
				this.em.flush();
				return predio;
			}
			
		}
		catch (PersistenceException e) {
			throw new DatosInvalidosException("No se pudo grabar los cambios", e);
		}
		
	}
	
	@Override
	public List<Predio> obtenerListaTodos() {
		TypedQuery<Predio> query = this.em.createQuery("SELECT p FROM Predio p",Predio.class); 
//		query.setParameter("activo", true);
		List<Predio> resultado = query.getResultList();
		return resultado;
	}

	@Override
	public boolean formaEditable(int idPredio) {
		Predio predio = this.em.find(Predio.class, idPredio);
		TypedQuery<Long> query = this.em.createQuery("SELECT count(1) FROM ZonaGeografica z where z.activo=:activo and z.predio=:predio",Long.class);
		query.setParameter("predio", predio);
		query.setParameter("activo", true);
		Long cantidadZonasActivas = query.getSingleResult();	//Obtengo la cantidad de ZonaGeograficas activas
		
		query = this.em.createQuery("SELECT count(1) FROM Potrero p where p.activo=:activo and p.predio=:predio",Long.class);
		query.setParameter("predio", predio);
		query.setParameter("activo", true);
		Long cantidadPotrerosActivos = query.getSingleResult();	//Obtengo la cantidad de Potreros activas
		
		//Solo se podr� editar la forma si est� activo, no tiene potreros ni zonas geogr�ficas activas.
		return predio.getActivo() && cantidadPotrerosActivos == 0 && cantidadZonasActivas == 0;
	}

	@Override
	public Predio activarPredio(Predio predio) throws DatosInvalidosException {
		try {
			predio = this.em.find(Predio.class, predio.getId());
			
			predio.setActivo(true);
			predio.setHasta(new Date());
			
			this.em.flush();
			
			return predio;
		}
		catch (PersistenceException e) {
			throw new DatosInvalidosException("No se pudo grabar los cambios", e);
		}
	}

	@Override
	public boolean existeNombre(String nombre) {
		TypedQuery<Predio> query = this.em.createQuery("SELECT p FROM Predio p WHERE p.nombre=:nombre",Predio.class); 
		query.setParameter("nombre", nombre);
		List<Predio> resultado = new ArrayList<Predio>();
		try {
			resultado = query.getResultList();
		}catch (NoResultException e) {
			//Si no hay resultado con ese nombre
			return false;
		}
		
		if (resultado.isEmpty()) {
			return false;
		}
		return true;
	}
}
