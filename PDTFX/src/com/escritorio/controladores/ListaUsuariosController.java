package com.escritorio.controladores;

import java.io.IOException;
import com.escritorio.MainApp;
import com.escritorio.clientesbean.UsuariosClient;
import com.escritorio.entidadesFx.UsuarioFx;
import com.excepciones.PotrerosException;
import com.excepciones.ProblemaDeConexionException;
import com.excepciones.SinSeleccionException;
import javafx.beans.property.ReadOnlyBooleanWrapper;
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
import javafx.scene.control.cell.CheckBoxTableCell;

public class ListaUsuariosController {
	
	//Lista de todos los UsuarioFx
	private ObservableList<UsuarioFx> usuariosData;
	FilteredList<UsuarioFx> filteredData; 
	
    @FXML
    private TableView<UsuarioFx> usuarioTable;
    @FXML
    private TableColumn<UsuarioFx, String> nombresColumn;
    @FXML
    private TableColumn<UsuarioFx, String> apellidosColumn;
    @FXML
    private TableColumn<UsuarioFx, String> documentoColumn;
    @FXML
    TableColumn<UsuarioFx, Boolean> activoColumn;;
    
    @FXML 
    private TextField searchTextField;
    
    @FXML
    private Label nombresLabel;
    @FXML
    private Label apellidosLabel;
    @FXML
    private Label idLabel;
    @FXML
    private Label tipoDocumentoLabel;
    @FXML
    private Label documentoLabel;
    @FXML
    private Label direccionLabel;
    @FXML
    private Label correoLabel;
    @FXML
    private Label usuarioLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label rolesLabel;

    private MainApp mainApp;
    
    private UsuariosClient usuariosClient;
   

    public ListaUsuariosController() throws ProblemaDeConexionException{
    	
    	this.usuariosClient = UsuariosClient.getInstancia();
		this.usuariosData = this.usuariosClient.obtenerTodos();
		this.filteredData = new FilteredList<UsuarioFx>(this.usuariosData, p -> true);

    }
    
    public ObservableList<UsuarioFx> getUsuariosData() {
        return usuariosData;
    }

    @FXML
    private void initialize() {
        nombresColumn.setCellValueFactory(cellData -> cellData.getValue().nombresProperty());
        apellidosColumn.setCellValueFactory(cellData -> cellData.getValue().apellidosProperty());
        documentoColumn.setCellValueFactory(cellData -> cellData.getValue().documentoProperty());
        activoColumn.setCellValueFactory(cell -> {
        	UsuarioFx p = cell.getValue();
            return new ReadOnlyBooleanWrapper(p.isActivo());
        });
        
        showDetallesUsuario(null);
        activoColumn.setCellFactory(CheckBoxTableCell.forTableColumn(activoColumn));

        usuarioTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDetallesUsuario(newValue));
        
