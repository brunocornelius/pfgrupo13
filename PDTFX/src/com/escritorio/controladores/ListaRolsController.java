package com.escritorio.controladores;

import java.io.IOException;
import com.escritorio.MainApp;
import com.escritorio.clientesbean.RolsClient;
import com.escritorio.entidadesFx.RolFx;
import com.excepciones.PotrerosException;
import com.excepciones.ProblemaDeConexionException;
import com.excepciones.SinSeleccionException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ListaRolsController {
	
	//Lista de todos los RolFx
	private ObservableList<RolFx> rolsData = FXCollections.observableArrayList();
	FilteredList<RolFx> filteredData; 
	@FXML 
    private TextField searchTextField;
	
    @FXML
    private TableView<RolFx> rolTable;
    @FXML
    private TableColumn<RolFx, String> nombreColumn;

    @FXML
    private Label nombreLabel;
    
    @FXML
    private Label idLabel;

    private MainApp mainApp;
    
    private RolsClient rolsClient;

    public ListaRolsController() throws ProblemaDeConexionException{
    	this.rolsClient = RolsClient.getInstancia();
    	this.rolsData = this.rolsClient.obtenerTodos();
    }
    
    public void inicializar() {
    	
    }
    
    public ObservableList<RolFx> getRolsData() {
        return rolsData;
    }

    @FXML
    private void initialize() {
        nombreColumn.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        
        showDetallesRol(null);

        rolTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDetallesRol(newValue));
        
    }


    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        rolTable.setItems(this.rolsData);
    }
    

    private void showDetallesRol(RolFx rolFx) {
        if (rolFx != null) {
            nombreLabel.setText(rolFx.getNombre());
            idLabel.setText(Integer.toString(rolFx.getId()));
        } else {
            nombreLabel.setText("");
            idLabel.setText("");
        }
    }
    
    @FXML
    private void handleNuevoRol() {
        RolFx tempRolFx = new RolFx();
        boolean okClicked = this.showEditarRolDialog(tempRolFx);
        //El método showEditarRolDialog, queda esperando a que se presione algo
        if (okClicked) {
        	try {
				tempRolFx = this.rolsClient.altaDeRol(tempRolFx);
				this.rolsData.add(tempRolFx);
				rolTable.getSelectionModel().select(tempRolFx);
				this.showDetallesRol(tempRolFx);
				this.mainApp.mostrarAlerta("Roles", "Alta de Rol", "Se dio de alta el rol " + tempRolFx.getNombre(),AlertType.INFORMATION);
			} catch (PotrerosException e) {
				this.mainApp.mostrarAlerta(e, AlertType.ERROR);
			}
        	
        }
    }
    @FXML
    private void handleEditarRol() {
        RolFx rolSeleccionado = rolTable.getSelectionModel().getSelectedItem();
        int indice = rolTable.getSelectionModel().getSelectedIndex();
        if (rolSeleccionado != null) {
            boolean okClicked = this.showEditarRolDialog(rolSeleccionado);
            //El método showEditarRolDialog, queda esperando a que se presione algo
            if (okClicked) {
            	//Si se presionó el botón Ok
            	try {
            		
					rolSeleccionado = this.rolsClient.editarRol(rolSeleccionado);
					//solo si pasa el ditarRol del bean
					this.rolsData.remove(rolSeleccionado);
					this.rolsData.add(indice,rolSeleccionado);
					rolTable.getSelectionModel().select(rolSeleccionado);
					this.mainApp.mostrarAlerta("Roles","Editar Rol", "Se editó correctamente el rol " + rolSeleccionado.getNombre(),AlertType.INFORMATION);
	                showDetallesRol(rolSeleccionado);
				} catch (PotrerosException e) {
					this.mainApp.mostrarAlerta(e, AlertType.ERROR);
				}
				
            }

        } else {
        	SinSeleccionException s = new SinSeleccionException("Por favor selecciona un Rol");
            this.mainApp.mostrarAlerta(s, AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleBorrarRol() {
    	RolFx rolSeleccionado = rolTable.getSelectionModel().getSelectedItem();
    	if (rolSeleccionado != null) {
            try {
				if (this.rolsClient.borrarRol(rolSeleccionado)) {
					//Solo si se puede borrar del server lo borro de la tabla
					this.rolsData.remove(rolSeleccionado);
					this.mainApp.mostrarAlerta("Roles", "Borrar rol", "Se borró el rol " + rolSeleccionado.getNombre(), AlertType.INFORMATION);
				}
				
			} catch (PotrerosException e) {
				this.mainApp.mostrarAlerta(e, AlertType.ERROR);
			}
        } else {
        	SinSeleccionException s = new SinSeleccionException("Por favor selecciona un Rol");
            this.mainApp.mostrarAlerta(s, AlertType.ERROR);
        }
    }
    
    public boolean showEditarRolDialog(RolFx rolFx) {
        try {
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("pantallas/editarRolDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Rol");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(this.mainApp.getPrimaryStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            EditarRolController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setRol(rolFx);
            controller.setMainApp(this.mainApp);
            dialogStage.showAndWait();

            return controller.isOkClicked();
            
        } catch (IOException e) {
            return false;
        }
    }
}