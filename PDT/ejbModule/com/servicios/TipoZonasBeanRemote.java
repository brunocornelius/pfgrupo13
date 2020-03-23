package com.servicios;

import java.util.List;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import com.entidades.TipoZona;
import com.excepciones.DatosInvalidosException;
import com.interfaces.ITipoZonas;


@Stateless
@Remote
public class TipoZonasBeanRemote implements ITipoZonas {

	@PersistenceContext
	private EntityManager em;
	
	
    public TipoZonasBeanRemote() {
        
    }


	@Override
	public TipoZona altaTipoZona(TipoZona tz) throws DatosInvalidosException {
		try {
			this.em.persist(tz);
			this.em.flush();
		}
		catch (PersistenceException e) {
			throw new DatosInvalidosException("No se pudo dar el Alta",e);
		}
		
		//Aca tz ya esta actualizada con el id
		return tz;
	}


	@Override
	public TipoZona editarTipoZona(TipoZona tz) throws DatosInvalidosException {
		try {
			this.em.merge(tz);
			this.em.flush();
		}
		catch (PersistenceException e) {
			throw new DatosInvalidosException("No se pudo grabar los cambios",e);
		}
		
		return tz;
	}


	@Override
	public boolean borrarTipoZona(Integer id) throws DatosInvalidosException {
		TipoZona tipoZona = this.em.find(TipoZona.class, id);
		try {
			
			this.em.remove(tipoZona);
			this.em.flush();
		}
		catch (PersistenceException e) {
			throw new DatosInvalidosException("No se puede borrar el tipo de zona "+ tipoZona.getNombre() + ". Está siendo utilizado ",e);
		}
		
		return true;
	}


	@Override
	public List<TipoZona> obtenerListaTodos() {
		TypedQuery<TipoZona> query = this.em.createQuery("SELECT tz FROM TipoZona tz",TipoZona.class); 
		List<TipoZona> resultado = query.getResultList();
//		resultado.size();
		return resultado;
	}

	
}
