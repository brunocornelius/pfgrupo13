package com.escritorio.entidadesFx;

import com.entidades.Indicador;
import com.entidades.IndicadorPotrero;
import com.excepciones.ProblemaDeConexionException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class IndicadorPotreroFx implements EntidadFx {

    private final StringProperty nombre;
    private final StringProperty desde;
    private final StringProperty hasta;
    private final BooleanProperty activo;
   
    private IndicadorPotrero indicadorPotrero;
    
    public IndicadorPotreroFx(IndicadorPotrero indicadorPotrero) {
    	this.indicadorPotrero = indicadorPotrero;
        this.nombre = new SimpleStringProperty(indicadorPotrero.getIndicador().getNombre());
        this.desde = new SimpleStringProperty(indicadorPotrero.getDesde().toString());
        if (indicadorPotrero.getHasta() == null) {
        	this.hasta = new SimpleStringProperty("");
        }else {
        	this.hasta = new SimpleStringProperty(indicadorPotrero.getHasta().toString());
        }
        
        this.activo = new SimpleBooleanProperty(indicadorPotrero.getActivo());
    }

    public String getNombre() {
    	return this.indicadorPotrero.getIndicador().getNombre();
    }

    public void setNombre(String nombre) {
        
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

	public IndicadorPotrero getIndicador() {
		return indicadorPotrero;
	}
	
	

	@Override
	public boolean losCamposSonValidos() throws ProblemaDeConexionException {
		return true;
	}

	public final StringProperty desdeProperty() {
		return this.desde;
	}
	

	public final String getDesde() {
		return this.indicadorPotrero.getDesde().toString();
	}
	

	public final void setDesde(final String desde) {
		this.desdeProperty().set(desde);
	}
	

	public final StringProperty hastaProperty() {
		return this.hasta;
	}
	

	public final String getHasta() {
		if (this.indicadorPotrero.getHasta() == null) {
			return "";
		}
		return this.indicadorPotrero.getHasta().toString();
	}
	

	public final void setHasta(final String hasta) {
		this.hastaProperty().set(hasta);
	}
	

	public final BooleanProperty activoProperty() {
		return this.activo;
	}
	

	public final boolean isActivo() {
		return this.indicadorPotrero.getActivo();
	}
	

	public final void setActivo(final boolean activo) {
		this.activoProperty().set(activo);
	}
	

}