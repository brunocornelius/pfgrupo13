package com.escritorio.entidadesFx;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import com.entidades.TipoZona;
import com.entidades.ZonaGeografica;
import com.excepciones.DatosInvalidosException;
import com.excepciones.ProblemaDeConexionException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import netscape.javascript.JSObject;

public class ZonaGeograficaFx implements EntidadFx {

    private final StringProperty nombre;
    private final StringProperty descripcion;
    private final IntegerProperty id;
    private final SimpleDoubleProperty area;
//    private GeometryFactory geometryFactory = new GeometryFactory();
    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 3785);
    private ZonaGeografica zonaGeografica;
    private final SimpleObjectProperty<Button> botonBorrar;
    private final SimpleObjectProperty<TipoZona> tipoZona;
    
    private int idJs;
    
/************************************************************* 
 *	Constructores
 *************************************************************/
	public ZonaGeograficaFx() {
		//Constructor para nuevo ZonaGeografica
		this.zonaGeografica = new ZonaGeografica();
		this.nombre = new SimpleStringProperty("nombre");
		this.descripcion = new SimpleStringProperty("descripcion");
		this.id = new SimpleIntegerProperty(0);
		this.zonaGeografica.setNombre("Ingrese nombre");
		this.zonaGeografica.setDescripcion("Ingrese descripcion");
		this.tipoZona = new SimpleObjectProperty<TipoZona>();
		this.botonBorrar = new SimpleObjectProperty<Button>(new Button("Borrar"));
		this.area = new SimpleDoubleProperty(0);
    }
    
    public ZonaGeograficaFx(ZonaGeografica zonaGeo) {
    	//Constructor para un zonaGeografica ya existente
    	this.zonaGeografica = zonaGeo;
        this.nombre = new SimpleStringProperty(zonaGeo.getNombre());
        this.descripcion = new SimpleStringProperty(zonaGeo.getDescripcion());
        this.id = new SimpleIntegerProperty(zonaGeo.getId());        
        this.tipoZona = new SimpleObjectProperty<TipoZona>(zonaGeo.getTipoZona());
        this.botonBorrar = new SimpleObjectProperty<Button>(new Button("Borrar"));
		this.area = new SimpleDoubleProperty();
		this.setArea(zonaGeo.getAreaEnHectareas());
//		this.idJs = zonaGeo.getIdJs();
    }

   
/************************************************************* 
 * Override del metodo para validación de los atributos
 *************************************************************/
	@Override
	public boolean losCamposSonValidos() throws DatosInvalidosException {
		//La validacion se hace sobre los campos de la entidad Posta.
		String errorMessage = "Los siguientes campos son incorrectos: ";
		int largo = errorMessage.length();
		//
        if (this.zonaGeografica.getNombre() == null || this.zonaGeografica.getNombre().length() == 0) {
        	errorMessage = errorMessage + "Nombre,\n ";
        }
        if (this.zonaGeografica.getDescripcion() == null || this.zonaGeografica.getDescripcion().length() == 0) {
        	errorMessage = errorMessage + "Descripcion,\n ";
        }
        
        if ( this.zonaGeografica.getForma() == null || this.zonaGeografica.getForma().isEmpty() ) {
        	errorMessage = errorMessage + "Forma,\n ";
        }
        
        if ( this.zonaGeografica.getTipoZona() == null) {
        	errorMessage = errorMessage + "TipoZona,\n ";
        }
        
        if (errorMessage.length() == largo) {
        	//Si no cambió el largo del mensaje
        	System.out.println("Los datos son VALIDOS");
            return true;
        } else {
        	System.out.println("Los datos son invalidos");
        	throw new DatosInvalidosException(errorMessage);
        }
		
	}
	
	public boolean comprobarSiEstaDentroDePoligono(Polygon poligono) {
		return this.zonaGeografica.getForma().within(poligono);
	}
	
