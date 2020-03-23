package com.escritorio.controladores;

import com.escritorio.MainApp;
import com.escritorio.entidadesFx.PropietarioFx;
import com.excepciones.DatosInvalidosException;
import com.excepciones.PotrerosException;
import com.excepciones.ProblemaDeConexionException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditarPropietarioController {

    @FXML
    private TextField nombreField;
    @FXML
    private TextField rutField;
    @FXML
    private TextField idField;
    @FXML
    private TextField razonSocialField;
    @FXML
    private TextField direccionField;
    @FXML
    private TextField correoField;
    @FXML
    private TextField telefonoField;
    @FXML
    private TextField contactoField;

    MainApp mainApp;

    private Stage dialogStage;
    private PropietarioFx propietario;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setPropietario(PropietarioFx propietarioFx) {
        this.propietario = propietarioFx;
        nombreField.setText(propietarioFx.getNombre());
        rutField.setText(Long.toString(propietarioFx.getRut()));
        razonSocialField.setText(propietarioFx.getRazonSocial());
        direccionField.setText(propietarioFx.getDireccion());
        correoField.setText(propietarioFx.getCorreo());
        telefonoField.setText(propietarioFx.getTelefono());
        contactoField.setText(propietarioFx.getContactoReferencia());
    }

    public boolean isOkClicked() {
        return okClicked;
    }

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@FXML
    private void handleOk() {
        try {
        	//Asigno los valores al PropietarioFX
        	this.propietario.setNombre(nombreField.getText());

    		this.propietario.setRut(Long.parseLong(rutField.getText()));
    		this.propietario.setRazonSocial(razonSocialField.getText());
		    this.propietario.setDireccion(direccionField.getText());
		    this.propietario.setCorreo(correoField.getText());
		    this.propietario.setTelefono(telefonoField.getText());
		    this.propietario.setContactoReferencia(contactoField.getText());
		    
		    this.propietario.losCamposSonValidos();
		    
		    okClicked = true;
		    dialogStage.close();
			
    	}catch (NumberFormatException e) {
			this.mainApp.mostrarAlerta(new DatosInvalidosException("El campo RUT solo admite números",e), AlertType.ERROR);
		}catch (PotrerosException e) {
			this.mainApp.mostrarAlerta(e, AlertType.WARNING);
			okClicked = false;
		}

    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    
    
}