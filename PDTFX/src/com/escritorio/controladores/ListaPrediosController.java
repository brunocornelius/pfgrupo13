package com.escritorio.controladores;

import java.io.IOException;

import javax.naming.NamingException;

import com.entidades.Indicador;
import com.escritorio.MainApp;
import com.escritorio.clientesbean.IndicadorsClient;
import com.escritorio.clientesbean.PotrerosClient;
import com.escritorio.clientesbean.PrediosClient;
import com.escritorio.entidadesFx.PredioFx;
import com.excepciones.DatosInvalidosException;
import com.excepciones.NoAutorizadoException;
import com.excepciones.NoExisteElementoException;
import com.excepciones.PotrerosException;
import com.excepciones.ProblemaDeConexionException;
import com.excepciones.SinSeleccionException;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class ListaPrediosController {
	
	//Lista de todos los PredioFx
	private ObservableList<PredioFx> prediosData = FXCollections.observableArrayList();
	FilteredList<PredioFx> filteredData; 
	@FXML 
    private TextField searchTextField;
	
    @FXML
    private TableView<PredioFx> predioTable;
    @FXML
    private TableColumn<PredioFx, String> nombreColumn;
    @FXML
    private TableColumn<PredioFx, String> descripcionColumn;
    @FXML
    TableColumn<PredioFx, Boolean> activoColumn;
    
    @FXML
    private Label nombreLabel;
    
    @FXML
    private Label descripcionLabel;
    
    @FXML
    private Label idLabel;
    
    @FXML
    private Label propietariosLabel;
    
    @FXML
    private Label extensionLabel;

    private MainApp mainApp;
    
    private MainController mainController;
    
    private PrediosClient prediosClient;
    
    @FXML
    private TreeView<Indicador> indicadoresTreeView;
    
    private IndicadorsClient indicadorsClient;


    public ListaPrediosController() throws ProblemaDeConexionException{
    	
    	this.indicadorsClient = IndicadorsClient.getInstancia();
    	this.prediosClient = PrediosClient.getInstancia();
		this.prediosData = this.prediosClient.obtenerTodos();
		this.filteredData = new FilteredList<PredioFx>(this.prediosData, p -> true);
		System.out.println("Constructor ListaPrediosController");

    }
    
    public ObservableList<PredioFx> getPrediosData() {
        return prediosData;
    }

    @FXML
    private void initialize() {
        nombreColumn.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        descripcionColumn.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());
        activoColumn.setCellValueFactory(cell -> {
        	PredioFx p = cell.getValue();
            return new ReadOnlyBooleanWrapper(p.isActivo());
        });
        activoColumn.setCellFactory(CheckBoxTableCell.forTableColumn(activoColumn));
        
        showDetallesPredio(null);

        predioTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDetallesPredio(newValue));
        
        searchTextField.setPromptText("Buscar...");
        searchTextField.textProperty().addListener((prop, old, text) -> {
            filteredData.setPredicate(predioFx -> {
                if(text == null || text.isEmpty()) return true;
                return predioFx.getNombre().toLowerCase().contains(text.toLowerCase());
            });
        });
    }


    public void setMainApp(MainApp mainApp, MainController mainController) {
        this.mainApp = mainApp;
        this.mainController = mainController;
        predioTable.setItems(this.filteredData);
    }
    

    private void showDetallesPredio(PredioFx predio) {
        if (predio != null) {
        	
        	 TreeItem<Indicador> indicadorRaiz;
			try {

				indicadorRaiz = this.indicadorsClient.obtenerIndicadorRaizParaPredio(predio);
				indicadoresTreeView.setRoot(indicadorRaiz);
			} catch (ProblemaDeConexionException | NoExisteElementoException | DatosInvalidosException e) {
				// TODO Auto-generated catch block
				indicadoresTreeView.setRoot(null);
				this.mainApp.mostrarAlerta(e, AlertType.WARNING);
				e.printStackTrace();
			}
             
             
            nombreLabel.setText(predio.getNombre());
            descripcionLabel.setText(predio.getDescripcion());
            idLabel.setText(Integer.toString(predio.getId()));
            propietariosLabel.setText(predio.getPropietariosString());
            extensionLabel.setText(Double.toString(predio.getArea()));
        } else {
            nombreLabel.setText("");
            descripcionLabel.setText("");
            idLabel.setText("");
            propietariosLabel.setText("");
            extensionLabel.setText("");
        }
    }
    
    @FXML
    private void handleNuevoPredio() {
    	
    	if (!this.mainApp.getUsuario().tieneRol("admin")) {
    		this.mainApp.mostrarAlerta(new NoAutorizadoException("No tiene autorización para crear un nuevo predio"), AlertType.ERROR);
            return;
        }
    	
    	PredioFx tempPredioFx = new PredioFx();
		tempPredioFx.setMapaEditable(true);
        boolean okClicked = this.showEditarPredioDialog(tempPredioFx);
        //El método showEditarPredioDialog, queda esperando a que se presione algo
        if (okClicked) {
        	try {
				tempPredioFx = this.prediosClient.altaDePredio(tempPredioFx);
				this.prediosData.add(tempPredioFx);
				this.predioTable.getSelectionModel().select(tempPredioFx);
				this.mainApp.mostrarAlerta("Predios", "Alta de predio", "Se dio de alta el predio " + tempPredioFx.getNombre(), AlertType.INFORMATION);
			} catch (PotrerosException e) {
				this.mainApp.mostrarAlerta(e, AlertType.WARNING);
				e.printStackTrace();
			}
        	
        }else {
        	this.mainApp.mostrarAlerta("Predios", "Alta de predio", "Se canceló alta del predio ", AlertType.INFORMATION);
        }
        
    }
    @FXML
    private void handleEditarPredio() {
    	if (!this.mainApp.getUsuario().tieneRol("admin")) {
            this.mainApp.mostrarAlerta(new NoAutorizadoException("No tiene autorización para realizar edicion de predio"), AlertType.ERROR);
            return;
    	}
    	
    	PredioFx predioSeleccionado = predioTable.getSelectionModel().getSelectedItem();
    	if (predioSeleccionado == null) {
    		SinSeleccionException s = new SinSeleccionException("Por favor selecciona un Predio");
            this.mainApp.mostrarAlerta(s, AlertType.WARNING);
            return;
    	}
    	
		int indice = predioTable.getSelectionModel().getSelectedIndex();        	
    	try {
			predioSeleccionado.setMapaEditable(this.prediosClient.formaEditable(predioSeleccionado));
			boolean okClicked = this.showEditarPredioDialog(predioSeleccionado);
            //El método showEditarPredioDialog, queda esperando a que se presione algo
            if (okClicked) {
            	//Si se presionó el botón Ok
            	try {
            		this.prediosData.remove(predioSeleccionado);
					predioSeleccionado = this.prediosClient.editarPredio(predioSeleccionado);
					
					this.prediosData.add(indice,predioSeleccionado);
					this.predioTable.getSelectionModel().select(indice);
	                showDetallesPredio(predioSeleccionado);
	                this.mainApp.mostrarAlerta("Predios", "Edicion de predio", "Se guardaron los cambios del predio " + predioSeleccionado.getNombre(), AlertType.INFORMATION);
					
				} catch (PotrerosException e) {
					this.mainApp.mostrarAlerta(e, AlertType.WARNING);
					this.mainController.handleListadoPredios();
    						e.printStackTrace();
				}
				
            }else {
            	this.mainApp.mostrarAlerta("Predios", "Edicion de predio", "Se canceló edicion del predio ", AlertType.INFORMATION);
            }
		} catch (PotrerosException e) {
			// TODO Auto-generated catch block
			this.mainApp.mostrarAlerta(e, AlertType.ERROR);
			this.prediosData.add(indice,predioSeleccionado);
			e.printStackTrace();
		} 
    }
    
    @FXML
    private void handleEditarZonasPredio() {
        PredioFx predioSeleccionado = predioTable.getSelectionModel().getSelectedItem();
        
        if (!this.mainApp.getUsuario().tieneRol("master")) {
            this.mainApp.mostrarAlerta(new NoAutorizadoException("No tiene autorización para editar las zonas geográficas del predio"), AlertType.ERROR);
            return;
    	}
        
        if (predioSeleccionado == null) {
        	SinSeleccionException s = new SinSeleccionException("Por favor selecciona un Predio");
            this.mainApp.mostrarAlerta(s, AlertType.WARNING);   
            return;
        }
        
        int indice = predioTable.getSelectionModel().getSelectedIndex();
        
        boolean okClicked = this.showEditarZonasPredioDialog(predioSeleccionado);
        //El método showEditarPredioDialog, queda esperando a que se presione algo
        if (okClicked) {
        	//Si se presionó el botón Ok
    		this.prediosData.remove(predioSeleccionado);
			this.prediosData.add(indice,predioSeleccionado);
			this.predioTable.getSelectionModel().select(indice);
            showDetallesPredio(predioSeleccionado);
            this.mainApp.mostrarAlerta("Predio", "Editar ZonasGeograficas predio", "Se guardaron los cambios de Zonas geograficas", AlertType.INFORMATION);
        }else {
        	this.mainApp.mostrarAlerta("Predios", "Editar ZonasGeograficas predio", "Se canceló edicion ZonasGeograficas predio", AlertType.INFORMATION);
        }
    }
    
    @FXML
    private void handleEditarPotrerosPredio() {
        PredioFx predioSeleccionado = predioTable.getSelectionModel().getSelectedItem();
        if (!this.mainApp.getUsuario().tieneRol("master")) {
            this.mainApp.mostrarAlerta(new NoAutorizadoException("No tiene autorización para editar los potreros del predio"), AlertType.ERROR);
            return;
    	}
        
        if (predioSeleccionado == null) {
        	SinSeleccionException s = new SinSeleccionException("Por favor selecciona un Predio");
            this.mainApp.mostrarAlerta(s, AlertType.WARNING);   
            return;
        }
        int indice = predioTable.getSelectionModel().getSelectedIndex();

        boolean okClicked = this.showEditarPotrerosPredioDialog(predioSeleccionado);
        //El método showEditarPredioDialog, queda esperando a que se presione algo
        if (okClicked) {
        	//Si se presionó el botón Ok

    		this.prediosData.remove(predioSeleccionado);
    		//se podría recargar el predio...
			this.prediosData.add(indice,predioSeleccionado);
			this.predioTable.getSelectionModel().select(indice);
			this.mainApp.mostrarAlerta("Predio", "Editar potreros predio", "Se guardaron los cambios de potreros", AlertType.INFORMATION);
            showDetallesPredio(predioSeleccionado);
        }else {
        	this.mainApp.mostrarAlerta("Predios", "Editar potreros predio", "Se canceló edicion potreros predio", AlertType.INFORMATION);
        }

    }
    
    @FXML
    private void handleBorrarPredio() {
    	
    	if (!this.mainApp.getUsuario().tieneRol("admin")) {
            this.mainApp.mostrarAlerta(new NoAutorizadoException("No tiene autorización para borrar/desactivar predio"), AlertType.ERROR);
            return;
    	}
    	PredioFx predioSeleccionado = predioTable.getSelectionModel().getSelectedItem();
        if (predioSeleccionado == null) {
        	SinSeleccionException s = new SinSeleccionException("Por favor selecciona un Predio");
            this.mainApp.mostrarAlerta(s, AlertType.WARNING);   
            return;
        }
        
        if (!predioSeleccionado.getPredio().getActivo()) {
        	this.mainApp.mostrarAlerta("PREDIOS", "Activar predio", "El predio se encuentra desactivo, y no se puede borrar", AlertType.ERROR);
        	return;
        }
			 
		 int indice = predioTable.getSelectionModel().getSelectedIndex();

        //El método showEditarPredioDialog, queda esperando a que se presione algo
    	//Se presionó el botón Borrar
    	try {
    		this.prediosData.remove(predioSeleccionado);
			predioSeleccionado = this.prediosClient.borrarPredio(predioSeleccionado);
			if (predioSeleccionado != null) {
				this.prediosData.add(indice,predioSeleccionado);
				this.predioTable.getSelectionModel().select(indice);
				this.mainApp.mostrarAlerta("Predio", "Borrar predio", "Se desactivó el predio", AlertType.INFORMATION);
                showDetallesPredio(predioSeleccionado);
			}else {
				this.mainApp.mostrarAlerta("Predio", "Borrar predio", "Se borró completamente el predio", AlertType.INFORMATION);
				showDetallesPredio(null);
			}
			
		} catch (PotrerosException e) {
			this.mainApp.mostrarAlerta(e, AlertType.ERROR);
			this.prediosData.add(indice,predioSeleccionado);
		}  	
    	
    }
    
    @FXML
    private void handleActivar() {
    	
    	if (!this.mainApp.getUsuario().tieneRol("admin")) {
            this.mainApp.mostrarAlerta(new NoAutorizadoException("No tiene autorización para activar predio"), AlertType.ERROR);
            return;
    	}
    	PredioFx predioSeleccionado = predioTable.getSelectionModel().getSelectedItem();
        if (predioSeleccionado == null) {
        	SinSeleccionException s = new SinSeleccionException("Por favor selecciona un Predio");
            this.mainApp.mostrarAlerta(s, AlertType.WARNING);   
            return;
        }
        if (predioSeleccionado.getPredio().getActivo()) {
        	this.mainApp.mostrarAlerta("PREDIOS", "Activar predio", "El predio ya se encuentra activo", AlertType.ERROR);
        	return;
        }
			 
		 int indice = predioTable.getSelectionModel().getSelectedIndex();

        //El método showEditarPredioDialog, queda esperando a que se presione algo
    	//Se presionó el botón Borrar
    	try {
    		this.prediosData.remove(predioSeleccionado);
			predioSeleccionado = this.prediosClient.activarPredio(predioSeleccionado);
			this.prediosData.add(indice,predioSeleccionado);
			this.predioTable.getSelectionModel().select(indice);
			this.mainApp.mostrarAlerta("Predio", "Activar predio", "Se activó el predio", AlertType.INFORMATION);
            showDetallesPredio(predioSeleccionado);
		} catch (PotrerosException e) {
			this.mainApp.mostrarAlerta(e, AlertType.ERROR);
			this.prediosData.add(indice,predioSeleccionado);
		}  	
    	
    }
    
    public boolean showEditarPredioDialog(PredioFx predioFx) {
        try {
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("pantallas/editarPredioDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Predio");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(this.mainApp.getPrimaryStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            EditarPredioController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPredio(predioFx);
            controller.setMainApp(this.mainApp);
            dialogStage.showAndWait();

            return controller.isOkClicked();
            
        } catch ( PotrerosException e) {
            this.mainApp.mostrarAlerta(e, AlertType.WARNING);;
        } catch (IOException e) {
			this.mainApp.mostrarAlerta(new DatosInvalidosException("No se puede abrir el fxml  asociado" ), AlertType.WARNING);
		}
        return false;
    }
    public boolean showEditarZonasPredioDialog(PredioFx predioFx) {
        try {
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("pantallas/listadoZonaGeograficasPredio.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Zonas Predio");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(this.mainApp.getPrimaryStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            ListaZonaGeograficasController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPredio(predioFx);
            controller.setMainApp(mainApp);
            dialogStage.showAndWait();

            return controller.isOkClicked();
            
        } catch (Exception e) {
        	PotrerosException pe =  new PotrerosException("Error", "Error de apertura de archivo", "\"Error al abrir el archivo listadoZonaGeograficasPredio.fxml\"",e);
        	this.mainApp.mostrarAlerta(pe, AlertType.ERROR);
        	e.printStackTrace();
            return false;
        }
    }
    public boolean showEditarPotrerosPredioDialog(PredioFx predioFx) {
        try {
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("pantallas/listadoPotrerosPredio.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Potreros Predio");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(this.mainApp.getPrimaryStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            ListaPotrerosController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPredio(predioFx);
            controller.setMainApp(mainApp);
            dialogStage.showAndWait();

            return controller.isOkClicked();
            
        } catch (Exception e) {
        	PotrerosException pe =  new PotrerosException("Error", "Error de apertura de archivo", "\"Error al abrir el archivo listadoPotrerosPredio.fxml\"",e);
        	this.mainApp.mostrarAlerta(pe, AlertType.ERROR);
        	e.printStackTrace();
            return false;
        }
    }
}