package com.escritorio.controladores;

import java.io.IOException;
import com.escritorio.MainApp;
import com.escritorio.clientesbean.TipoZonasClient;
import com.escritorio.entidadesFx.TipoZonaFx;
import com.excepciones.NoAutorizadoException;
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
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ListaTipoZonasController {
	
	//Lista de todos los TipoZonaFx
	private ObservableList<TipoZonaFx> tipoZonasData = FXCollections.observableArrayList();
	FilteredList<TipoZonaFx> filteredData; 
	@FXML 
    private TextField searchTextField;
	
    @FXML
    private TableView<TipoZonaFx> tipoZonaTable;
    @FXML
    private TableColumn<TipoZonaFx, String> nombreColumn;

    @FXML
    private Label nombreLabel;
    
    @FXML
    private Label idLabel;
    
    @FXML
    private Label colorLabel;
    
    @FXML
    private Circle circulo;

    private MainApp mainApp;
    
    private TipoZonasClient tipoZonasClient;

    public ListaTipoZonasController() throws ProblemaDeConexionException{
    	this.tipoZonasClient = TipoZonasClient.getInstancia();
    	this.tipoZonasData = this.tipoZonasClient.obtenerTodos();
    }
    
    public void inicializar() {
    	
    }
    
    public ObservableList<TipoZonaFx> getTipoZonasData() {
        return tipoZonasData;
    }

    @FXML
    private void initialize() {
    
        nombreColumn.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        
        showDetallesTipoZona(null);

        tipoZonaTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDetallesTipoZona(newValue));
    }


    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        tipoZonaTable.setItems(this.tipoZonasData);
    }
    

    private void showDetallesTipoZona(TipoZonaFx tipoZona) {
        if (tipoZona != null) {
            nombreLabel.setText(tipoZona.getNombre());
            idLabel.setText(Integer.toString(tipoZona.getId()));
            colorLabel.setText(tipoZona.getColor());
//            colorLabel.setTextFill(Color.web(tipoZona.getColor().toString()));
            circulo.setFill(Color.web(tipoZona.getColor()));
            
        } else {
            nombreLabel.setText("");
            idLabel.setText("");
            colorLabel.setText("");
            circulo.setFill(null);
        }
    }
    
    @FXML
    private void handleNuevoTipoZona() {
    	
        TipoZonaFx tempTipoZonaFx = new TipoZonaFx();
        boolean okClicked = this.showEditarTipoZonaDialog(tempTipoZonaFx);
        //El método showEditarTipoZonaDialog, queda esperando a que se presione algo
        if (okClicked) {
        	try {
				tempTipoZonaFx = this.tipoZonasClient.altaDeTipoZona(tempTipoZonaFx);
				this.tipoZonasData.add(tempTipoZonaFx);
				this.mainApp.mostrarAlerta("Tipo de zonas", "Alta de tipo de zonas", "Se dio de alta el tipo de zona " + tempTipoZonaFx.getNombre(), AlertType.INFORMATION);
				this.tipoZonaTable.getSelectionModel().select(tempTipoZonaFx);
			} catch (PotrerosException e) {
				this.mainApp.mostrarAlerta(e, AlertType.ERROR);
			}
        	
        }
    }
    @FXML
    private void handleEditarTipoZona() {
        TipoZonaFx tipoZonaSeleccionado = tipoZonaTable.getSelectionModel().getSelectedItem();
        int indice = tipoZonaTable.getSelectionModel().getSelectedIndex();
        if (tipoZonaSeleccionado == null) {
            this.mainApp.mostrarAlerta(new SinSeleccionException("Por favor selecciona un TipoZona"), AlertType.WARNING);
        }
        
        boolean okClicked = this.showEditarTipoZonaDialog(tipoZonaSeleccionado);
        //El método showEditarTipoZonaDialog, queda esperando a que se presione algo
        if (okClicked) {
        	//Si se presionó el botón Ok
        	try {
        		this.tipoZonasData.remove(tipoZonaSeleccionado);
				tipoZonaSeleccionado = this.tipoZonasClient.editarTipoZona(tipoZonaSeleccionado);
				this.tipoZonasData.add(indice, tipoZonaSeleccionado);
				this.tipoZonaTable.getSelectionModel().select(indice);
				this.mainApp.mostrarAlerta("Tipo de zonas", "Edicion de tipo de zonas", "Se editó el tipo de zona " + tipoZonaSeleccionado.getNombre(), AlertType.INFORMATION);
                showDetallesTipoZona(tipoZonaSeleccionado);
			} catch (PotrerosException e) {
				this.mainApp.mostrarAlerta(e, AlertType.WARNING);
			}
			
        }else {
        	this.mainApp.mostrarAlerta("Tipo de zonas", "Edicion de tipo de zonas", "Se canceló la edicion del tipo de zona " + tipoZonaSeleccionado.getNombre(), AlertType.INFORMATION);
        }
    }
    
    @FXML
    private void handleBorrarTipoZona() {
    	TipoZonaFx tipoZonaSeleccionado = tipoZonaTable.getSelectionModel().getSelectedItem();
    	if (tipoZonaSeleccionado == null) {
            this.mainApp.mostrarAlerta(new SinSeleccionException("Por favor selecciona un TipoZona"), AlertType.WARNING);
        }
    	
        try {
			if (this.tipoZonasClient.borrarTipoZona(tipoZonaSeleccionado)) {
				//Solo si se puede borrar del server lo borro de la tabla
				this.tipoZonasData.remove(tipoZonaSeleccionado);
				this.mainApp.mostrarAlerta("Tipo de zonas", "Borrado de tipo de zonas", "Se borró el tipo de zona " + tipoZonaSeleccionado.getNombre(), AlertType.INFORMATION);
			}
			
		} catch (PotrerosException e) {
			this.mainApp.mostrarAlerta(e, AlertType.WARNING);
		}
    }
    
    
    public boolean showEditarTipoZonaDialog(TipoZonaFx tipoZonaFx) {
        try {
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("pantallas/editarTipoZonaDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar TipoZona");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(this.mainApp.getPrimaryStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            EditarTipoZonaController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setTipoZona(tipoZonaFx);
            
            dialogStage.showAndWait();

            return controller.isOkClicked();
            
        } catch (IOException e) {
            return false;
        }
    }
}