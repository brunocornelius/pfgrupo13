package com.escritorio.entidadesJS;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import com.escritorio.controladores.ListaZonaGeograficasController;
import com.escritorio.entidadesFx.PredioFx;
import com.escritorio.entidadesFx.ZonaGeograficaFx;
import com.excepciones.DatosInvalidosException;
import com.excepciones.PotrerosException;
import com.util.CadenasUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.TopologyException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert.AlertType;
import netscape.javascript.JSObject;

public class ZonaGeograficasJsDriver {

	
	
    private PredioFx predioFx;
    
    private ListaZonaGeograficasController fxController;
    
    private int siguienteId = 0;
	
	public ZonaGeograficasJsDriver( ListaZonaGeograficasController fxc) { 
		this.fxController = fxc;
	}

	public PredioFx getPredioFx() {
		return predioFx;
	}

	public void setPredioFx(PredioFx predioFx) throws NamingException {
		this.predioFx = predioFx;
		
	}
	
	private int getNuevoId() {
		this.siguienteId--;
		return this.siguienteId;
	}
	
	public int addZonaGeograficaRestante() throws DatosInvalidosException {
		int cant = 0;
		//Tomar la forma del predio y intersectarlo con todas las zonas. el resultado es la zonaResto
		if (this.fxController.getZonaGeograficasData().isEmpty()) {
			//Si aún no hay zonas.... crear nueva zona con la forma del predio completa
			Polygon formaPredio = (Polygon) this.predioFx.getPredio().getForma().clone();
			ZonaGeograficaFx zonaRestoFx = new ZonaGeograficaFx();
			zonaRestoFx.getZonaGeografica().setForma(formaPredio);
			this.agregarDibujoAJsConConfirmar(zonaRestoFx);
			cant++;
		}else {
			//Si ya hay zonasgeograficas creadas...
			//Obtengo la suma de las zonas.
			Geometry zonasSumadas = null;	//Variable para sumar todas las zonas existentes
			for (ZonaGeograficaFx zonaExistente : this.fxController.getZonaGeograficasData()) {
				if (zonasSumadas == null) {
					zonasSumadas = zonaExistente.getZonaGeografica().getForma();
				}else {
					zonasSumadas = zonasSumadas.union(zonaExistente.getZonaGeografica().getForma());
				}
			}
			
			if (zonasSumadas instanceof Polygon || zonasSumadas instanceof MultiPolygon) {
				System.out.println("Zonas sumadas es un MultiPolygon o poligono");
				Geometry zonaRestoSinInterseccion = null;
				try {
					zonaRestoSinInterseccion = this.predioFx.getPredio().getForma().difference(zonasSumadas);	//Si no funciona con difference, tomar la interseccion y restar solo la intersección.
				}catch (TopologyException e){
					System.out.println("TopologyException en resto");
					throw new DatosInvalidosException("TopologyException. No se puede crear la ZonaGeografica Restante, no está soportada la forma resultante");
				}
			
				boolean resultado = true;

				if(zonaRestoSinInterseccion instanceof Polygon) {
					int huecos = ((Polygon) zonaRestoSinInterseccion).getNumInteriorRing();
					if (huecos > 0) {
						throw new DatosInvalidosException("No se puede crear la ZonaGeogrfica del resto, la forma resultante tiene " + huecos + " hueco/s. No está soportado");
					}
					System.out.println("zonaRestoSinInterseccion es un poligono");
					ZonaGeograficaFx zonaRestoFx = new ZonaGeograficaFx();
					zonaRestoFx.getZonaGeografica().setForma((Polygon) zonaRestoSinInterseccion);
					resultado = this.agregarDibujoAJsConConfirmar(zonaRestoFx);
					if (!resultado) {
						throw new DatosInvalidosException("No se puede crear la ZonaGeografica Restante, no está soportada la forma resultante");
					}
				}else if (zonaRestoSinInterseccion instanceof MultiPolygon) {
					System.out.println("zonaRestoSinInterseccion es un MultiPolygon");
					int cantPoligonos = zonaRestoSinInterseccion.getNumGeometries();
					for (int i = 0; i < cantPoligonos; i++) {
						Polygon poligonoActual = (Polygon) zonaRestoSinInterseccion.getGeometryN(i);
						ZonaGeograficaFx zonaRestoFx = new ZonaGeograficaFx();
						zonaRestoFx.getZonaGeografica().setForma((Polygon) poligonoActual);
						resultado = this.agregarDibujoAJsConConfirmar(zonaRestoFx);
						if (resultado) {
							cant++;
							System.out.println("Se altó zona resto");
						}
					}
				}
				
			}else if (zonasSumadas instanceof GeometryCollection) {
				System.out.println("Zonas sumadas es una GeometryColletion");
				throw new DatosInvalidosException("No se puede crear la Zona geogrfica Restante, Zona geogrfica sumadas es una GeometryColletion. No está soportado");
			}
		}
		return cant;
	}
	
