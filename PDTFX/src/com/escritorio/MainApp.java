package com.escritorio;

import java.io.IOException;

import com.entidades.Usuario;
import com.escritorio.clientesbean.UsuariosClient;
import com.escritorio.controladores.LoginController;
import com.escritorio.controladores.MainController;
import com.excepciones.DatosInvalidosException;
import com.excepciones.PotrerosException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private final String rolAdmin = "admin";
    private final String rolMasterFiles = "master";
    private final String rolUsuarioInvitado = "Usuario invitado";
    
    Usuario usuario = null;

    public MainApp() {

    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Potreros de Nueva Helvecia");
        
/******************************************************************** 
 *  CARGAR EL ADMIN DE PRUEBA
 * ******************************************************************/
        try {
			UsuariosClient.getInstancia().cargarAdmindePrueba();
			System.out.println("Se dio de alta el usuario admin");
		} catch (PotrerosException e) {
			System.out.println("Ya existe el usuario admin");
		}
/********************************************************************/        
        
        showLoginLayout();
    }

    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("pantallas/MenuLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            Scene scene = new Scene(rootLayout);
            this.primaryStage.setScene(scene);
            this.primaryStage.show();
            
            MainController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(this.primaryStage);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean showLoginLayout() {
    	try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("pantallas/loginLayout.fxml"));

			AnchorPane vistaLogin = (AnchorPane) loader.load();
            
         // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Ingreso al sistema");
            dialogStage.initModality(Modality.WINDOW_MODAL);
//            dialogStage.initOwner(this.pantallaPrincipal);
            Scene scene = new Scene(vistaLogin);
            dialogStage.setScene(scene);
            
            LoginController loginController = loader.getController();
            loginController.setMainApp(this);
            loginController.setDialogStage(dialogStage);

            dialogStage.showAndWait();
            
            return loginController.isOkClicked();
            
            
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public BorderPane getRootLayout() {
		return rootLayout;
	}

	public void setRootLayout(BorderPane rootLayout) {
		this.rootLayout = rootLayout;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void mostrarAlerta(PotrerosException e, AlertType tipoDeAlerta) {
    	Alert alert = new Alert(tipoDeAlerta);
        alert.initOwner(this.getPrimaryStage());
        alert.setTitle(e.getTitulo());
        alert.setHeaderText(e.getCabecera());
        alert.setContentText(e.getMessage());
        alert.showAndWait();
//        alert.show();
    }
	
	public void mostrarAlerta(String titulo, String header, String contenido, AlertType tipoDeAlerta) {
    	Alert alert = new Alert(tipoDeAlerta);
        alert.initOwner(this.getPrimaryStage());
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
//        alert.show();
    }

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) throws DatosInvalidosException {
		if (usuario == null) {
			throw new DatosInvalidosException("El usuario logueado no puede ser null");
		}
		this.usuario = usuario;
	}

	public String getRolAdmin() {
		return rolAdmin;
	}

	public String getRolMasterFiles() {
		return rolMasterFiles;
	}

	public String getRolUsuario() {
		return rolUsuarioInvitado;
	}
	
	
	
	
}