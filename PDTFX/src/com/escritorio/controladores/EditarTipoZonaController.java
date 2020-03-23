package com.escritorio.controladores;

import com.escritorio.MainApp;
import com.escritorio.entidadesFx.TipoZonaFx;
import com.excepciones.PotrerosException;
import com.util.ColorUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class EditarTipoZonaController {

    @FXML
    private TextField nombreField;
    @FXML
    private ColorPicker colorField;

    MainApp mainApp;
    private Stage dialogStage;
    private TipoZonaFx tipoZona;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTipoZona(TipoZonaFx p) {
        this.tipoZona = p;
        nombreField.setText(p.getNombre());
        colorField.setValue(Color.web(p.getColor()));
        
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        try {
        	//Asigno los valores al TipoZonaFX
        	this.tipoZona.setNombre(nombreField.getText());
        	this.tipoZona.setColor(ColorUtil.colorToHex(colorField.getValue()));
        	System.out.println("Se setió el color: " + this.tipoZona.getColor());
//        	this.tipoZona.setColor(this.colorField.getValue());
			if (this.tipoZona.losCamposSonValidos()) {
				this.okClicked = true;
			    dialogStage.close();
			}
		} catch (PotrerosException e) {
			this.mainApp.mostrarAlerta(e, AlertType.WARNING);
			this.okClicked = false;
		}
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}