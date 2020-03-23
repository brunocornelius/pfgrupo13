package com.servicios;

import java.util.List;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import com.entidades.Indicador;
import com.entidades.Predio;
import com.excepciones.DatosInvalidosException;
import com.excepciones.NoExisteElementoException;
import com.interfaces.IIndicadors;


@Stateless
@Remote
public class IndicadorsBeanRemote implements IIndicadors {

	@PersistenceContext
	private EntityManager em;
	
	
    public IndicadorsBeanRemote() {
        
    }


	@Override
	public Indicador altaIndicador(Indicador indicador) throws DatosInvalidosException {
		try {
			this.em.persist(indicador);
			this.em.flush();
		}
		catch (PersistenceException e) {
			throw new DatosInvalidosException("No se pudo dar el Alta",e);
		}
		
		//Aca tz ya esta actualizada con el id
		return indicador;
	}


	@Override
	public Indicador editarIndicador(Indicador indicador) throws DatosInvalidosException {
		try {
			this.em.merge(indicador);
			this.em.flush();
		}
		catch (PersistenceException e) {
			throw new DatosInvalidosException("No se pudo grabar los cambios",e);
		}
		
		return indicador;
	}


	@Override
	public boolean borrarIndicador(Integer id) throws DatosInvalidosException {
		try {
			Indicador indicador = this.em.find(Indicador.class, id);
			this.em.remove(indicador);
			this.em.flush();
		}
		catch (PersistenceException e) {
			throw new DatosInvalidosException("No se pudo grabar los cambios",e);
		}
		
		return true;
	}


	@Override
	public List<Indicador> obtenerListaTodos() {
		//La consulta solo me tiene que devolver los indicadores que no tienen hijos. TODO en otro metodo?
		TypedQuery<Indicador> query = this.em.createQuery("SELECT i FROM Indicador i",Indicador.class); 
		List<Indicador> resultado = query.getResultList();
		return resultado;
	}


	@Override
	public Indicador obtenerIndicador(String nombre) throws NoExisteElementoException, DatosInvalidosException {
		//La consulta solo me tiene que devolver los indicadores que no tienen hijos. TODO en otro metodo?
		TypedQuery<Indicador> query = this.em.createQuery("SELECT i FROM Indicador i where i.nombre=:nombre",Indicador.class); 
		query.setParameter("nombre", nombre);
		Indicador  resultado = null;
		try {
			resultado = query.getSingleResult();
		}catch(NoResultException e){
			throw new NoExisteElementoException("No existe un indicador raiz",e);
		}catch(NonUniqueResultException e){
			throw new DatosInvalidosException("Existe mas de un indicador raíz",e);
		}
		return resultado;
	}


	@Override
	public List<Indicador> obtenerListaSinHijosActivos() {
		//La consulta solo me tiene que devolver los indicadores que no tienen hijos. TODO en otro metodo?
		TypedQuery<Indicador> query = this.em.createQuery("SELECT i FROM Indicador i WHERE i.hijos IS EMPTY",Indicador.class); 
		List<Indicador> resultado = query.getResultList();
		return resultado;
	}


	@Override
	public Indicador obtenerIndicadorParaPredio(String nombre, int idPredio) throws NoExisteElementoException, DatosInvalidosException {
		//La consulta solo me tiene que devolver los indicadores que no tienen hijos. TODO en otro metodo?
		TypedQuery<Indicador> query = this.em.createQuery("SELECT i FROM Indicador i where i.nombre=:nombre ",Indicador.class); 
		query.setParameter("nombre", nombre);
		Indicador  resultado = null;
		Predio predio = null;
		try {
			resultado = query.getSingleResult();
			resultado.getHijos().size();
			predio = this.em.find(Predio.class, idPredio);
			predio.getPotreros().size();
		}catch(NoResultException e){
			throw new NoExisteElementoException("No existe un indicador raiz",e);
		}catch(NonUniqueResultException e){
			throw new DatosInvalidosException("Existe mas de un indicador raíz",e);
		}
		
		predio.cargarAreaIndicador(resultado);
		return resultado;
	}
	

	
}
