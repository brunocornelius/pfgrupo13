package com.escritorio.entidadesFx;

import com.entidades.Propietario;
import com.excepciones.DatosInvalidosException;
import com.excepciones.ProblemaDeConexionException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class PropietarioFx implements EntidadFx {

    private final StringProperty nombre;
    private final LongProperty rut;
    private final IntegerProperty id;
    private Propietario propietario;

    private final BooleanProperty activo;
    private final StringProperty razonSocial;
    private final StringProperty direccion;
    private final StringProperty correo;
    private final StringProperty telefono;
    private final StringProperty contactoReferencia;
    
    private BooleanProperty seleccionado = new SimpleBooleanProperty(false);

    public PropietarioFx() {
        this.nombre = new SimpleStringProperty("");
        this.rut = new SimpleLongProperty(0);
        this.id = new SimpleIntegerProperty(1234);
        this.razonSocial = new SimpleStringProperty("");
        this.direccion = new SimpleStringProperty("");
        this.correo = new SimpleStringProperty("");
        this.telefono = new SimpleStringProperty("");
        this.contactoReferencia = new SimpleStringProperty("");
        this.propietario = new Propietario();
        this.propietario.setNombre("");
        this.propietario.setRut(new Long(0));
        this.propietario.setRazonSocial("");
        this.propietario.setDireccion("");
        this.propietario.setCorreo("");
        this.propietario.setTelefono("");
        this.propietario.setContactoReferencia("");
        this.activo = new SimpleBooleanProperty(true);
        this.propietario.setActivo(false);
        
    }
    
    public PropietarioFx(Propietario propietario) {
    	this.propietario = propietario;
        this.nombre = new SimpleStringProperty(propietario.getNombre());
        this.rut = new SimpleLongProperty(propietario.getRut());
        this.id = new SimpleIntegerProperty(propietario.getId());
        
        this.razonSocial = new SimpleStringProperty(propietario.getRazonSocial());
        this.direccion = new SimpleStringProperty(propietario.getDireccion());
        this.correo = new SimpleStringProperty(propietario.getCorreo());
        this.telefono = new SimpleStringProperty(propietario.getTelefono());
        this.contactoReferencia = new SimpleStringProperty(propietario.getContactoReferencia());
        
        this.activo = new SimpleBooleanProperty(propietario.getActivo());
    }

    public String getNombre() {
//        return nombre.get();
    	return this.propietario.getNombre();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
        this.propietario.setNombre(nombre);
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public Long getRut() {
//        return rut.get();
    	return this.propietario.getRut();
    }

    public void setRut(Long rut) {
        this.rut.set(rut);
        this.propietario.setRut(rut);
    }

    public LongProperty rutProperty() {
        return rut;
    }

    public int getId() {
//        return id.get();
    	return this.propietario.getId();
    }

    public void setId(int id) {
//        this.id.set(id);
    	//no se puede editar el id
    }

    public IntegerProperty idProperty() {
        return id;
    }

	public Propietario getPropietario() {
		return propietario;
	}
	
	public final BooleanProperty activoProperty() {
		return this.activo;
	}
	

	public final boolean isActivo() {
		this.activoProperty().set(this.propietario.getActivo());
		return this.activoProperty().get();
	}
	

	public final void setActivo(final boolean activo) {
		this.activoProperty().set(activo);
		this.propietario.setActivo(activo);
	}

	public void setPropietario(Propietario p) {
		this.propietario = p;
		this.nombre.set(p.getNombre());
		this.rut.set(p.getRut());
		this.id.set(p.getId());
		this.activo.set(p.getActivo());
	}

	@Override
	public boolean losCamposSonValidos() throws DatosInvalidosException {
		//La validacion se hace sobre los campos FX
		String errorMessage = "";
		//TODO FALTAN CONSIDERAR TODOS LOS CAMPOS
        if (this.nombre.get() == null || this.nombre.get().length() == 0) {
            errorMessage += "No has ingresado un nombre de Propietario válido\n"; 
        }
        if (this.rut.get() == 0) {
            errorMessage += "No has ingresado un número de RUT válido"; 
        }
        if (this.razonSocial.get() == null || this.razonSocial.get().length() == 0) {
            errorMessage += "No has ingresado una razón social válida\n"; 
        }
        if (this.direccion.get() == null || this.direccion.get().length() == 0) {
            errorMessage += "No has ingresado una direccion válida\n"; 
        }
        if (this.correo.get() == null || this.correo.get().length() == 0) {
            errorMessage += "No has ingresado un correo válido\n"; 
        }
        
        if ( !( this.correo.get().contains("@") && this.correo.get().contains(".") ) ) {
        	//Si no contiene @ y .
        	errorMessage += "El correo no es válido\n"; 
        }
        
        if (this.telefono.get() == null || this.telefono.get().length() == 0) {
            errorMessage += "No has ingresado un telefono válido\n"; 
        }
        if (this.contactoReferencia.get() == null || this.contactoReferencia.get().length() == 0) {
            errorMessage += "No has ingresado un contacto de Referencia válida\n"; 
        }
        
        
        if (errorMessage.length() == 0) {
            return true;
        } else {
        	throw new DatosInvalidosException(errorMessage);
        }
		
        //Ya no llegaría nunca aqui.
	}
	
	
	

	public final StringProperty razonSocialProperty() {
		return this.razonSocial;
	}
	

	public final String getRazonSocial() {
		this.razonSocialProperty().set(this.propietario.getRazonSocial());
		return this.razonSocialProperty().get();
	}
	

	public final void setRazonSocial(final String razonSocial) {
		this.propietario.setRazonSocial(razonSocial);
		this.razonSocialProperty().set(razonSocial);
	}
	

	public final StringProperty direccionProperty() {
		return this.direccion;
	}
	

	public final String getDireccion() {
		this.direccionProperty().set(this.propietario.getDireccion());
		return this.direccionProperty().get();
	}
	

	public final void setDireccion(final String direccion) {
		this.propietario.setDireccion(direccion);
		this.direccionProperty().set(direccion);
	}
	

	public final StringProperty correoProperty() {
		return this.correo;
	}
	

	public final String getCorreo() {
		this.correoProperty().set(this.propietario.getCorreo());
		return this.correoProperty().get();
	}
	

	public final void setCorreo(final String correo) {
		this.propietario.setCorreo(correo);
		this.correoProperty().set(correo);
	}
	

	public final StringProperty telefonoProperty() {
		return this.telefono;
	}
	

	public final String getTelefono() {
		this.telefonoProperty().set(this.propietario.getTelefono());
		return this.telefonoProperty().get();
	}
	

	public final void setTelefono(final String telefono) {
		this.propietario.setTelefono(telefono);
		this.telefonoProperty().set(telefono);
	}
	

	public final StringProperty contactoReferenciaProperty() {
		return this.contactoReferencia;
	}
	

	public final String getContactoReferencia() {
		this.contactoReferenciaProperty().set(this.propietario.getContactoReferencia());
		return this.contactoReferenciaProperty().get();
	}
	

	public final void setContactoReferencia(final String contactoReferencia) {
		this.propietario.setContactoReferencia(contactoReferencia);
		this.contactoReferenciaProperty().set(contactoReferencia);
	}
	
	public final BooleanProperty selectedProperty() {
		return this.seleccionado;
	}
	

	public final boolean isSelected() {
		return this.selectedProperty().get();
	}
	

	public final void setSelected(final boolean selected) {
		this.selectedProperty().set(selected);
	}
	
	
}