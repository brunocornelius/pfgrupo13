package com.servicios;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.entidades.Predio;
import com.entidades.ZonaGeografica;
import com.entidades.ZonaPotrero;
import com.interfaces.IZonaPotrero;

/**
 * Session Bean implementation class ZonaPotrerosBeanRemote
 */
@Stateless
@LocalBean
public class ZonaPotrerosBeanRemote implements IZonaPotrero {

	@PersistenceContext
	private EntityManager em;
	
    public ZonaPotrerosBeanRemote() {
        
    }

	@Override
	public List<ZonaPotrero> obtenerListaTodosActivas(int idPredio) {
		//Se pasa solo el id, porque de cualquier forma hay que buscar el predio
		Predio predio = this.em.find(Predio.class, idPredio);
		
		// Aca se obtienen todas las ZonaGeografica activas del predio
		TypedQuery<ZonaPotrero> query = this.em.createQuery("SELECT z FROM ZonaPotrero z JOIN z.potrero p where z.activo=:activo and p.predio=:predio",ZonaPotrero.class);
		query.setParameter("predio", predio);
		query.setParameter("activo", true);
		List<ZonaPotrero> resultado = query.getResultList();

		return resultado;
	}

}