	public void addZonaGeografica(JSObject puntosJs, int largo) {
//		System.out.println("Js llamo a addZonaGeografica " + puntosJs);
		ZonaGeograficaFx zonaFx = new ZonaGeograficaFx();	//Creo la zonaFx vacía
		zonaFx.setForma(puntosJs, largo);
//		zonaFx.setArea(area);
		this.agregarZonaGeograficaChequeandoInterseccionPredio(zonaFx);
		
	}
	
	public void agregarZonaGeograficaChequeandoInterseccionPredio(ZonaGeograficaFx zonaFx) {
		//Tomar el pedazo de zona que está dentro del predio
		if (zonaFx.getZonaGeografica().getForma().intersects(this.predioFx.getPredio().getForma())) {
			//Si la zonaFx recibida se intersecta con el predio
			Geometry formaInterseccion = zonaFx.getZonaGeografica().getForma().intersection(this.predioFx.getPredio().getForma());
			
			if (formaInterseccion instanceof Polygon) {
				System.out.println("Intersección con el predio resultó en un Polygon");
				zonaFx.getZonaGeografica().setForma((Polygon) formaInterseccion);	//seteo la nueva forma
				this.agregarFormaAJsSinInterseccionZonas(zonaFx);	//Pasa para que se agregue chequiando interseccion con las demas zonas
			}else if (formaInterseccion instanceof MultiPolygon) {
				System.out.println("Intersección con el predio resultó en un MultiPolygon");
				MultiPolygon interseccionMultiPolygon = (MultiPolygon) formaInterseccion;
				int cantPoligonos = interseccionMultiPolygon.getNumGeometries();
				for (int i = 0; i < cantPoligonos; i++) {
					Polygon poligonoActual = (Polygon) interseccionMultiPolygon.getGeometryN(i);
					//Creo una nueva zona geografica para cada poligono
					ZonaGeograficaFx nuevaZonaFx = new ZonaGeograficaFx();
					nuevaZonaFx.getZonaGeografica().setForma(poligonoActual);
					this.agregarFormaAJsSinInterseccionZonas(nuevaZonaFx);
				}
				
			}else if (formaInterseccion instanceof GeometryCollection) {
				System.out.println("Intersección con el predio resultó en una GeometryCollection");
				//TODO terminar implementación
//				GeometryCollection interseccionCollection = (GeometryCollection) formaInterseccion;
				
			}
		}else {
			//La zona recibida no se intersecta con el predio
			this.mostrarMensaje("La zona recibida no se intersecta con el predio");
		}
		
	}
	
	public void mostrarMensaje(String s) {
		System.out.println(s);
	}
	
	private void agregarFormaAJsSinInterseccionZonas(ZonaGeograficaFx zonaFx) {
		System.out.println("agregarFormaAJsSinInterseccionZonas");
//		GeometryFactory gf = new GeometryFactory();
		
		if (this.fxController.getZonaGeograficasData().isEmpty()) {
			System.out.println("Aún no hay zonas en la lista");
			//Si no hay ZonaGeograficaFx aún en la lista...
			this.agregarDibujoAJsConConfirmar(zonaFx);
			
		}else {
			System.out.println("Ya hay zonas en la lista");
			//Ya hay zonas en la lista
			Geometry zonasSumadas = null;	//Variable para sumar todas las zonas existentes
			System.out.println("Armemos la zonaSumada");
			for (ZonaGeograficaFx zonaExistente : this.fxController.getZonaGeograficasData()) {
				System.out.println(zonaExistente.getNombre());
				if (zonasSumadas == null) {
					zonasSumadas = zonaExistente.getZonaGeografica().getForma();
				}else {
					zonasSumadas = zonasSumadas.union(zonaExistente.getZonaGeografica().getForma());
				}
			}
			System.out.println("Ya sume todas las zonas existentes");
			if (zonasSumadas instanceof Polygon || zonasSumadas instanceof MultiPolygon) {
				if (zonasSumadas instanceof Polygon) {
					System.out.println("Zonas sumadas es un Polygon");
				}
				if (zonasSumadas instanceof MultiPolygon) {
					System.out.println("Zonas sumadas es un MultiPolygon");
				}
				
				Geometry zonaSinInterseccion = zonaFx.getZonaGeografica().getForma().difference(zonasSumadas);	//Si no funciona con difference, tomar la interseccion y restar solo la intersección.
				
				if (zonaSinInterseccion instanceof Polygon) {
					System.out.println("zonaSinInterseccion es un Polygon");
				}
				if (zonaSinInterseccion instanceof MultiPolygon) {
					System.out.println("zonaSinInterseccion es un MultiPolygon");
				}
				
				if (zonaSinInterseccion instanceof MultiPolygon) {
					System.out.println("La interseccion con las zonas existentes resultó en un MultiPoligono");
					int cantPoligonos = zonaSinInterseccion.getNumGeometries();
					for (int i = 0; i < cantPoligonos; i++) {
						Polygon poligonoActual = (Polygon) zonaSinInterseccion.getGeometryN(i);
						//Creo una nueva zona geografica para cada poligono
						ZonaGeograficaFx nuevaZonaFx = new ZonaGeograficaFx();
						nuevaZonaFx.getZonaGeografica().setForma(poligonoActual);
						this.agregarDibujoAJsConConfirmar(nuevaZonaFx);
					}
				}else if(zonaSinInterseccion instanceof Polygon) {
					//Si la intersección con las demas zonas resulta en un poligono
					System.out.println("La intersección con las zonas existentes resultó en un Poligono");
					zonaFx.getZonaGeografica().setForma((Polygon) zonaSinInterseccion);
					this.agregarDibujoAJsConConfirmar(zonaFx);
				}
			}else if (zonasSumadas instanceof GeometryCollection) {
				System.out.println("Zonas sumadas es una GeometryColletion");
			}else {
				System.out.println("Ni GeometryCollection ni poligonos");
			}
		}
	}
	
