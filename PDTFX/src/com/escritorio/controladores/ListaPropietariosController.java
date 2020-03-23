package com.escritorio.controladores;

import java.io.IOException;
import com.escritorio.MainApp;
import com.escritorio.clientesbean.PropietariosClient;
import com.escritorio.entidadesFx.PropietarioFx;
import com.escritorio.entidadesFx.UsuarioFx;
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

public class ListaPropietariosController {
	
	//Lista de todos los PropietarioFx
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
    TableColumn<PropietarioFx, Boolean> activoColumn;;
    
    @FXML
    private Label nombreLabel;
    @FXML
    private Label rutLabel;
    @FXML
    private Label idLabel;
    @FXML
    private Label razonSocialLabel;
    @FXML
    private Label direccionLabel;
    @FXML
    private Label correoLabel;
    @FXML
    private Label telefonoLabel;
    @FXML
    private Label contactoLabel;

    private MainApp mainApp;
    
    private PropietariosClient propietariosClient;

    public ListaPropietariosController() throws ProblemaDeConexionException{
    	
    	this.propietariosClient = PropietariosClient.getInstancia();
		this.propietariosData = this.propietariosClient.obtenerTodos();
		this.filteredData = new FilteredList<PropietarioFx>(this.propietariosData, p -> true);

    }
    
    public ObservableList<PropietarioFx> getPropietariosData() {
        return propietariosData;
    }

    @FXML
    private void initialize() {
        nombreColumn.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        rutColumn.setCellValueFactory(cellData -> cellData.getValue().rutProperty());
        activoColumn.setCellValueFactory(cell -> {
        	PropietarioFx p = cell.getValue();
            return new ReadOnlyBooleanWrapper(p.isActivo());
        });
        activoColumn.setCellFactory(CheckBoxTableCell.forTableColumn(activoColumn));
        
        showDetallesPropietario(null);

        propietarioTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDetallesPropietario(newValue));
        
