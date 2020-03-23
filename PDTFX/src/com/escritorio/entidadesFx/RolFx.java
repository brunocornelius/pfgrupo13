package com.escritorio.entidadesFx;

import com.entidades.Rol;
import com.excepciones.ProblemaDeConexionException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RolFx implements EntidadFx {

    private final StringProperty nombre;
    private final IntegerProperty id;
    
    private BooleanProperty selected = new SimpleBooleanProperty();
    
    private Rol rol;
    
    public RolFx() {
    	this.nombre = new SimpleStringProperty("");
        this.id = new SimpleIntegerProperty(0);
        this.rol = new Rol();
        
    }
    
    public RolFx(Rol rol) {
    	this.rol = rol;
        this.nombre = new SimpleStringProperty(rol.getNombre());
        this.id = new SimpleIntegerProperty(rol.getId());
    }

    public String getNombre() {
//        return nombre.get();
    	return this.rol.getNombre();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
        this.rol.setNombre(nombre);
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public int getId() {
        return id.get();
    }


    public IntegerProperty idProperty() {
        return id;
    }

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
		this.nombre.set(rol.getNombre());
		this.id.set(rol.getId());
	}

	@Override
	public boolean losCamposSonValidos() throws ProblemaDeConexionException {
		//La validacion se hace sobre los campos FX
		String errorMessage = "";
		//TODO chequear largo maximo en el nombres
        if (this.nombre.get() == null || this.nombre.get().length() == 0) {
            errorMessage += "No has ingresado un nombre de Rol válido\n"; 
        }
        
        if (errorMessage.length() == 0) {
            return true;
        } else {
        	throw new ProblemaDeConexionException(errorMessage);
        }
		
        //Ya no llegaría nunca aqui.
	}

	public final BooleanProperty selectedProperty() {
		return this.selected;
	}
	

	public final boolean isSelected() {
		return this.selectedProperty().get();
	}
	

	public final void setSelected(final boolean selected) {
		this.selectedProperty().set(selected);
	}
	
}