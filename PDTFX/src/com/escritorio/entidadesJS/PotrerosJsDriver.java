package com.escritorio.entidadesJS;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import com.entidades.ZonaPotrero;
import com.escritorio.controladores.ListaPotrerosController;
import com.escritorio.entidadesFx.PredioFx;
import com.escritorio.entidadesFx.ZonaGeograficaFx;
import com.escritorio.entidadesFx.PotreroFx;
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

public class PotrerosJsDriver {
	
    private PredioFx predioFx;
    
    private ListaPotrerosController fxController;
    
    private int siguienteId = 0;
	
	public PotrerosJsDriver( ListaPotrerosController fxc) { 
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
	
	public void addPotreroRestante() throws DatosInvalidosException {
		//Tomar la forma del predio y intersectarlo con todas las potreros. el resultado es la potreroResto
		if (this.fxController.getPotrerosData().isEmpty()) {
			//Si aún no hay potreros.... crear nueva potrero con la forma del predio completa
			Polygon formaPredio = (Polygon) this.predioFx.getPredio().getForma().clone();
			PotreroFx potreroRestoFx = new PotreroFx();
			potreroRestoFx.getPotrero().setForma(formaPredio);
			this.agregarDibujoAJsConConfirmar(potreroRestoFx);
		}else {
			//Si ya hay potrerosgeograficas creadas...
			//Obtengo la suma de las potreros.
			Geometry potrerosSumadas = null;	//Variable para sumar todas las potreros existentes
			for (PotreroFx potreroExistente : this.fxController.getPotrerosData()) {
				if (potrerosSumadas == null) {
					potrerosSumadas = potreroExistente.getPotrero().getForma();
				}else {
					potrerosSumadas = potrerosSumadas.union(potreroExistente.getPotrero().getForma());
				}
			}
			
			if (potrerosSumadas instanceof Polygon || potrerosSumadas instanceof MultiPolygon) {
				System.out.println("Potreros sumadas es un MultiPolygon o poligono");
				
				Geometry potreroRestoSinInterseccion = null;
				try {
					potreroRestoSinInterseccion = this.predioFx.getPredio().getForma().difference(potrerosSumadas);	//Si no funciona con difference, tomar la interseccion y restar solo la intersección.
				}catch (TopologyException e){
					System.out.println("TopologyException en resto");
					throw new DatosInvalidosException("No se puede crear el potrero del resto, no está soportada la forma resultante");
				}
				
				boolean resultado = true;
				if(potreroRestoSinInterseccion instanceof Polygon) {
					System.out.println("potreroRestoSinInterseccion es un poligono");
					int huecos = ((Polygon) potreroRestoSinInterseccion).getNumInteriorRing();
					if (huecos > 0) {
						throw new DatosInvalidosException("No se puede crear el Potrero del resto, la forma resultante tiene " + huecos + " hueco/s. No está soportado");
					}
					PotreroFx potreroRestoFx = new PotreroFx();
					potreroRestoFx.getPotrero().setForma((Polygon) potreroRestoSinInterseccion);
					
					resultado = resultado & this.agregarDibujoAJsConConfirmar(potreroRestoFx);
				}else if (potreroRestoSinInterseccion instanceof MultiPolygon) {
					System.out.println("potreroRestoSinInterseccion es un MultiPolygon");
					int cantPoligonos = potreroRestoSinInterseccion.getNumGeometries();
					for (int i = 0; i < cantPoligonos; i++) {
						Polygon poligonoActual = (Polygon) potreroRestoSinInterseccion.getGeometryN(i);
						PotreroFx potreroRestoFx = new PotreroFx();
						potreroRestoFx.getPotrero().setForma((Polygon) poligonoActual);
						resultado = resultado & this.agregarDibujoAJsConConfirmar(potreroRestoFx);
					}
				}
			}else if (potrerosSumadas instanceof GeometryCollection) {
				System.out.println("Potreros sumadas es una GeometryColletion");
				throw new DatosInvalidosException("No se puede crear el potrero Restante, Potreros sumadas es una GeometryColletion. No está soportado");
			}
		}
		
	}
	
	public void addPotrero(JSObject puntosJs, int largo) {
//		System.out.println("Js llamo a addPotrero " + puntosJs);
		PotreroFx potreroFx = new PotreroFx();	//Creo la potreroFx vacía
		potreroFx.setForma(puntosJs, largo);
//		potreroFx.setArea(area);
		this.agregarPotreroChequeandoInterseccionPredio(potreroFx);
		
	}
	
	public void agregarPotreroChequeandoInterseccionPredio(PotreroFx potreroFx) {
		//Tomar el pedazo de potrero que está dentro del predio
		if (potreroFx.getPotrero().getForma().intersects(this.predioFx.getPredio().getForma())) {
			//Si la potreroFx recibida se intersecta con el predio
			Geometry formaInterseccion = potreroFx.getPotrero().getForma().intersection(this.predioFx.getPredio().getForma());
			
			if (formaInterseccion instanceof Polygon) {
				System.out.println("Intersección con el predio resultó en un Polygon");
				potreroFx.getPotrero().setForma((Polygon) formaInterseccion);	//seteo la nueva forma
				this.agregarFormaAJsSinInterseccionPotreros(potreroFx);	//Pasa para que se agregue chequiando interseccion con las demas potreros
			}else if (formaInterseccion instanceof MultiPolygon) {
				System.out.println("Intersección con el predio resultó en un MultiPolygon");
				MultiPolygon interseccionMultiPolygon = (MultiPolygon) formaInterseccion;
				int cantPoligonos = interseccionMultiPolygon.getNumGeometries();
				for (int i = 0; i < cantPoligonos; i++) {
					Polygon poligonoActual = (Polygon) interseccionMultiPolygon.getGeometryN(i);
					//Creo una nueva potrero geografica para cada poligono
					PotreroFx nuevaPotreroFx = new PotreroFx();
					nuevaPotreroFx.getPotrero().setForma(poligonoActual);
					this.agregarFormaAJsSinInterseccionPotreros(nuevaPotreroFx);
				}
				
			}else if (formaInterseccion instanceof GeometryCollection) {
				System.out.println("Intersección con el predio resultó en una GeometryCollection");
				//TODO terminar implementación
//				GeometryCollection interseccionCollection = (GeometryCollection) formaInterseccion;
				
			}
		}else {
			//La potrero recibida no se intersecta con el predio
			this.mostrarMensaje("La potrero recibida no se intersecta con el predio");
		}
		
	}
	
	public void mostrarMensaje(String s) {
		System.out.println(s);
	}
	
	private void agregarFormaAJsSinInterseccionPotreros(PotreroFx potreroFx) {
		System.out.println("agregarFormaAJsSinInterseccionPotreros");
//		GeometryFactory gf = new GeometryFactory();
		
		if (this.fxController.getPotrerosData().isEmpty()) {
			System.out.println("Aún no hay potreros en la lista");
			//Si no hay PotreroFx aún en la lista...
			this.agregarDibujoAJsConConfirmar(potreroFx);
			
		}else {
			System.out.println("Ya hay potreros en la lista");
			//Ya hay potreros en la lista
			Geometry potrerosSumadas = null;	//Variable para sumar todas las potreros existentes
			for (PotreroFx potreroExistente : this.fxController.getPotrerosData()) {
				if (potrerosSumadas == null) {
					potrerosSumadas = potreroExistente.getPotrero().getForma();
				}else {
					potrerosSumadas = potrerosSumadas.union(potreroExistente.getPotrero().getForma());
				}
			}
			System.out.println("Ya sume todas las potreros existentes");
			if (potrerosSumadas instanceof Polygon || potrerosSumadas instanceof MultiPolygon) {
				System.out.println("Potreros sumadas es un MultiPolygon");
				Geometry potreroSinInterseccion = potreroFx.getPotrero().getForma().difference(potrerosSumadas);	//Si no funciona con difference, tomar la interseccion y restar solo la intersección.
				
				if(potreroSinInterseccion instanceof Polygon) {
					//Si la intersección con las demas potreros resulta en un poligono
					System.out.println("La intersección con las potreros existentes resultó en un Poligono");
					potreroFx.getPotrero().setForma((Polygon) potreroSinInterseccion);
					this.agregarDibujoAJsConConfirmar(potreroFx);
				}else if (potreroSinInterseccion instanceof MultiPolygon) {
					System.out.println("La interseccion con las potreros existentes resultó en un MultiPoligono");
					int cantPoligonos = potreroSinInterseccion.getNumGeometries();
					for (int i = 0; i < cantPoligonos; i++) {
						Polygon poligonoActual = (Polygon) potreroSinInterseccion.getGeometryN(i);
						//Creo una nueva potrero geografica para cada poligono
						PotreroFx nuevaPotreroFx = new PotreroFx();
						nuevaPotreroFx.getPotrero().setForma(poligonoActual);
						this.agregarDibujoAJsConConfirmar(nuevaPotreroFx);
					}
				}
			}else if (potrerosSumadas instanceof GeometryCollection) {
				System.out.println("Potreros sumadas es una GeometryColletion");
			}else {
				System.out.println("Ni GeometryCollection ni poligonos");
			}
		}
	}
	
	private boolean agregarDibujoAJsConConfirmar(PotreroFx potreroFx) {	
		
		Polygon forma = potreroFx.getPotrero().getForma();
		
		if (potreroFx.getPotrero().getForma().getNumInteriorRing() != 0) {	
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
			System.out.println("no puede existir un poligono con 3 coordenadas ya que la primera se repite con la ultima");
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
		potreroFx.getPotrero().setForma(nuevaForma);
		
		
		potreroFx.setId(this.getNuevoId());
		potreroFx.setNombre("Potrero" + potreroFx.getId());
		agregarBotonBorrar(potreroFx);
		this.confirmarFormaJs(potreroFx);
		JSObject window = (JSObject) this.fxController.getWebViewEngine().executeScript("window");
		window.setMember("ultimaPotrero", potreroFx);
		String strArea = (String) fxController.getWebViewEngine().executeScript("dibujarPotrero()");
		
		System.out.println("Area potreroJS: " + strArea);
		System.out.println("AreaForma: " + potreroFx.getPotrero().getAreaEnHectareas());
		window.removeMember("ultimaPotrero");
		potreroFx.setArea(potreroFx.getPotrero().getAreaEnHectareas());
		fxController.getPotrerosData().add(potreroFx);									//Agrego a la tabla
		//Seleccionar el agregado
		fxController.getPotrerosTable().getSelectionModel().select(potreroFx);
		return true;
	}
	
	private void borrarFormaDeJs(PotreroFx potreroFx) {		
		fxController.getWebViewEngine().executeScript("borrarPotrero("+  potreroFx.getId()  +")");	//Borro de javascript
		fxController.getPotrerosData().remove(potreroFx);									//Borro de la tabla
	}
	
	private void confirmarFormaJs(PotreroFx potreroFx) {
		try {
			fxController.getPotreroClient().altaPotrero(potreroFx);	//Agrego la potrero al bean con estado
			agregarBotonBorrar(potreroFx);				//cambiar la accion del boton borrar
//			potreroFx.getBotonOk().setVisible(false);	//Oculto el botón confirmar.
			
		} catch (PotrerosException e) {
			System.out.println("No se pudo informar del alta de potrero al servidor");
			//Borro la forma del mapa y el elemento de la tabla
			borrarFormaDeJs(potreroFx);
			fxController.getPotrerosData().remove(potreroFx);
//			fxController.getMainApp().mostrarAlerta(e, AlertType.WARNING);
		}
	}
	
	private void agregarBotonBorrar(PotreroFx potreroFx) {
		potreroFx.getBotonBorrar().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Apretaste boton borrar");
				
				int indice = fxController.getPotrerosTable().getSelectionModel().getSelectedIndex();
				try {
					fxController.getPotreroClient().borrarPotrero(potreroFx);
					fxController.getPotrerosData().remove(potreroFx);
					borrarFormaDeJs(potreroFx);
					fxController.getMainApp().mostrarAlerta("Potreros","Borrar potreros", "Se borró el potrero " + potreroFx.getNombre(),AlertType.INFORMATION);
				} catch (DatosInvalidosException e) {
					System.out.println("Dio error el bean al borrar");
					fxController.getPotrerosData().add(indice,potreroFx);
					fxController.getMainApp().mostrarAlerta(e, AlertType.WARNING);
				}
			}
		});
	}
	
	public void agregarPotrerosExistentesPredio() {
		//Tomar la lista de potreroFx y dibujarlas todas con sus correspondientes IDs
		JSObject window = (JSObject) this.fxController.getWebViewEngine().executeScript("window");
		for (PotreroFx potreroFx : this.fxController.getPotrerosData()) {
			agregarBotonBorrar(potreroFx);
			window.setMember("ultimaPotrero", potreroFx);
			fxController.getWebViewEngine().executeScript("dibujarPotrero()");
			window.removeMember("ultimaPotrero");
			
			int id = CadenasUtil.getNumeros(potreroFx.getNombre()) * -1;
//			System.out.println(potreroFx.getNombre() + ": " + id);
			if (id < this.siguienteId) {
				this.siguienteId = id;
			}
			
		}
		this.agregarZonasExistentesPredio();
	}
	
	public void agregarZonasExistentesPredio() {
		//Tomar la lista de zonaFx y dibujarlas todas con sus correspondientes IDs
		JSObject window = (JSObject) this.fxController.getWebViewEngine().executeScript("window");
		for (ZonaGeograficaFx zonaFx : this.fxController.getZonaGeograficasData()) {
			window.setMember("ultimaZona", zonaFx);
			fxController.getWebViewEngine().executeScript("dibujarZona()");
			window.removeMember("ultimaZona");
		}
		
		//Ahora las zonas potreros
		List<ZonaPotrero> zonaPotreros = this.fxController.getZonaPotrerosActivas();
		System.out.println("Hay " + zonaPotreros.size() + " ZonaPotrero");
		for (ZonaPotrero zonaPotrero : zonaPotreros) {
			window.setMember("ultimaZona", zonaPotrero);
			fxController.getWebViewEngine().executeScript("dibujarZonaPotrero()");
			window.removeMember("ultimaZona");
			
		}
	}
	
	public void actualizarTooltip(PotreroFx potreroFx) {
		String metodo = "actualizarTooltip(" + potreroFx.getId() + ",'" + potreroFx.toString() + "')";
		System.out.println(metodo);
		fxController.getWebViewEngine().executeScript(metodo);
	}
	
	public void mostrarTooltip(PotreroFx potreroFx) {
		String metodo = "mostrarTooltip(" + potreroFx.getId() + ")";
		System.out.println(metodo);
		fxController.getWebViewEngine().executeScript(metodo);
	}
	
	public void actualizarColor(PotreroFx potreroFx) {
		String metodo = "actualizarColor(" + potreroFx.getId() + ",'" + potreroFx.getIndicador().getColor() + "')";
		System.out.println(metodo);
		fxController.getWebViewEngine().executeScript(metodo);
	}
}