        searchTextField.setPromptText("Buscar...");
        searchTextField.textProperty().addListener((prop, old, text) -> {
            filteredData.setPredicate(usuarioFx -> {
                if(text == null || text.isEmpty()) return true;
                
//                String name = usuarioFx.getNombres().toLowerCase().concat(" ").concat(usuarioFx.getApellidos());  
                return usuarioFx.getNombres().toLowerCase().contains(text.toLowerCase()) || usuarioFx.getApellidos().toLowerCase().contains(text.toLowerCase()) || usuarioFx.getDocumento().toLowerCase().contains(text.toLowerCase());
            });
        });
    }


    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        usuarioTable.setItems(this.filteredData);
    }
    

    private void showDetallesUsuario(UsuarioFx usuario) {
        if (usuario != null) {
            nombresLabel.setText(usuario.getNombres());
            apellidosLabel.setText(usuario.getApellidos());
            idLabel.setText(Integer.toString(usuario.getId()));
            
            tipoDocumentoLabel.setText(usuario.getTipoDocumento().toString());
            documentoLabel.setText(usuario.getDocumento());
            direccionLabel.setText(usuario.getDireccion());
            correoLabel.setText(usuario.getCorreo());
            usuarioLabel.setText(usuario.getUsername());
            passwordLabel.setText(usuario.getPassword());
            rolesLabel.setText(usuario.getStringRoles());
        } else {
            nombresLabel.setText("");
            apellidosLabel.setText("");
            idLabel.setText("");
            tipoDocumentoLabel.setText("");
            documentoLabel.setText("");
            direccionLabel.setText("");
            correoLabel.setText("");
            usuarioLabel.setText("");
            passwordLabel.setText("");
            rolesLabel.setText("");
        }
    }
    
    @FXML
    private void handleNuevoUsuario() {
        UsuarioFx tempUsuarioFx = new UsuarioFx();
        boolean okClicked = this.showEditarUsuarioDialog(tempUsuarioFx);
        //El método showEditarUsuarioDialog, queda esperando a que se presione algo
        if (okClicked) {
        	try {
				tempUsuarioFx = this.usuariosClient.altaDeUsuario(tempUsuarioFx);
				
				this.usuariosData.add(tempUsuarioFx);
				usuarioTable.getSelectionModel().select(tempUsuarioFx);
				this.mainApp.mostrarAlerta("Alta exitosa", "Alta OK", "Se dio de alta el usuario " + tempUsuarioFx.getUsername(), AlertType.INFORMATION);
			} catch (PotrerosException e) {
				this.mainApp.mostrarAlerta(e, AlertType.WARNING);
				e.printStackTrace();
			}
        }else {
        	this.mainApp.mostrarAlerta("Alta cancelada", "Alta cancelada", "Se canceló el alta de usuario", AlertType.INFORMATION);
        }
    }
    @FXML
    private void handleEditarUsuario() {
        UsuarioFx usuarioSeleccionado = usuarioTable.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado != null) {
        	int indice = usuarioTable.getSelectionModel().getSelectedIndex();
            if (!usuarioSeleccionado.getUsuario().getActivo()) {
        		//Si el usuario está activo
        		this.mainApp.mostrarAlerta("Usuario No activo", "No se puede editar el usuario " + usuarioSeleccionado.getUsername(),"No se pueden editar usuarios deshabilitados",AlertType.ERROR);
        	}else {
        		boolean okClicked = this.showEditarUsuarioDialog(usuarioSeleccionado);
                //El método showEditarUsuarioDialog, queda esperando a que se presione algo
                if (okClicked) {
                	//Si se presionó el botón Ok
                	try {
                		this.usuariosData.remove(usuarioSeleccionado);
    					usuarioSeleccionado = this.usuariosClient.editarUsuario(usuarioSeleccionado);
    					this.usuariosData.add(indice,usuarioSeleccionado);
    					usuarioTable.getSelectionModel().select(indice);
    					this.mainApp.mostrarAlerta("Edicion exitosa", "Edicion OK", "Se editó el usuario " + usuarioSeleccionado.getUsername(), AlertType.INFORMATION);
    	                showDetallesUsuario(usuarioSeleccionado);
    				} catch (PotrerosException e) {
    					this.mainApp.mostrarAlerta(e, AlertType.WARNING);
    				}
    				
                }else {
                	this.mainApp.mostrarAlerta("Edicion cancelada", "Edicion cancelada", "Se canceló la edición del usuario " + usuarioSeleccionado.getUsername(), AlertType.INFORMATION);
                }
        	}
        } else {
        	SinSeleccionException s = new SinSeleccionException("Por favor selecciona un Usuario");
            this.mainApp.mostrarAlerta(s, AlertType.WARNING);
        }
    }
    
    @FXML
    private void handleBorrarUsuario() {
    	UsuarioFx usuarioSeleccionado = usuarioTable.getSelectionModel().getSelectedItem();
    	
    	if (usuarioSeleccionado != null) {
    		
    		int indice = usuarioTable.getSelectionModel().getSelectedIndex();
        	if (!usuarioSeleccionado.getUsuario().getActivo()) {
        		//Si el usuario NO está activo
        		this.mainApp.mostrarAlerta("Usuario ya inactivo", "No se puede deshabilitar el usuario","El usuario ya se encuentra deshabilitado",AlertType.ERROR);
        	}else {
        		try {
            		this.usuariosData.remove(usuarioSeleccionado);
    				usuarioSeleccionado.setActivo(false);
            		usuarioSeleccionado = this.usuariosClient.editarUsuario(usuarioSeleccionado);
    				
    				this.usuariosData.add(indice,usuarioSeleccionado);
    				usuarioTable.getSelectionModel().select(indice);
    				this.mainApp.mostrarAlerta("Edicion exitosa", "Edicion OK", "Se deshabilitó el usuario " + usuarioSeleccionado.getUsername(), AlertType.INFORMATION);
    				
                    showDetallesUsuario(usuarioSeleccionado);
    			} catch (PotrerosException e) {
    				this.mainApp.mostrarAlerta(e, AlertType.WARNING);
    			}
        	}
        } else {
        	SinSeleccionException s = new SinSeleccionException("Por favor selecciona un Usuario");
            this.mainApp.mostrarAlerta(s, AlertType.WARNING);
        }
    }
    
    @FXML
    private void handleActivarUsuario() {
    	UsuarioFx usuarioSeleccionado = usuarioTable.getSelectionModel().getSelectedItem();
    	
    	if (usuarioSeleccionado != null) {
    		int indice = usuarioTable.getSelectionModel().getSelectedIndex();
    		if (usuarioSeleccionado.getUsuario().getActivo()) {
        		//Si el usuario está activo
        		this.mainApp.mostrarAlerta("Usuario ya activo", "No se puede activar el usuario","El usuario ya se encuentra activo",AlertType.ERROR);
        	}else {
        		try {
            		this.usuariosData.remove(usuarioSeleccionado);
    				usuarioSeleccionado.setActivo(true);
            		usuarioSeleccionado = this.usuariosClient.editarUsuario(usuarioSeleccionado);
    				
    				this.usuariosData.add(indice,usuarioSeleccionado);
    				usuarioTable.getSelectionModel().select(indice);
    				this.mainApp.mostrarAlerta("Edicion exitosa", "Edicion OK", "Se habilitó el usuario " + usuarioSeleccionado.getUsername(), AlertType.INFORMATION);
                    showDetallesUsuario(usuarioSeleccionado);
    			} catch (PotrerosException e) {
    				this.mainApp.mostrarAlerta(e, AlertType.WARNING);
    			}
        	}
        } else {
        	SinSeleccionException s = new SinSeleccionException("Por favor selecciona un Usuario");
            this.mainApp.mostrarAlerta(s, AlertType.WARNING);
        }
    }

    
    public boolean showEditarUsuarioDialog(UsuarioFx usuarioFx) {
        try {
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("pantallas/editarUsuarioDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Usuario");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(this.mainApp.getPrimaryStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            EditarUsuarioController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setUsuario(usuarioFx);
            controller.setMainApp(this.mainApp);
            dialogStage.showAndWait();

            return controller.isOkClicked();
            
        } catch (IOException e) {
            return false;
        }
    }
}