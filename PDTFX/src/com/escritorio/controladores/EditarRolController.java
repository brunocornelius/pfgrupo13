package com.escritorio.controladores;

import com.escritorio.MainApp;
import com.escritorio.entidadesFx.RolFx;
import com.excepciones.PotrerosException;
import com.excepciones.ProblemaDeConexionException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditarRolController {

    @FXML
    private TextField nombreField;

    MainApp mainApp;

    private Stage dialogStage;
    private RolFx rol;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    

    public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public void setRol(RolFx p) {
        this.rol = p;
        nombreField.setText(p.getNombre());
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        try {
        	//Asigno los valores al RolFX
        	this.rol.setNombre(nombreField.getText());
			if (this.rol.losCamposSonValidos()) {
			    okClicked = true;
			    dialogStage.close();
			}
		} catch (PotrerosException e) {
			this.mainApp.mostrarAlerta(e, AlertType.WARNING);
			okClicked = false;
		}
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}