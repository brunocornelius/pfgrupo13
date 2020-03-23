package com.escritorio.controladores;

import com.entidades.Usuario;
import com.escritorio.MainApp;
import com.escritorio.clientesbean.UsuariosClient;
import com.excepciones.DatosInvalidosException;
import com.excepciones.PotrerosException;
import com.excepciones.ProblemaDeConexionException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
	
	@FXML
    private TextField usuario;
	
    @FXML
    private TextField password;
    
    @FXML
    private Label mensajeError;
    
    private boolean okClicked = false;
    
    private Stage dialogStage;
    
    private MainApp mainApp;

	public LoginController() {
		
	}

	@FXML
    private void initialize() {
		
    }
	
	public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage; 
        System.out.println("Se setió el dialogStage de LoginController");
    }
	
	public boolean isOkClicked() {
        return this.okClicked;
    }
	
	@FXML
    private void handleOk() {
		String username = this.usuario.getText();
		String password = this.password.getText();
		
		System.out.println("Usuario: " + this.usuario.getText());
		System.out.println("Password: " + this.password.getText());
        
		try {
			Usuario usuario = UsuariosClient.getInstancia().obtenerUsuario(username, password);
			System.out.println("Se obtuvo el usuario");
			this.mainApp.setUsuario(usuario);
			this.getMainApp().initRootLayout();
			dialogStage.close();
			okClicked = true;
		} catch (PotrerosException e) {
			this.mensajeError.setText(e.getMessage());
//			e.printStackTrace();
			okClicked = false;
		} 
        
    }
	
	@FXML
    private void handleCancel() {
		System.out.println("El usuario clickeo Salir");
        dialogStage.close();
    }

	public Stage getDialogStage() {
		return dialogStage;
	}

	public void setOkClicked(boolean okClicked) {
		this.okClicked = okClicked;
	}

	public MainApp getMainApp() {
		return mainApp;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	
	
}
