package com.escritorio.entidadesFx;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import com.entidades.Indicador;
import com.entidades.IndicadorPotrero;
import com.entidades.Potrero;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import netscape.javascript.JSObject;

public class PotreroFx implements EntidadFx {

    private final StringProperty nombre;
    private final StringProperty descripcion;
    private final IntegerProperty id;
    private final SimpleDoubleProperty area;
//    private GeometryFactory geometryFactory = new GeometryFactory();
    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 3785);
    private Potrero potrero;
    private final SimpleObjectProperty<Button> botonBorrar;
    private final SimpleObjectProperty<Indicador> indicador;
    
/************************************************************* 
 *	Constructores
 *************************************************************/
	public PotreroFx() {
		//Constructor para nuevo Potrero
		this.potrero = new Potrero();
		this.nombre = new SimpleStringProperty("nombre");
		this.descripcion = new SimpleStringProperty("descripcion");
		this.id = new SimpleIntegerProperty(0);
		this.potrero.setNombre("Ingrese nombre");
		this.potrero.setDescripcion("Ingrese descripcion");
		this.indicador = new SimpleObjectProperty<Indicador>();
		this.botonBorrar = new SimpleObjectProperty<Button>(new Button("Borrar"));
		this.area = new SimpleDoubleProperty(0);
    }
    
    public PotreroFx(Potrero potrero) {
    	//Constructor para un potrero ya existente
    	this.potrero = potrero;
        this.nombre = new SimpleStringProperty(potrero.getNombre());
        this.descripcion = new SimpleStringProperty(potrero.getDescripcion());
        this.id = new SimpleIntegerProperty(potrero.getId());        
        this.indicador = new SimpleObjectProperty<Indicador>(potrero.getIndicadorActual());
        this.botonBorrar = new SimpleObjectProperty<Button>(new Button("Borrar"));
		this.area = new SimpleDoubleProperty();
		this.setArea(potrero.getAreaEnHectareas());
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
        if (this.potrero.getNombre() == null || this.potrero.getNombre().length() == 0) {
        	errorMessage = errorMessage + "Nombre,\n ";
        }
        if (this.potrero.getDescripcion() == null || this.potrero.getDescripcion().length() == 0) {
        	errorMessage = errorMessage + "Descripcion,\n ";
        }
        
        if ( this.potrero.getForma() == null || this.potrero.getForma().isEmpty() ) {
        	errorMessage = errorMessage + "Forma,\n ";
        }
        
        if ( this.potrero.getIndicadorActual() == null) {
        	errorMessage = errorMessage + "Indicador,\n ";
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
		return this.potrero.getForma().within(poligono);
	}
	
/************************************************************* 
 * Getters y seters para atributos FX
 *************************************************************/
	public final StringProperty nombreProperty() {
		return this.nombre;
	}
	

	public final String getNombre() {
		this.nombreProperty().set(this.potrero.getNombre());
		return this.nombreProperty().get();
	}
	

	public final void setNombre(final String nombre) {
		this.nombreProperty().set(nombre);
		this.potrero.setNombre(nombre);
	}
	

	public final StringProperty descripcionProperty() {
		return this.descripcion;
	}
	

	public final String getDescripcion() {
		this.descripcionProperty().set(this.potrero.getDescripcion());
		return this.descripcionProperty().get();
	}
	

	public final void setDescripcion(final String descripcion) {
		this.descripcionProperty().set(descripcion);
		this.potrero.setDescripcion(descripcion);
	}
	

	public final IntegerProperty idProperty() {
		return this.id;
	}
	

	public final int getId() {
		return this.idProperty().get();
	}
	
	public final void setId(final int id) {
		this.idProperty().set(id);
		this.potrero.setId(id);
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
	
	//TODO Que hago con el indicador que seleccione ? Implementar un sincronizar IndicadorActual potrero
	public final SimpleObjectProperty<Indicador> indicadorProperty() {
		return this.indicador;
	}
	

	public final Indicador getIndicador() {
		this.indicadorProperty().set(this.potrero.getIndicadorActual());
		return this.indicadorProperty().get();
	}
	

	public final void setIndicador(final Indicador indicador) {
		//Aca tendria que crear el indicador potrero
		this.potrero.setIndicadorPotreroActual(indicador);
		this.indicadorProperty().set(indicador);
	}

	
	
	public final SimpleDoubleProperty areaProperty() {
		return this.area;
	}


	public final double getArea() {
		this.areaProperty().set(this.potrero.getAreaEnHectareas());
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		Double area = new Double(df.format(this.potrero.getAreaEnHectareas()).replace(",","."));
		this.areaProperty().set(area);
		return area ;

	}


	public final void setArea(double area) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		area = new Double(df.format(area).replace(",","."));
		this.areaProperty().set(area);
	}

/************************************************************* 
 * Getters y seters para el potrero
 *************************************************************/
	public Potrero getPotrero() {
		return potrero;
	}

	public void setPotrero(Potrero potrero) {
		this.potrero = potrero;
	}
	
/************************************************************* 
 * Getters y seters para atributos que no les podemos
 * poner atributos FX (forma, ubicacion, zonasPotrero, etc)
 *************************************************************/

	public boolean borrarForma() {
		this.potrero.setForma(null);
		System.out.println("Se borró la forma del potrero " + this.potrero.getNombre());
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
			
			System.out.println(lat + "," + lng);
		}
		
		//Agrego de nuevo la primer coordenada
		JSObject latLng = (JSObject) puntosJs.getMember("0");
		Double lat = new Double(latLng.getMember("0").toString());
		Double lng = new Double(latLng.getMember("1").toString());
		Coordinate punto = new Coordinate(lat, lng);
		puntosForma[largo] = punto;	//Agregamos la coordinate a la lista de puntos
		
	    Polygon polygon = this.geometryFactory.createPolygon(puntosForma);
	    this.potrero.setForma(polygon);
	    System.out.println(polygon + " seteado en potrero " + this.potrero.getNombre());
	    return true;
	}
	
	public Coordinate[] getForma() {
//		System.out.println(this.potrero.getForma());
		if (this.potrero != null) {
			return this.potrero.getForma().getCoordinates();
		}
		System.out.println("El potrero no tiene forma");
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
		String cadena = this.potrero.getNombre() + ":" + df.format(this.potrero.getAreaEnHectareas()) + "ha";
		System.out.println("Cadena: " + cadena);
		return cadena;
	}

	public String getColor() {
		String color = "lightskyblue";
		if (this.getIndicador() != null) {
			color = this.getIndicador().getColor().toString();
		}
		return color;
	}
	


	public ObservableList<IndicadorPotreroFx> getIndicadorPotrerosFx() {
		ObservableList<IndicadorPotreroFx> listado = FXCollections.observableArrayList();
		for (IndicadorPotrero indicadorPotrero : this.potrero.getIndicadorPotreros()) {
			IndicadorPotreroFx indicadorPotreroFx = new IndicadorPotreroFx(indicadorPotrero);
			listado.add(indicadorPotreroFx);
			System.out.println(indicadorPotrero.getIndicador().getNombre());
		}
		return listado;
	}




	
}