/************************************************************* 
 * Getters y seters para atributos FX
 *************************************************************/
	public final StringProperty nombreProperty() {
		return this.nombre;
	}
	

	public final String getNombre() {
		this.nombreProperty().set(this.zonaGeografica.getNombre());
		return this.nombreProperty().get();
	}
	

	public final void setNombre(final String nombre) {
		this.nombreProperty().set(nombre);
		this.zonaGeografica.setNombre(nombre);
	}
	

	public final StringProperty descripcionProperty() {
		return this.descripcion;
	}
	

	public final String getDescripcion() {
		this.descripcionProperty().set(this.zonaGeografica.getDescripcion());
		return this.descripcionProperty().get();
	}
	

	public final void setDescripcion(final String descripcion) {
		this.descripcionProperty().set(descripcion);
		this.zonaGeografica.setDescripcion(descripcion);
	}
	

	public final IntegerProperty idProperty() {
		return this.id;
	}
	

	public final int getId() {
		return this.idProperty().get();
	}
	
	public final void setId(final int id) {
		this.idProperty().set(id);
	}


	public final SimpleObjectProperty<Button> botonBorrarProperty() {
		return this.botonBorrar;
	}


	public final Button getBotonBorrar() {
		return this.botonBorrarProperty().get();
	}


	public final void setBotonBorrar(final Button botonBorrar) {
		this.botonBorrarProperty().set(botonBorrar);
	}
	
	public final SimpleObjectProperty<TipoZona> tipoZonaProperty() {
		return this.tipoZona;
	}


	public final TipoZona getTipoZona() {
		this.tipoZonaProperty().set(this.zonaGeografica.getTipoZona());
		return this.tipoZonaProperty().get();
	}


	public final void setTipoZona(final TipoZona tipoZona) {
		this.zonaGeografica.setTipoZona(tipoZona);
		this.tipoZonaProperty().set(tipoZona);
	}
	
	public final SimpleDoubleProperty areaProperty() {
		return this.area;
	}


	public final double getArea() {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		Double area = new Double(df.format(this.zonaGeografica.getAreaEnHectareas()).replace(",","."));
		this.areaProperty().set(area);
		return area ;
	}


	public final void setArea(double area) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		area = new Double(df.format(area).replace(",","."));
		
		this.areaProperty().set(area);
	}
	
	public String getColor() {
		String color = "lightskyblue";
		if (this.getTipoZona() != null) {
			color = this.getTipoZona().getColor().toString();
		}
		return color;
	}

/************************************************************* 
 * Getters y seters para el zonaGeografica
 *************************************************************/
	public ZonaGeografica getZonaGeografica() {
		return zonaGeografica;
	}

	public void setZonaGeografica(ZonaGeografica zonaGeografica) {
		this.zonaGeografica = zonaGeografica;
	}
	
/************************************************************* 
 * Getters y seters para atributos que no les podemos
 * poner atributos FX (forma, ubicacion, zonasZonaGeografica, etc)
 *************************************************************/

	public boolean borrarForma() {
		this.zonaGeografica.setForma(null);
		System.out.println("Se borró la forma del zonaGeografica " + this.zonaGeografica.getNombre());
		return true;
	}
	public boolean setForma(JSObject puntosJs, int largo) {
		Coordinate[] puntosForma = new Coordinate[largo+1];
		//Recorremos el array para ir creando una Coordinate para cada par de doubles
		for (int i = 0; i < largo; i++) {
			JSObject latLng = (JSObject) puntosJs.getMember(Integer.toString(i));
			Double lat = new Double(latLng.getMember("0").toString());
			Double lng = new Double(latLng.getMember("1").toString());
			Coordinate punto = new Coordinate(lat, lng);
			puntosForma[i] = punto;	//Agregamos la coordinate a la lista de puntos
			syso(lat + "," + lng);
		}
		
		//Agrego de nuevo la primer coordenada
		JSObject latLng = (JSObject) puntosJs.getMember("0");
		Double lat = new Double(latLng.getMember("0").toString());
		Double lng = new Double(latLng.getMember("1").toString());
		Coordinate punto = new Coordinate(lat, lng);
		puntosForma[largo] = punto;	//Agregamos la coordinate a la lista de puntos
		
	    Polygon polygon = this.geometryFactory.createPolygon(puntosForma);
	    this.zonaGeografica.setForma(polygon);
	    System.out.println(polygon + " seteado en zonaGeografica " + this.zonaGeografica.getNombre());
	    return true;
	}
	
	public Coordinate[] getForma() {
//		System.out.println(this.zonaGeografica.getForma());
		if (this.zonaGeografica != null) {
			return this.zonaGeografica.getForma().getCoordinates();
		}
		System.out.println("El zonaGeografica no tiene forma");
		return null;
	}
	

/************************************************************* 
 * Metodos para imprimir en consola desde JS
 *************************************************************/
	public void syso(String mensaje) {
		System.out.println(mensaje);
	}

	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.####");
		df.setRoundingMode(RoundingMode.CEILING);
		String cadena = this.zonaGeografica.getNombre() + ":" + df.format(this.zonaGeografica.getAreaEnHectareas()) + "ha";
		return cadena;
	}

	public int getIdJs() {
		return this.zonaGeografica.getIdJs();
	}

	public void setIdJs(int idJs) {
		this.zonaGeografica.setIdJs(idJs);
	}


	




	
}