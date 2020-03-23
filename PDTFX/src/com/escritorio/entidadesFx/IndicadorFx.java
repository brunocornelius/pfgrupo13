package com.escritorio.entidadesFx;

import com.entidades.Indicador;
import com.entidades.IndicadorPotrero;
import com.excepciones.ProblemaDeConexionException;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
public class IndicadorFx implements EntidadFx {

    private final StringProperty nombre;
    private final StringProperty descripcion;
    private final IntegerProperty id;
    private Indicador indicador;
    
    

    public IndicadorFx() {
        this.nombre = new SimpleStringProperty("");
        this.descripcion = new SimpleStringProperty("");
        this.id = new SimpleIntegerProperty(1);
        this.indicador = new Indicador();
        
    }
    
    public IndicadorFx(Indicador tz) {
    	this.indicador = tz;
        this.nombre = new SimpleStringProperty(tz.getNombre());
        this.descripcion = new SimpleStringProperty(tz.getDescripcion());
        this.id = new SimpleIntegerProperty(tz.getId());
    }

    public String getNombre() {
    	return this.indicador.getNombre();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
        this.indicador.setNombre(nombre);
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public int getId() {
//        return id.get();
    	return this.indicador.getId();
    }

    public void setId(int id) {
//        this.id.set(id);
    	//no se puede editar el id
    }

    public IntegerProperty idProperty() {
        return id;
    }
    
	public String getDescripcion() {

	  	return this.indicador.getDescripcion();
	}

	  public void setDescripcion(String descripcion) {
	      this.descripcion.set(descripcion);
	      this.indicador.setDescripcion(descripcion);
	  }
	
	  public StringProperty descripcionProperty() {
	      return nombre;
	  }

	public Indicador getIndicador() {
		return indicador;
	}

	public void setIndicador(Indicador indicador) {
		this.indicador = indicador;
		this.nombre.set(indicador.getNombre());
		this.id.set(indicador.getId());
	}

	@Override
	public boolean losCamposSonValidos() throws ProblemaDeConexionException {
		//La validacion se hace sobre los campos FX
		String errorMessage = "";
		//TODO chequear largo maximo en el nombres
        if (this.nombre.get() == null || this.nombre.get().length() == 0) {
            errorMessage += "No has ingresado un nombre de Indicador válido\n"; 
        }
        
        if (errorMessage.length() == 0) {
            return true;
        } else {
        	throw new ProblemaDeConexionException(errorMessage);
        }
		
        //Ya no llegaría nunca aqui.
	}

}