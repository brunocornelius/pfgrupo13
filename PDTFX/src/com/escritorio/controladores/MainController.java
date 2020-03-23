package com.escritorio.controladores;

import java.io.IOException;
import com.escritorio.MainApp;
import com.escritorio.clientesbean.AltasAutomaticasClient;
import com.excepciones.DatosInvalidosException;
import com.excepciones.NoAutorizadoException;
import com.excepciones.NoExisteElementoException;
import com.excepciones.PotrerosException;
import com.excepciones.ProblemaDeConexionException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class MainController {
    
    private Stage dialogStage;
    	
    private MainApp mainApp;
    	
	public MainController() {
		
	}

	@FXML
    private void initialize() {
		
    }
	
	public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage; 
        System.out.println("Se setió el dialogStage de MainController");
    }
	
	@FXML
    private void handleAcercaDe() {
		
		try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("pantallas/acercaDeLayout.fxml"));
            AnchorPane acercaDe = (AnchorPane) loader.load();

            this.mainApp.getRootLayout().setCenter(acercaDe);
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }

		
    }
	
	@FXML
    private void handleCargarDatos() {
		
		try {

			AltasAutomaticasClient.getInstancia().altaAutomaticaTipoZonas();
			AltasAutomaticasClient.getInstancia().altaAutomaticaIndicadores();
			AltasAutomaticasClient.getInstancia().altaAutomaticaPropietarios();
			AltasAutomaticasClient.getInstancia().altaAutomaticaPredios();
            
        } catch (PotrerosException e ) {
            this.mainApp.mostrarAlerta(e, AlertType.ERROR);
        }

		
    }
	
	@FXML
    private void handleSalir() {
		System.out.println("El usuario clickeo en Salir");
		
		this.dialogStage.close();
		this.mainApp.showLoginLayout();
    }
	
	@FXML
    private void handleListadoPropietarios() {
		System.out.println("El usuario clickeo en Listado de Propietarios");
		if (this.mainApp.getUsuario().tieneRol("admin")) {
			try {
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(MainApp.class.getResource("pantallas/listadoPropietarios.fxml"));
	            AnchorPane listaPropietarios = (AnchorPane) loader.load();

	            this.mainApp.getRootLayout().setCenter(listaPropietarios);
	            ListaPropietariosController controller = loader.getController();
	            controller.setMainApp(this.mainApp);
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}else {
			this.mainApp.mostrarAlerta(new NoAutorizadoException("Listado de usuarios"), AlertType.WARNING);
		}
		
    }
	
	@FXML
    private void handleListadoUsuarios() {
		System.out.println("El usuario clickeo en Listado de Usuarios");
		if (this.mainApp.getUsuario().tieneRol("admin")) {
			try {
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(MainApp.class.getResource("pantallas/listadoUsuarios.fxml"));
	            AnchorPane listaPropietarios = (AnchorPane) loader.load();
	
	            this.mainApp.getRootLayout().setCenter(listaPropietarios);
	            ListaUsuariosController controller = loader.getController();
	            controller.setMainApp(this.mainApp);
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}else {
			this.mainApp.mostrarAlerta(new NoAutorizadoException("Listado de usuarios"), AlertType.WARNING);
		}
    }
	
	@FXML
    private void handleListadoRols() {
		
		if (this.mainApp.getUsuario().tieneRol("admin")) {
			System.out.println("El usuario clickeo en Listado de Roles");
			try {
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(MainApp.class.getResource("pantallas/listadoRols.fxml"));
	            AnchorPane listaRols = (AnchorPane) loader.load();
	
	            this.mainApp.getRootLayout().setCenter(listaRols);
	            ListaRolsController controller = loader.getController();
	            controller.setMainApp(this.mainApp);
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}else {
			this.mainApp.mostrarAlerta(new NoAutorizadoException("Listado de roles"), AlertType.WARNING);
		}
    }
	
	@FXML
    private void handleListadoTipoZonas() {
		System.out.println("El usuario clickeo en Listado de TipoZonas");
		try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("pantallas/listadoTipoZonas.fxml"));
            AnchorPane listaTipoZonasAnchorPane = (AnchorPane) loader.load();

            this.mainApp.getRootLayout().setCenter(listaTipoZonasAnchorPane);
            ListaTipoZonasController controller = loader.getController();
            controller.setMainApp(this.mainApp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	@FXML
    private void handleListadoIndicadores() {
		System.out.println("El usuario clickeo en Listado de Indicadores");
		try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("pantallas/listadoIndicadores.fxml"));
            AnchorPane listaIndicadoresAnchorPane = (AnchorPane) loader.load();

            this.mainApp.getRootLayout().setCenter(listaIndicadoresAnchorPane);
            ListaIndicadoresController controller = loader.getController();
            controller.setMainApp(this.mainApp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	@FXML
    private void handleListadoArbolIndicadores() {
		System.out.println("El usuario clickeo en Listado de Indicadores");
		try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("pantallas/listadoArbolIndicadores.fxml"));
            AnchorPane listaIndicadoresAnchorPane = (AnchorPane) loader.load();

            this.mainApp.getRootLayout().setCenter(listaIndicadoresAnchorPane);
            ListaIndicadoresArbolController controller = loader.getController();
            try {
				controller.setMainApp(this.mainApp);
			} catch (ProblemaDeConexionException | NoExisteElementoException | DatosInvalidosException e) {
				this.mainApp.mostrarAlerta(e, AlertType.WARNING);
			}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	@FXML
    public void handleListadoPredios() {
		System.out.println("El usuario clickeo en Listado de Predios");
		try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("pantallas/listadoPredios.fxml"));
            AnchorPane listaPrediosAnchorPane = (AnchorPane) loader.load();
            
            
            
            this.mainApp.getRootLayout().setCenter(listaPrediosAnchorPane);
            ListaPrediosController controller = loader.getController();
            controller.setMainApp(this.mainApp, this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public MainApp getMainApp() {
		return mainApp;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public Stage getDialogStage() {
		return dialogStage;
	}

	
	
	
	
}
