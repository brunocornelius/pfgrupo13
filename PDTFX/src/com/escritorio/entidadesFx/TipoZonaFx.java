package com.escritorio.entidadesFx;

import com.entidades.TipoZona;
import com.excepciones.ProblemaDeConexionException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TipoZonaFx implements EntidadFx {

    private final StringProperty nombre;
    private final IntegerProperty id;
    private final SimpleObjectProperty<String> color;
    
    private TipoZona tipoZona;
    public TipoZonaFx() {
        this("");
        
    }

    public TipoZonaFx(String nombre) {
        this.nombre = new SimpleStringProperty(nombre);
        this.id = new SimpleIntegerProperty(1);
        this.tipoZona = new TipoZona();
        this.tipoZona.setNombre(nombre);
        this.color = new SimpleObjectProperty<String>("blueviolet");
    }
    
    public TipoZonaFx(TipoZona tz) {
    	this.tipoZona = tz;
        this.nombre = new SimpleStringProperty(tz.getNombre());
        this.id = new SimpleIntegerProperty(tz.getId());
        this.color = new SimpleObjectProperty<String>(tz.getColor());
    }

    public String getNombre() {
//        return nombre.get();
    	return this.tipoZona.getNombre();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
        this.tipoZona.setNombre(nombre);
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public int getId() {
//        return id.get();
    	return this.tipoZona.getId();
    }

    public void setId(int id) {
//        this.id.set(id);
    	//no se puede editar el id
    }

    public IntegerProperty idProperty() {
        return id;
    }

	public TipoZona getTipoZona() {
		return tipoZona;
	}

	public void setTipoZona(TipoZona tipoZona) {
		this.tipoZona = tipoZona;
		this.nombre.set(tipoZona.getNombre());
		this.id.set(tipoZona.getId());
	}
	
	public final SimpleObjectProperty<String> colorProperty() {
		return this.color;
	}
	

	public final String getColor() {
		this.colorProperty().set(this.tipoZona.getColor());
		return this.colorProperty().get();
	}
	

	public final void setColor(final String color) {
		this.tipoZona.setColor(color);
		this.colorProperty().set(color);
	}

	@Override
	public boolean losCamposSonValidos() throws ProblemaDeConexionException {
		//La validacion se hace sobre los campos FX
		String errorMessage = "";
		//TODO chequear largo maximo en el nombres
        if (this.nombre.get() == null || this.nombre.get().length() == 0) {
            errorMessage += "No has ingresado un nombre de TipoZona válido\n"; 
        }
        
        if (errorMessage.length() == 0) {
            return true;
        } else {
        	throw new ProblemaDeConexionException(errorMessage);
        }
		
        //Ya no llegaría nunca aqui.
	}
	
}