	private boolean agregarDibujoAJsConConfirmar(ZonaGeograficaFx zonaFx) {
		Polygon forma = zonaFx.getZonaGeografica().getForma();
		
		if (zonaFx.getZonaGeografica().getForma().getNumInteriorRing() != 0) {
			System.out.println("La forma tiene un hueco, no esta soportado");
			return false;
		}
		
		double area = forma.getArea()*1000000;
		if ( area<= 0.04) {
			System.out.println("El area " + area + " es menor o igual a 4");
			return false;
		}
		
		
		Coordinate[] coordenadas = forma.getCoordinates(); 

		int cant = coordenadas.length;
		List<Coordinate> listaCoordenadas = new ArrayList<Coordinate>();
		for (int i = 0; i < coordenadas.length; i++) {
			listaCoordenadas.add(i, coordenadas[i]);
		}
		if (cant<4) {
			//no puede existir un poligono con 3 coordenadas ya que la primera se repite con la ultima
			return false; 
		}
		
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 3785);
		
		if (cant>=4) {
			System.out.println("La forma tiene " + cant + " coordenadas");
			//Si tiene mas de 3 lados
			for (int i = 1; i < cant - 1; i++) {
				System.out.println("Coordenada " + i);
				Coordinate[] coords = {coordenadas[i-1],coordenadas[i],coordenadas[i+1],coordenadas[i-1]};
				Polygon temp = geometryFactory.createPolygon(coords);
				System.out.println(temp);
				area = temp.getArea()*1000000;
				System.out.println("Area temp: " + area  + " m2");
				if (area<= 0.01) {
					System.out.println("Area DEMASIADO PEQUEÑA ");
					listaCoordenadas.remove(coordenadas[i]);
				}
			}
			//ahora verificar el ultimo con el penultimo, el ultimo (o primero), y el segundo
			Coordinate[] coords = {coordenadas[cant-2],coordenadas[cant -1],coordenadas[1],coordenadas[cant-2]};
			Polygon temp = geometryFactory.createPolygon(coords);
			System.out.println(temp);
			area = temp.getArea()*1000000;
			System.out.println("Area temp: " + area  + " m2");
			if (area<= 0.01) {
				System.out.println("Area DEMASIADO PEQUEÑA en inicio fin");
				listaCoordenadas.remove(coordenadas[cant-1]);	//Saco la última coordenada, pero también tengo que sacar la primera
				listaCoordenadas.remove(coordenadas[0]);		//Saco la primera porque es igual a la última
				//Ahora tengo que hacer que le primer y ultima coordenada de listaCoordenadas se repitan
				listaCoordenadas.add((Coordinate) listaCoordenadas.get(0).clone());	//Clono la primer coordenada a la última posicion
			}
			
		}
		System.out.println("Se terminaron los controles. Quedaron " + listaCoordenadas.size() + " coordenadas");
		Coordinate[] nuevasCoordenadas = new Coordinate[listaCoordenadas.size()] ;
		for (int i = 0; i < listaCoordenadas.size(); i++) {
			System.out.println(listaCoordenadas.get(i).toString());
			nuevasCoordenadas[i] = listaCoordenadas.get(i);
			System.out.println(i);
		}
	
		
		System.out.println("Se cargaron nuevas coordenadas");
		Polygon nuevaForma = geometryFactory.createPolygon(nuevasCoordenadas);
		System.out.println("Se creó nuevo poligono");
		zonaFx.getZonaGeografica().setForma(nuevaForma);

