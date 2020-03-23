package com.servicios;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import com.entidades.Potrero;
import com.entidades.Predio;
import com.entidades.ZonaGeografica;
import com.entidades.ZonaPotrero;
import com.excepciones.DatosInvalidosException;
import com.excepciones.PotrerosException;
import com.interfaces.IZonaGeografica;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

@Stateful
@TransactionManagement(value=TransactionManagementType.BEAN)
public class ZonaGeograficasBeanRemote implements IZonaGeografica {

	private EntityManager em;
	
	private HashMap<Integer,ZonaGeografica> listaExistentes;
	
	private HashMap<Integer,ZonaGeografica> listaDesactivar;

	private HashMap<Integer,ZonaGeografica> listaNuevas;

	private Predio predio = null;
	
	private Date fecha = new Date();
	
	public ZonaGeograficasBeanRemote() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("PDT");
		this.em = emf.createEntityManager();
	}
	
	@PostConstruct
	private void Inicializar() {
		this.listaExistentes = new HashMap<Integer,ZonaGeografica>();
		this.listaNuevas = new HashMap<Integer,ZonaGeografica>();
		this.listaDesactivar = new HashMap<Integer,ZonaGeografica>();
	}

	@Override
	public boolean altaZonaGeografica(ZonaGeografica zonaGeografica) throws PotrerosException {
		if (zonaGeografica.getIdJs() < 0) {
			//si no tiene id o es igual a cero, significa que no existe en la bd
			//Así que la mantengo el la lista de nuevas.
//			zonaGeografica.setDesde(new Date());	//Los desde los seteo todos a la vez al final
			this.listaNuevas.put(zonaGeografica.getIdJs(), zonaGeografica);
			return true;
		}
		//No debería bajar aca
		throw new PotrerosException("ERROR", "ERROR DE ALTA", "El idJs " + zonaGeografica.getIdJs() + " no es válido");
		
		
	}

	@Override
	public boolean editarZonaGeografica(ZonaGeografica zonaEditada) throws PotrerosException {
		if (zonaEditada.getIdJs() == null || zonaEditada.getIdJs() == 0) {
			throw new DatosInvalidosException("El idJs de la Zona no es correcto");
		}
//		System.out.println("Editar zona geografica en bean");
		if (zonaEditada.getIdJs() < 0) {
			//si no tiene id o es menor que cero, significa que no existe en la bd
			//Así que la mantengo en la lista de nuevas.
			this.listaNuevas.put(zonaEditada.getIdJs(), zonaEditada);
			
		}else {
//			System.out.println("es zona ya existente en bd");
			//Mayor que cero...
			//Significa que ya existe en la bd y se modificó algo
			//Como esta implementada la interfez, en el editar no es posible que cambie la forma. solo en alta o borrar se asigna forma.
			//Como se supone que la forma sigue igual, simplemente remplazo la existente con la editada mergeado
			if (zonaEditada.getNombre() == null ) {
				throw new DatosInvalidosException("No es valido el nombre especificado");
			}
			if (zonaEditada.getDescripcion() == null ||  zonaEditada.getDescripcion().equals("")) {
				throw new DatosInvalidosException("La descripcion de la zona " + zonaEditada.getNombre() + " no puede ser vacía.");
			}
			if ( zonaEditada.getTipoZona() == null) {
				throw new DatosInvalidosException("El Tipo de zona de la zona " + zonaEditada.getNombre() + " no puede ser vacío.");
			}
			this.em.merge(zonaEditada);
			this.listaExistentes.put(zonaEditada.getIdJs(), zonaEditada);
		}
		return true;
	}

	@Override
	public boolean borrarZonaGeografica(ZonaGeografica zonaBorrada) throws DatosInvalidosException {
		//nunca va a entrar dos veces aca en un mismo bean para la misma ZonaGeografica
		if (zonaBorrada.getIdJs() == null || zonaBorrada.getIdJs() == 0) {
			throw new DatosInvalidosException("La ZonaGeografica a borrar no tiene IdJs");
		}
		
		
		if (zonaBorrada.getIdJs() < 0) {
			//si no tiene id o es menor que cero, significa que no existe en la bd. Esta en la lista de nuevas
			//y no va a tener zonapotrero asociadas
			this.listaNuevas.remove(zonaBorrada.getIdJs(), zonaBorrada);
			
		}else {
			//Mayor que cero...
			//Significa que ya existe en la bd y se la quiere borrar			
			
			this.listaDesactivar.put(zonaBorrada.getIdJs(), zonaBorrada);
			this.listaExistentes.remove(zonaBorrada.getIdJs());
		}
		
		return true;
	}

	@Override
	public List<ZonaGeografica> obtenerListaTodosActivas(int idPredio) {
		//Se pasa solo el id, porque de cualquier forma hay que buscar el predio
		this.predio = this.em.find(Predio.class, idPredio);
		
		// Aca se obtienen todas las ZonaGeografica activas del predio
		TypedQuery<ZonaGeografica> query = this.em.createQuery("SELECT z FROM ZonaGeografica z where z.activo=:activo and z.predio=:predio",ZonaGeografica.class);
		query.setParameter("predio", this.predio);
		query.setParameter("activo", true);
		List<ZonaGeografica> resultado = query.getResultList();
		for (ZonaGeografica zonaGeografica : resultado) {
			zonaGeografica.setIdJs(zonaGeografica.getId());
			this.listaExistentes.put(zonaGeografica.getIdJs(),zonaGeografica);
		}
		return resultado;
	}

	@Override
	public boolean guardarTodo() throws DatosInvalidosException {
		//La lista de nueva tiene solo id's null o negativos
		this.em.getTransaction().begin();
		
		for (ZonaGeografica zonaBorrada : this.listaDesactivar.values()) {
			//Recorro la lista de zonas que tengo que desactivar
			ZonaGeografica zona = this.em.find(ZonaGeografica.class, zonaBorrada.getId());	//Me desactiva los cambios que le haya hecho si la borro
			this.em.refresh(zona);
			zona.setActivo(false);
			zona.setHasta(fecha);
			this.desactivarZonaPotreros(zona);
			System.out.println("Zona borrada quedó: " + zona.getNombre() + ", " + zona.getActivo() + ", " + zona.getHasta().toString());
		}
		
		try {
			this.em.flush();	//persisto las borradas por si hay nombres nuevos igual a borradas
		}catch (PersistenceException e) {
			this.em.getTransaction().rollback();
			throw new DatosInvalidosException("No se pudo dar el Alta", e);
		}
		
		
		for (ZonaGeografica zona : this.listaNuevas.values()) {
			zona.setPredio(this.predio); 	//Seteo nuevamente el predio por si acaso
			if (zona.getIdJs() == null || zona.getIdJs() == 0 ) {
				this.em.getTransaction().rollback();
				throw new DatosInvalidosException("guardarTodo(): IdJs invalido. No se puede guardar en la base de datos: " + zona.getNombre());
			}else if (zona.getIdJs() < 0 ) {
				//Estas son las nuevas
				if (zona.getNombre() == null  ||  zona.getNombre().equals("")) {
					this.em.getTransaction().rollback();
					throw new DatosInvalidosException("Tienes una zona sin nombre asignado");
				}
				if (zona.getDescripcion() == null  ||  zona.getDescripcion().equals("")) {
					this.em.getTransaction().rollback();
					throw new DatosInvalidosException("La descripcion de la zona " + zona.getNombre() + " no puede ser vacía.");
				}
				if ( zona.getTipoZona() == null) {
					this.em.getTransaction().rollback();
					throw new DatosInvalidosException("El Tipo de zona de la zona " + zona.getNombre() + " no puede ser vacío.");
				}
				
				zona.setActivo(true);
				zona.setDesde(fecha);
				this.crearZonaPotreros(zona);	//Creo las nuevas intersecciones con los potreros existentes
				System.out.println("Zona nueva quedó: " + zona.getNombre() + ", " + zona.getActivo()  + ", "+ zona.getIdJs());
				
				this.em.persist(zona);
				
			}else {
				//getIdJs mayor 0: no debería pasar ya q tendría que estar en la lista de existentes, no en la de nuevas
				this.em.getTransaction().rollback();
				throw new DatosInvalidosException("guardarTodo(): No se puede guardar en la base de datos " + zona.getIdJs() + ": " + zona.getNombre());
			}
		}

		try {
			this.em.getTransaction().commit();
		}catch (PersistenceException e) {
			this.em.getTransaction().rollback();
			throw new DatosInvalidosException("No se pudo dar el Alta", e);
		}
		
		
		
		
		return true;
	}
	
	
	private void crearZonaPotreros(ZonaGeografica nuevaZonaGeografica) {
		System.out.println("Crear zona potreros");
		//Me traigo todos los potreros activos del predio
		//Los intersectados con la ZonaGeografica anterior se desactivaron cuando se borró la ZonaGeografica, x lo que solo tengo que crear las nuevas intersecciones
		TypedQuery<Potrero> query = this.em.createQuery("SELECT p FROM Potrero p where p.activo=:activo and p.predio=:predio",Potrero.class);
		query.setParameter("predio", nuevaZonaGeografica.getPredio());
		query.setParameter("activo", true);
		List<Potrero> listaPotrerosActivos = query.getResultList();
		System.out.println("Hay " + listaPotrerosActivos.size() + " potreros activos en el predio " + nuevaZonaGeografica.getPredio().getNombre());
		for (Potrero potreroActivo : listaPotrerosActivos) {
			if (nuevaZonaGeografica.getForma().intersects(potreroActivo.getForma())) {
				System.out.println("El potrero " + potreroActivo.getNombre() + " SE intersecta con la zonaGeografica " + nuevaZonaGeografica.getNombre());
				//Si el potrero se intersecta con la zonageografica
				Geometry forma = potreroActivo.getForma().intersection(nuevaZonaGeografica.getForma());
				if (forma instanceof MultiPolygon) {
					//Si la intersección  es un multipoligono.
					int cant = forma.getNumGeometries();
					for (int i = 0; i < cant; i++) {
						ZonaPotrero nuevaZonaPotrero = new ZonaPotrero(potreroActivo, nuevaZonaGeografica, new Date(), true, (Polygon)forma.getGeometryN(i));
						nuevaZonaGeografica.addZonaPotrero(nuevaZonaPotrero);
						this.em.persist(nuevaZonaPotrero);
					}
					
				}else if (forma instanceof Polygon) {
					ZonaPotrero nuevaZonaPotrero = new ZonaPotrero(potreroActivo, nuevaZonaGeografica, this.fecha, true, (Polygon)forma);
					nuevaZonaGeografica.addZonaPotrero(nuevaZonaPotrero);
					this.em.persist(nuevaZonaPotrero);
				}
				
			}else{
				System.out.println("El potrero " + potreroActivo.getNombre() + " no se intersecta con la zonaGeografica " + nuevaZonaGeografica.getNombre());
			}
		}
	}


	private void desactivarZonaPotreros(ZonaGeografica zonaGeograficaDesactivada) {
		// Me traigo todas las ZonaPotrero activas de la ZonaGeografica
		System.out.println("Desactivar zonapotreros");
		TypedQuery<ZonaPotrero> query = this.em.createQuery("SELECT zp FROM ZonaPotrero zp where zp.activo=:activo and zp.zonaGeografica=:zonaGeografica",ZonaPotrero.class);
		query.setParameter("zonaGeografica", zonaGeograficaDesactivada);
		query.setParameter("activo", true);
		List<ZonaPotrero> listaZonaPotreroActivas = query.getResultList();
		for (ZonaPotrero zonaPotrero : listaZonaPotreroActivas) {
			//si la ZonaPotrero activa se intersecta con el área de la viejaZonaGeografica
			zonaPotrero.setActivo(false);
			zonaPotrero.setHasta(this.fecha);  
		}
	}

	@Override
	@Remove
	public boolean finalizarBean() {

		return true;
	}

}