        searchTextField.setPromptText("Buscar...");
        searchTextField.textProperty().addListener((prop, old, text) -> {
            filteredData.setPredicate(propietarioFx -> {
                if(text == null || text.isEmpty()) return true;
                
//                String name = usuarioFx.getNombres().toLowerCase().concat(" ").concat(usuarioFx.getApellidos());  
                return propietarioFx.getNombre().toLowerCase().contains(text.toLowerCase()) || propietarioFx.getRut().toString().contains(text.toLowerCase()) ;
            });
        });
    }


    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        propietarioTable.setItems(this.filteredData);
    }
    

    private void showDetallesPropietario(PropietarioFx propietario) {
        if (propietario != null) {
            nombreLabel.setText(propietario.getNombre());
            rutLabel.setText(Long.toString(propietario.getRut()));
            idLabel.setText(Integer.toString(propietario.getId()));
            
            razonSocialLabel.setText(propietario.getRazonSocial());
            direccionLabel.setText(propietario.getDireccion());
            correoLabel.setText(propietario.getCorreo());
            telefonoLabel.setText(propietario.getTelefono());
            contactoLabel.setText(propietario.getContactoReferencia());
        } else {
            nombreLabel.setText("");
            rutLabel.setText("");
            idLabel.setText("");
            razonSocialLabel.setText("");
            direccionLabel.setText("");
            correoLabel.setText("");
            telefonoLabel.setText("");
            contactoLabel.setText("");
        }
    }
    
    @FXML
    private void handleNuevoPropietario() {
        PropietarioFx tempPropietarioFx = new PropietarioFx();
        boolean okClicked = this.showEditarPropietarioDialog(tempPropietarioFx);
        //El método showEditarPropietarioDialog, queda esperando a que se presione algo
        if (okClicked) {
        	try {
				tempPropietarioFx = this.propietariosClient.altaDePropietario(tempPropietarioFx);
				this.propietariosData.add(tempPropietarioFx);
				this.propietarioTable.getSelectionModel().select(tempPropietarioFx);
				this.mainApp.mostrarAlerta("Propietario creado"," Se creo el nuevo propietario", "Se dio de alta el propietario ", AlertType.INFORMATION);
			} catch (PotrerosException e) {
				this.mainApp.mostrarAlerta(e, AlertType.ERROR);
			}
        	
        }
    }
    @FXML
    private void handleEditarPropietario() {
        PropietarioFx propietarioSeleccionado = propietarioTable.getSelectionModel().getSelectedItem();
        if (propietarioSeleccionado != null) {
        	if (!propietarioSeleccionado.isActivo()) {
    			//Si el propietario no está activo
    			this.mainApp.mostrarAlerta("Editar propietario", "No se puede editar el propietario", "El propietario se encuentra desactivado", AlertType.WARNING);
    			return;
    		}
        	int indice = propietarioTable.getSelectionModel().getSelectedIndex();
            boolean okClicked = this.showEditarPropietarioDialog(propietarioSeleccionado);
            //El método showEditarPropietarioDialog, queda esperando a que se presione algo
            if (okClicked) {
            	//Si se presionó el botón Ok
            	try {
            		this.propietariosData.remove(propietarioSeleccionado);
					propietarioSeleccionado = this.propietariosClient.editarPropietario(propietarioSeleccionado);
					this.propietariosData.add(indice,propietarioSeleccionado);
					this.propietarioTable.getSelectionModel().select(propietarioSeleccionado);
					this.mainApp.mostrarAlerta("Editar Propietario", "Propietario editado", "Se editó el propietario " + propietarioSeleccionado.getNombre(), AlertType.INFORMATION);
	                showDetallesPropietario(propietarioSeleccionado);
				} catch (PotrerosException e) {
					this.mainApp.mostrarAlerta(e, AlertType.ERROR);
				}
				
            }else {
            	this.mainApp.mostrarAlerta("Editar propietario", "Propietario no editado", "Se canceló la edición del propietario " + propietarioSeleccionado.getNombre(), AlertType.INFORMATION);
            }

        } else {
        	SinSeleccionException s = new SinSeleccionException("Por favor selecciona un Propietario");
            this.mainApp.mostrarAlerta(s, AlertType.WARNING);
        }
    }
    
    @FXML
    private void handleDesactivarPropietario() {
    	PropietarioFx propietarioSeleccionado = propietarioTable.getSelectionModel().getSelectedItem();
    	int indice = propietarioTable.getSelectionModel().getSelectedIndex();
    	if (propietarioSeleccionado != null) {
    		if (!propietarioSeleccionado.isActivo()) {
    			//Si el propietario no está activo
    			this.mainApp.mostrarAlerta("Desactivar propietario", "No se puede desactivar el propietario", "El propietario ya se encuentra desactivado", AlertType.WARNING);
    			return;
    		}
            try {
            	this.propietariosData.remove(propietarioSeleccionado);
            	propietarioSeleccionado = this.propietariosClient.desactivarPropietario(propietarioSeleccionado);
				//Solo si se puede borrar del server lo borro de la tabla
				//El propietario nunca se borra, solo se deshabilita
				this.propietariosData.add(indice,propietarioSeleccionado);
				this.propietarioTable.getSelectionModel().select(propietarioSeleccionado);
				this.mainApp.mostrarAlerta("Desactivar propietario", "Propietario desactivado", "Se ha desactivado el propietario " + propietarioSeleccionado.getNombre(), AlertType.INFORMATION);
			} catch (PotrerosException e) {
				this.mainApp.mostrarAlerta(e, AlertType.ERROR);
			}
        } else {
        	SinSeleccionException s = new SinSeleccionException("Por favor selecciona un Propietario");
            this.mainApp.mostrarAlerta(s, AlertType.WARNING);
        }
    }
    
    @FXML
    private void handleActivarPropietario() {
    	PropietarioFx propietarioSeleccionado = propietarioTable.getSelectionModel().getSelectedItem();
    	int indice = propietarioTable.getSelectionModel().getSelectedIndex();
    	if (propietarioSeleccionado != null) {
    		if (propietarioSeleccionado.isActivo()) {
    			//Si el propietario no está activo
    			this.mainApp.mostrarAlerta("Activar propietario", "No se puede activar el propietario", "El propietario ya se encuentra activado", AlertType.WARNING);
    			return;
    		}
            try {
            	this.propietariosData.remove(propietarioSeleccionado);
            	propietarioSeleccionado = this.propietariosClient.activarPropietario(propietarioSeleccionado);
				this.propietariosData.add(indice,propietarioSeleccionado);
				this.propietarioTable.getSelectionModel().select(propietarioSeleccionado);
				this.mainApp.mostrarAlerta("Activar propietario", "Propietario activado", "Se ha activado el propietario " + propietarioSeleccionado.getNombre(), AlertType.INFORMATION);
			} catch (PotrerosException e) {
				this.mainApp.mostrarAlerta(e, AlertType.ERROR);
			}
        } else {
        	SinSeleccionException s = new SinSeleccionException("Por favor selecciona un Propietario");
            this.mainApp.mostrarAlerta(s, AlertType.WARNING);
        }
    }
    
    public boolean showEditarPropietarioDialog(PropietarioFx propietarioFx) {
        try {
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("pantallas/editarPropietarioDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Propietario");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(this.mainApp.getPrimaryStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            EditarPropietarioController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPropietario(propietarioFx);
            controller.setMainApp(this.mainApp);
            dialogStage.showAndWait();

            return controller.isOkClicked();
            
        } catch (IOException e) {
            return false;
        }
    }
}