		zonaFx.setIdJs(this.getNuevoId());
		zonaFx.setNombre("Zona" + zonaFx.getIdJs());
		agregarBotonBorrar(zonaFx);
		this.confirmarFormaJs(zonaFx);
		JSObject window = (JSObject) this.fxController.getWebViewEngine().executeScript("window");
		window.setMember("ultimaZona", zonaFx);
		fxController.getWebViewEngine().executeScript("dibujarZona()");
		
		System.out.println("Area " + zonaFx.getNombre() + ": " + zonaFx.getZonaGeografica().getAreaEnHectareas() + " ha");
		window.removeMember("ultimaZona");
		zonaFx.setArea(zonaFx.getZonaGeografica().getAreaEnHectareas());
		fxController.getZonaGeograficasData().add(zonaFx);			
		return true;
	}
	
	private void borrarFormaDeJs(ZonaGeograficaFx zonaFx) {		
		fxController.getWebViewEngine().executeScript("borrarZona("+  zonaFx.getIdJs()  +")");	//Borro de javascript
		fxController.getZonaGeograficasData().remove(zonaFx);									//Borro de la tabla
	}
	
	private void confirmarFormaJs(ZonaGeograficaFx zonaFx) {
		try {
			fxController.getZonaGeograficaClient().altaZonaGeografica(zonaFx);	//Agrego la zona al bean con estado
			agregarBotonBorrar(zonaFx);				//cambiar la accion del boton borrar
//			zonaFx.getBotonOk().setVisible(false);	//Oculto el botón confirmar.
			
		} catch (PotrerosException e) {
			System.out.println("No se pudo informar del alta de zona al servidor");
			//Borro la forma del mapa y el elemento de la tabla
			borrarFormaDeJs(zonaFx);
			fxController.getZonaGeograficasData().remove(zonaFx);
//			fxController.getMainApp().mostrarAlerta(e, AlertType.WARNING);
		}
	}
	
	private void agregarBotonBorrar(ZonaGeograficaFx zonaFx) {
		zonaFx.getBotonBorrar().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Apretaste boton borrar");
				int indice = fxController.getZonaGeograficasTable().getSelectionModel().getSelectedIndex();
				try {
					fxController.getZonaGeograficaClient().borrarZonaGeografica(zonaFx);
					//Solo si los anteriores no dan excepcion
					fxController.getZonaGeograficasData().remove(zonaFx);	//Borro de la lista/tabla
					borrarFormaDeJs(zonaFx);								//Borro del bean
					fxController.getMainApp().mostrarAlerta("Zonas geograficas","Borrar zona", "Se borró la zona " + zonaFx.getNombre(),AlertType.INFORMATION);
				} catch (PotrerosException e) {
					System.out.println("Error al conectar con el bean");
//					fxController.getZonaGeograficasData().add(indice,zonaFx);	//no es necesaria porque la borro de la tabla solo si no salta excepcion
					fxController.getMainApp().mostrarAlerta(e, AlertType.ERROR);
				}
			}
		});
	}
	
	public void agregarZonasExistentesPredio() {
		//Tomar la lista de zonaFx y dibujarlas todas con sus correspondientes IDs
		JSObject window = (JSObject) this.fxController.getWebViewEngine().executeScript("window");
		for (ZonaGeograficaFx zonaFx : this.fxController.getZonaGeograficasData()) {
			agregarBotonBorrar(zonaFx);
			window.setMember("ultimaZona", zonaFx);
			System.out.println("");
			fxController.getWebViewEngine().executeScript("dibujarZona()");
			System.out.println("");
			window.removeMember("ultimaZona");
			int id = CadenasUtil.getNumeros(zonaFx.getNombre()) * -1;
//			System.out.println(potreroFx.getNombre() + ": " + id);
			if (id < this.siguienteId) {
				this.siguienteId = id;
			}
		}
	}
	
	public void actualizarTooltip(ZonaGeograficaFx zonaFx) {
		String metodo = "actualizarTooltip(" + zonaFx.getIdJs() + ",'" + zonaFx.toString() + "')";
		System.out.println(metodo);
		fxController.getWebViewEngine().executeScript(metodo);
	}
	
	public void actualizarColor(ZonaGeograficaFx zonaFx) {
		String metodo = "actualizarColor(" + zonaFx.getIdJs() + ",'" + zonaFx.getTipoZona().getColor() + "')";
		System.out.println(metodo);
		fxController.getWebViewEngine().executeScript(metodo);
	}
}
