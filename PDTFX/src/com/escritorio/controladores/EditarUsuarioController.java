package com.escritorio.controladores;

import java.util.List;
import java.util.stream.Collectors;

import com.entidades.Rol;
import com.enumerados.TipoDocumento;
import com.escritorio.MainApp;
import com.escritorio.clientesbean.RolsClient;
import com.escritorio.entidadesFx.RolFx;
import com.escritorio.entidadesFx.UsuarioFx;
import com.excepciones.DatosInvalidosException;
import com.excepciones.PotrerosException;
import com.excepciones.ProblemaDeConexionException;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.beans.property.BooleanProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

public class EditarUsuarioController {

    @FXML
    private TextField nombresField;
    @FXML
    private TextField apellidosField;
    @FXML
    private TextField idField;
    @FXML
    private ComboBox<TipoDocumento> tipoDocumentoField;
    @FXML
    private TextField documentoField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField direccionField;
    @FXML
    private TextField correoField;

    private Stage dialogStage;
    private UsuarioFx usuarioFx;
    private boolean okClicked = false;
    
    MainApp mainApp;
    
    @FXML
    private VBox rolesVBox = new VBox();

    //PPARA LOS ROLES: 
    @FXML
    ComboBox<RolFx> comboBox;
    
    private RolsClient rolsClient;
    private List<Rol> roles;
    
    
    public EditarUsuarioController() throws ProblemaDeConexionException {
		super();
		this.rolsClient = RolsClient.getInstancia();
    	this.roles = this.rolsClient.obtenerTodosSinMapear();
	}

	@FXML
    private void initialize() {
		
    }
	
	@FXML
	private void soloLetras(KeyEvent event) {
		
		char tecla  = event.getCharacter().charAt(0);
		
//		System.out.println("IsoControl: " + Character.isISOControl(tecla));
		
		if (!(Character.isAlphabetic(tecla)|| Character.isSpaceChar(tecla) || Character.isISOControl(tecla))) {
			//No es alfabetico o espacio
			this.mainApp.mostrarAlerta(new DatosInvalidosException("Solo se permiten caracteres alfabéticos en este campo"), AlertType.WARNING);
			event.consume();//Se borra
		}
	}
	
	@FXML
	private void soloQueParaDocumento(KeyEvent event) {
		char tecla  = event.getCharacter().charAt(0);
		if (this.tipoDocumentoField.getValue() == null) {
			this.mainApp.mostrarAlerta(new DatosInvalidosException("Debe seleccionar un tipo de documento"), AlertType.WARNING);
			event.consume();//Se borra
		}else {
			if (this.tipoDocumentoField.getValue() == TipoDocumento.CI) {
				if ( !( Character.isDigit(tecla) || Character.isISOControl(tecla) )) {
					this.mainApp.mostrarAlerta(new DatosInvalidosException("Solo se permiten dígitos para la CI"), AlertType.WARNING);
					event.consume();//Se borra
				}
			}else {
				//Si es DNI o pasarporte solo permitir numeros y letras.
				if (!(Character.isAlphabetic(tecla) || Character.isDigit(tecla) ||  Character.isISOControl(tecla))) {
					this.mainApp.mostrarAlerta(new DatosInvalidosException("Solo se permiten letras y números para " + this.tipoDocumentoField.getValue().toString()), AlertType.WARNING);
					event.consume();//Se borra
				}
			}
		}
		
		
	}
			

    public MainApp getMainApp() {
		return mainApp;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setUsuario(UsuarioFx p) {
    	p.cargarTodosLosRoles(this.roles);
        this.usuarioFx = p;
        this.nombresField.setText(p.getNombres());
        this.apellidosField.setText(p.getApellidos());
        this.tipoDocumentoField.getItems().setAll(TipoDocumento.values());
        this.tipoDocumentoField.setValue(p.getTipoDocumento());
        this.documentoField.setText(p.getDocumento());
        this.usernameField.setText(p.getUsername());
        this.passwordField.setText(p.getPassword());
        this.direccionField.setText(p.getDireccion());
        this.correoField.setText(p.getCorreo());
        this.comboBox = new ComboBox<RolFx>(this.usuarioFx.getRolesFx()) {
            protected javafx.scene.control.Skin<?> createDefaultSkin() {
                return new ComboBoxListViewSkin<RolFx>(this) {
                    @Override
                    protected boolean isHideOnClickEnabled() {
                        return false;
                    }
                };
            }
        };
        comboBox.setPrefWidth(150);
        comboBox.setItems(this.usuarioFx.getRolesFx());
        comboBox.setCellFactory(new Callback<ListView<RolFx>, ListCell<RolFx>>() {
            @Override
            public ListCell<RolFx> call(ListView<RolFx> param) {
                return new ListCell<RolFx>() {
                    private CheckBox cb = new CheckBox();
                    private BooleanProperty booleanProperty;

                    {
                        cb.setOnAction(e->getListView().getSelectionModel().select(getItem()));
                    }
                    @Override
                    protected void updateItem(RolFx item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {

                            if (booleanProperty != null) {
                                cb.selectedProperty().unbindBidirectional(booleanProperty);
                                
                            }
                            booleanProperty = item.selectedProperty();
                            cb.selectedProperty().bindBidirectional(booleanProperty);
                            setGraphic(cb);
                            setText(item.getNombre());
                        } else {
                            setGraphic(null);
                            setText(null);
                        }
                    }
                };
            }
        });

        comboBox.setButtonCell(new ListCell<RolFx>() {
            @Override
            protected void updateItem(RolFx item, boolean empty) {
                super.updateItem(item, empty);
                String selected = comboBox.getItems().stream().filter(i -> i.isSelected())
                        .map(i -> i.getNombre()).sorted()
                        .map(i -> i + "").collect(Collectors.joining(","));
                setText(selected);
                if (item != null) {
                	System.out.println(item.getNombre());
                }
            }
        });

        this.rolesVBox.getChildren().addAll(comboBox);

    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        try {
        	//Asigno los valores al UsuarioFX
        	this.usuarioFx.setNombres(nombresField.getText());
		    this.usuarioFx.setApellidos(apellidosField.getText());
		    this.usuarioFx.setTipoDocumento(tipoDocumentoField.getValue());
		    this.usuarioFx.setDocumento(documentoField.getText());
		    this.usuarioFx.setUsername(usernameField.getText());
		    this.usuarioFx.setPassword(passwordField.getText());
		    this.usuarioFx.setDireccion(direccionField.getText());
		    this.usuarioFx.setCorreo(correoField.getText());
			if (this.usuarioFx.losCamposSonValidos()) {
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