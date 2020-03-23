package com.escritorio.controladores;

import java.net.URL;
import com.escritorio.MainApp;
import com.escritorio.clientesbean.PrediosClient;
import com.escritorio.clientesbean.PropietariosClient;
import com.escritorio.entidadesFx.PredioFx;
import com.escritorio.entidadesFx.PropietarioFx;
import com.excepciones.DatosInvalidosException;
import com.excepciones.PotrerosException;
import com.excepciones.ProblemaDeConexionException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class EditarPredioController {

    @FXML
    private TextField nombreField;
    @FXML
    private TextField descripcionField;
    @FXML
    private TextField idField;
    @FXML
    private WebView webView;
    
    private Stage dialogStage;
    private PredioFx predioFx;
    private boolean okClicked = false;
    private MainApp mainApp;
    
  //Lista de todos los PropietarioFx
    private PropietariosClient propietariosClient;
    
  	private ObservableList<PropietarioFx> propietariosData = FXCollections.observableArrayList();
  	FilteredList<PropietarioFx> filteredData; 
  	@FXML 
    private TextField searchTextField;
  	@FXML
    private TableView<PropietarioFx> propietarioTable;
    @FXML
    private TableColumn<PropietarioFx, String> nombreColumn;
    @FXML
    private TableColumn<PropietarioFx, Number> rutColumn;
    @FXML
    private TableColumn<PropietarioFx, Boolean> selectedColumn;
    
    public MainApp getMainApp() {
		return mainApp;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		propietarioTable.setItems(this.filteredData);
	}

	public EditarPredioController() throws ProblemaDeConexionException {
		super();
		this.propietariosClient = PropietariosClient.getInstancia();
		
	}

	@FXML
    private void initialize() {
    	WebEngine engine = this.webView.getEngine();
    	
    	URL miHtml = getClass().getResource("../html/predio.html");
    	engine.load(miHtml.toExternalForm());
    	
    	engine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            public void changed(ObservableValue ov, State oldState, State newState) {
            	System.out.println("predio.html: " + newState.toString());
                if (newState == State.SUCCEEDED) {
                	JSObject window = (JSObject) engine.executeScript("window");
                	window.setMember("predioFx", predioFx);
                	System.out.println("Ya se cargo predioFx a javascript");
                    engine.executeScript("crearMapaPrueba()");
                    System.out.println("Se creo el mapa");
                }
                
            }
        });
    	
    	nombreColumn.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        rutColumn.setCellValueFactory(cellData -> cellData.getValue().rutProperty());
        
        
        selectedColumn.setCellValueFactory(new PropertyValueFactory<PropietarioFx, Boolean>("selected"));
//        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));
        
        selectedColumn.setCellFactory(p -> {
            CheckBox checkBox = new CheckBox();
            TableCell<PropietarioFx, Boolean> cell = new TableCell<PropietarioFx, Boolean>() {
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    if (empty) {
                        setGraphic(null);
                    } else {
                        checkBox.setSelected(item);
                        setGraphic(checkBox);
                    }
                }
            };
            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> 
                ((PropietarioFx)cell.getTableRow().getItem()).setSelected(isSelected));
            cell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            cell.setAlignment(Pos.CENTER);
            return cell ;
        });
        
        
        propietarioTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> actualizarSelected(newValue));
        
    	searchTextField.setPromptText("Buscar...");
        searchTextField.textProperty().addListener((prop, old, text) -> {
            filteredData.setPredicate(propietarioFx -> {
                if(text == null || text.isEmpty()) return true;
                
//                String name = usuarioFx.getNombres().toLowerCase().concat(" ").concat(usuarioFx.getApellidos());  
                return propietarioFx.getNombre().toLowerCase().contains(text.toLowerCase()) || propietarioFx.getRut().toString().contains(text.toLowerCase()) ;
            });
        });
    	
    }
	
	public void actualizarSelected(PropietarioFx propietario) {
		System.out.println("Propietario seleccionado " + propietario.getNombre());
//		propietario.setSelected(!propietario.isSelected());
//		this.propietarioTable.getSelectionModel().clearSelection();
	}

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setPredio(PredioFx predioFx) throws ProblemaDeConexionException, DatosInvalidosException {
        this.predioFx = predioFx;
        this.predioFx.cargarTodosLosPropietarios(this.propietariosClient.obtenerTodos());
        this.propietariosData = this.predioFx.getPropietariosFx();
		this.filteredData = new FilteredList<PropietarioFx>(this.propietariosData, p -> true);
		if (this.propietariosData.size() == 0) {
			//Si no existen propietarios...
			throw new DatosInvalidosException("No existen propietarios para asignar al predio");
		}
        nombreField.setText(predioFx.getNombre());
        descripcionField.setText(predioFx.getDescripcion());
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
    	System.out.println("HANDLE OK");
    		this.predioFx.setNombre(nombreField.getText());
    		this.predioFx.setDescripcion(descripcionField.getText());
			this.predioFx.sincronizarPropietarios();
			
			if (this.predioFx.getPredio().getPropietarios().size() == 0) {
				this.mainApp.mostrarAlerta("Predio","Debe seleccionar un propietario","El predio debe tener al menos un propietario asignado",AlertType.ERROR);
			}else {
				try {
				    this.predioFx.losCamposSonValidos();
				    
				    PrediosClient.getInstancia().existeNombre(predioFx.getNombre());	//Si existe tira excepcion
				    
				    
				    okClicked = true;
				    dialogStage.close();
				} catch (PotrerosException e) {
					this.mainApp.mostrarAlerta(e, AlertType.ERROR);
					okClicked = false;
//					e.printStackTrace();
				}
			}
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}