package com.escritorio.entidadesFx;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import com.entidades.Predio;
import com.entidades.Propietario;
import com.excepciones.DatosInvalidosException;
import com.excepciones.ProblemaDeConexionException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import netscape.javascript.JSObject;

public class PredioFx implements EntidadFx {

    private final StringProperty nombre;
    private final StringProperty descripcion;
    private final IntegerProperty id;
    private final DoubleProperty area;
    private final BooleanProperty activo;
    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 3785);
//    GeometryFactory geometryFactory = new GeometryFactory();
    private Predio predio;

/************************************************************* 
 *	Constructores
 *************************************************************/
	public PredioFx() {
		//Constructor para nuevo Predio
		this.predio = new Predio();
		this.nombre = new SimpleStringProperty("");
		this.descripcion = new SimpleStringProperty("");
		this.id = new SimpleIntegerProperty(0);
		this.area = new SimpleDoubleProperty(0);
        this.activo = new SimpleBooleanProperty(true);
        
        this.predio.setActivo(true);
        
    }
    
    public PredioFx(Predio predio) {
    	//Constructor para un predio ya existente
    	this.predio = predio;
        this.nombre = new SimpleStringProperty(predio.getNombre());
        this.descripcion = new SimpleStringProperty(predio.getDescripcion());
        this.id = new SimpleIntegerProperty(predio.getId());
        this.area = new SimpleDoubleProperty();
        this.setArea(predio.getAreaEnHectareas());
        this.activo = new SimpleBooleanProperty(predio.getActivo());
    }

   
/************************************************************* 
 * Override del metodo para validación de los atributos
 *************************************************************/
	@Override
	public boolean losCamposSonValidos() throws ProblemaDeConexionException, DatosInvalidosException {
		//La validacion se hace sobre los campos de la entidad Posta.
		String errorMessage = "Los siguientes campos son incorrectos: ";
		int largo = errorMessage.length();
		//
        if (this.predio.getNombre() == null || this.predio.getNombre().length() == 0) {
        	errorMessage = errorMessage + "Nombre,\n ";
        }
        if (this.predio.getDescripcion() == null || this.predio.getDescripcion().length() == 0) {
        	errorMessage = errorMessage + "Descripcion,\n ";
        }
        
        if ( this.predio.getForma() == null || this.predio.getForma().isEmpty() ) {
        	errorMessage = errorMessage + "Forma,\n ";
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
	
/************************************************************* 
 * Getters y seters para atributos FX
 *************************************************************/
	public final StringProperty nombreProperty() {
		return this.nombre;
	}
	

	public final String getNombre() {
		this.nombreProperty().set(this.predio.getNombre());
		return this.nombreProperty().get();
	}
	

	public final void setNombre(final String nombre) {
		this.nombreProperty().set(nombre);
		this.predio.setNombre(nombre);
	}
	

	public final StringProperty descripcionProperty() {
		return this.descripcion;
	}
	

	public final String getDescripcion() {
		this.descripcionProperty().set(this.predio.getDescripcion());
		return this.descripcionProperty().get();
	}
	

	public final void setDescripcion(final String descripcion) {
		this.descripcionProperty().set(descripcion);
		this.predio.setDescripcion(descripcion);
	}
	

	public final IntegerProperty idProperty() {
		return this.id;
	}
	

	public final int getId() {
		return this.idProperty().get();
	}
	
	public final DoubleProperty areaProperty() {
		return this.area;
	}


	public final double getArea() {
		
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		Double area = new Double(df.format(this.predio.getAreaEnHectareas()).replace(",","."));
		
		this.areaProperty().set(area);
		return area ;
	}


	public final void setArea(double area) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		area = new Double(df.format(area).replace(",","."));
		
		this.areaProperty().set(area);
	}
	
	public final BooleanProperty activoProperty() {
		return this.activo;
	}
	

	public final boolean isActivo() {
		this.activoProperty().set(this.predio.getActivo());
		return this.activoProperty().get();
	}
	

	public final void setActivo(final boolean activo) {
		this.activoProperty().set(activo);
		this.predio.setActivo(activo);
	}
	
	
/************************************************************* 
 * Getters y seters para el predio
 *************************************************************/
	public Predio getPredio() {
		return predio;
	}

	public void setPredio(Predio predio) {
		this.predio = predio;
	}
	
/************************************************************* 
 * Getters y seters para atributos que no les podemos
 * poner atributos FX (forma, ubicacion, zonasPredio, etc)
 *************************************************************/

	public boolean borrarForma() {
		this.predio.setForma(null);
		System.out.println("Se borró la forma del predio " + this.predio.getNombre());
		return true;
	}
	
	public boolean setForma(JSObject puntosJs, int largo) {
		Coordinate[] puntosForma = new Coordinate[largo+1];
		//Recorremos el array para ir creando una Coordinate para cada par de doubles
		for (int i = 0; i < largo; i++) {
			JSObject latLng = (JSObject) puntosJs.getMember(Integer.toString(i));
			Double lat = new Double(latLng.getMember("0").toString());
			Double lng = new Double(latLng.getMember("1").toString());
			System.out.println(lat + "," + lng);
			Coordinate punto = new Coordinate(lat, lng);
			puntosForma[i] = punto;	//Agregamos la coordinate a la lista de puntos
		}
		
		//Agrego de nuevo la primer coordenada
		JSObject latLng = (JSObject) puntosJs.getMember("0");
		Double lat = new Double(latLng.getMember("0").toString());
		Double lng = new Double(latLng.getMember("1").toString());
		
		Coordinate punto = new Coordinate(lat, lng);
		puntosForma[largo] = punto;	//Agregamos la coordinate a la lista de puntos
		
	    Polygon polygon = this.geometryFactory.createPolygon(puntosForma);
	    
	    this.predio.setForma(polygon);
	    this.setArea(this.predio.getAreaEnHectareas());
	    System.out.println("SRID Forma: " + polygon.getSRID());
	    System.out.println("Area predio " + this.predio.getAreaEnHectareas());
	    return true;
	}
	
	public Coordinate[] getForma() {
//		System.out.println(this.predio.getForma());
		if (this.predio != null) {
			return this.predio.getForma().getCoordinates();
		}
		System.out.println("El predio no tiene forma");
		return null;
	}
	
	public String getPropietariosString() {
		String propietarios = "";
		for (Propietario propietario : this.predio.getPropietarios()) {
			propietarios = propietarios.concat(propietario.getNombre().concat("|"));
		}
		
		return propietarios;
	}

/************************************************************* 
 * 
 *************************************************************/
	public void syso(String mensaje) {
		System.out.println(mensaje);
	}
	
	private ObservableList<PropietarioFx> propietariosFx = FXCollections.observableArrayList();
	
    public void cargarTodosLosPropietarios(List<PropietarioFx> propietariosFx) {
    	//por cada rol de la lista de roles, genero el RolFx
    	this.propietariosFx = FXCollections.observableArrayList();
		for (PropietarioFx propietarioFx : propietariosFx) {
			propietarioFx.setSelected(this.predio.getPropietarios().contains(propietarioFx.getPropietario()));	//Seteo seleccionado según contenga o no el rol
			//Si hubiera algun propietario del predio, q esté deshabilitado, no se va a borrar.
			if (propietarioFx.getPropietario().getActivo()) {
				//Solo lo agrego al listado de propietarios, si está activo
				this.propietariosFx.add(propietarioFx);
			}
			
			
		}
    }

	public void sincronizarPropietarios() {
		System.out.println("Se llamó a sincronizarPropietarios");
		for (PropietarioFx propietarioFx : propietariosFx) {
			boolean selected = propietarioFx.isSelected();
			if (selected) {
				//Si lo selecciono
				this.predio.addPropietario(propietarioFx.getPropietario());
			}else {
				this.predio.removePropietario(propietarioFx.getPropietario());
			}
			System.out.println(propietarioFx.getNombre() + ":" + selected + " en " + this.getNombre());
		}
		
	}

	public ObservableList<PropietarioFx> getPropietariosFx() {
		return propietariosFx;
	}

	public void setPropietariosFx(ObservableList<PropietarioFx> propietariosFx) {
		this.propietariosFx = propietariosFx;
	}

	private boolean mapaEditable=false;

	public boolean isMapaEditable() {
		return mapaEditable;
	}

	public void setMapaEditable(boolean mapaEditable) {
		this.mapaEditable = mapaEditable;
	}
	
	

}