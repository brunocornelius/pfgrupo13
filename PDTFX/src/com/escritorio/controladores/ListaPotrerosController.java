package com.escritorio.controladores;

import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import javax.naming.NamingException;
import com.entidades.Indicador;
import com.entidades.ZonaPotrero;
import com.escritorio.MainApp;
import com.escritorio.clientesbean.PotrerosClient;
import com.escritorio.clientesbean.ZonaGeograficasClient;
import com.escritorio.clientesbean.ZonaPotrerosClient;
import com.escritorio.entidadesFx.PredioFx;
import com.escritorio.entidadesFx.ZonaGeograficaFx;
import com.escritorio.entidadesFx.IndicadorPotreroFx;
import com.escritorio.entidadesFx.PotreroFx;
import com.escritorio.entidadesJS.PotrerosJsDriver;
import com.excepciones.DatosInvalidosException;
import com.excepciones.PotrerosException;
import com.excepciones.ProblemaDeConexionException;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class ListaPotrerosController {
	
	//Lista de todos los PotreroFx
	@FXML 
    private TextField searchTextField;
	
	
	private PotrerosJsDriver jsDriver;
	private PotrerosClient potreroClient = null;
	private List<ZonaPotrero> zonaPotrerosActivas = null;
	private ZonaGeograficasClient zonaGeograficaClient = null;
	
	private ObservableList<Indicador> listaIndicadores = FXCollections.observableArrayList();	//Esta tiene q estar inicializada porque se pasa como parametro en el metodo cargar
	
	private ObservableList<PotreroFx> potrerosData;
	private ObservableList<ZonaGeograficaFx> zonaGeograficasData;
	
    @FXML
    private TableView<PotreroFx> potrerosTable;
    
    @FXML
    private TableView<IndicadorPotreroFx> historialTable;
    
    @FXML
    private TableColumn<PotreroFx, String> nombreColumn = new TableColumn<>("nombre");
    @FXML
    private TableColumn<PotreroFx, String> descripcionColumn = new TableColumn<>("descripcion");
    @FXML
    private TableColumn<PotreroFx, Double> areaColumn = new TableColumn<PotreroFx,Double>("area");
    @FXML
    private TableColumn<PotreroFx, Indicador> indicadorColumn = new TableColumn<>("indicador");
    @FXML
    private TableColumn<PotreroFx, Button> botonBorrarColumn = new TableColumn<>();
    @FXML
    private WebView webView;
    
    //COLUMNAS para la tabla historial
    @FXML
    private TableColumn<IndicadorPotreroFx, String> desdeColumn = new TableColumn<>("desde");
    @FXML
    private TableColumn<IndicadorPotreroFx, String> hastaColumn = new TableColumn<>("hasta");
    @FXML
    private TableColumn<IndicadorPotreroFx, String> indicadorHColumn = new TableColumn<>("indicador");
    @FXML
    private TableColumn<IndicadorPotreroFx, Boolean> activoColumn = new TableColumn<>("activo");

    
    private MainApp mainApp;
    
    private Stage dialogStage;
    
    private boolean okClicked = false;
    

    public ListaPotrerosController() throws ProblemaDeConexionException{
    	this.jsDriver = new PotrerosJsDriver(this);
    }
    
    public WebEngine getWebViewEngine() {
    	return this.webView.getEngine();
	}

    
	public PotrerosClient getPotreroClient() {
		return potreroClient;
	}



	public void setPotreroClient(PotrerosClient potreroClient) {
		this.potreroClient = potreroClient;
	}



	public PotrerosJsDriver getJsDriver() {
		return jsDriver;
	}

	

	public List<ZonaPotrero> getZonaPotrerosActivas() {
		return zonaPotrerosActivas;
	}

	public void setZonaPotrerosActivas(List<ZonaPotrero> zonaPotrerosActivas) {
		this.zonaPotrerosActivas = zonaPotrerosActivas;
	}

	public void setJsDriver(PotrerosJsDriver jsDriver) {
		this.jsDriver = jsDriver;
	}
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setPredio(PredioFx p) throws NamingException, ProblemaDeConexionException {
        this.jsDriver.setPredioFx(p);
        this.potreroClient = new PotrerosClient(this.jsDriver.getPredioFx().getPredio());
        this.zonaGeograficaClient = new ZonaGeograficasClient(this.jsDriver.getPredioFx().getPredio());
        this.potrerosData = this.potreroClient.obtenerTodos();
        this.zonaGeograficasData = this.zonaGeograficaClient.obtenerListaTodosActivas();
        this.potreroClient.cargarListaIndicadores(this.listaIndicadores);
        this.potrerosTable.setItems(this.potrerosData);
        
        this.zonaPotrerosActivas = ZonaPotrerosClient.getInstancia().obtenerZonaPotrerosPredio(p.getPredio());
        
    }
    
    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void initialize() {

        nombreColumn.setCellValueFactory(new PropertyValueFactory<PotreroFx, String>("nombre"));
        nombreColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<PotreroFx, String>("descripcion"));
        descripcionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        
        indicadorColumn.setCellValueFactory(new PropertyValueFactory<PotreroFx, Indicador>("indicador"));
        indicadorColumn.setCellFactory(ComboBoxTableCell.forTableColumn(listaIndicadores));
        
        areaColumn.setCellValueFactory(new PropertyValueFactory<PotreroFx, Double>("area"));
        
        botonBorrarColumn.setCellValueFactory(new PropertyValueFactory<PotreroFx, Button>("botonBorrar"));
        
        
        nombreColumn.setOnEditCommit(data -> {
            
            PotreroFx zonaFx = data.getRowValue();
            zonaFx.setNombre(data.getNewValue());
            try {
				potreroClient.editarPotrero(zonaFx);
				this.jsDriver.actualizarTooltip(zonaFx);
			} catch (PotrerosException e) {
				zonaFx.setNombre(data.getOldValue());	//Deshago la modificación
				this.mainApp.mostrarAlerta(e, AlertType.WARNING);
			}
        });
        
        descripcionColumn.setOnEditCommit(data -> {
            System.out.println("Nuevo descripcion: " +  data.getNewValue());
            System.out.println("Antiguo descripcion: " + data.getOldValue());

            PotreroFx zonaFx = data.getRowValue();
            zonaFx.setDescripcion(data.getNewValue());
            try {
				potreroClient.editarPotrero(zonaFx);
			} catch (PotrerosException e) {
				zonaFx.setDescripcion(data.getOldValue());	//Deshago la modificación
				this.mainApp.mostrarAlerta(e, AlertType.WARNING);
			}
        });
        
        indicadorColumn.setOnEditCommit(data -> {
            PotreroFx potreroFx = data.getRowValue();
            potreroFx.setIndicador(data.getNewValue());
            try {
				potreroClient.editarPotrero(potreroFx);
				this.jsDriver.actualizarColor(potreroFx);
			} catch (PotrerosException e) {
				potreroFx.setIndicador(data.getOldValue());		//Deshago la modificación.
				this.mainApp.mostrarAlerta(e, AlertType.WARNING);
			}
        });
        
        desdeColumn.setCellValueFactory(cellData -> cellData.getValue().desdeProperty());
        hastaColumn.setCellValueFactory(cellData -> cellData.getValue().hastaProperty());
        indicadorHColumn.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        activoColumn.setCellValueFactory(cell -> {
        	IndicadorPotreroFx p = cell.getValue();
            return new ReadOnlyBooleanWrapper(p.isActivo());
        });
        activoColumn.setCellFactory(CheckBoxTableCell.forTableColumn(activoColumn));
        
        
        potrerosTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> hacerAlgoAlSeleccionarPotrero(newValue));
        
        
        
        potrerosTable.setEditable(true);
        
        WebEngine engine = this.webView.getEngine();
    	
    	URL miHtml = getClass().getResource("../html/potrerosPredio.html");
    	engine.load(miHtml.toExternalForm());
    	this.webView.autosize();
    	System.out.println("Se autosizio el webview");
    	engine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            public void changed(ObservableValue ov, State oldState, State newState) {
            	
                if (newState == State.SUCCEEDED) {
                	JSObject window = (JSObject) engine.executeScript("window");
//                	window.setMember("predioFx", jsDriver.getPredioFx());
                	window.setMember("jsDriver", jsDriver);
                	System.out.println("Ya se cargo jsDriver a javascript");
                    engine.executeScript("crearMapaPrueba()");
                    System.out.println("Se creo el mapa");
                    jsDriver.agregarPotrerosExistentesPredio();
                    potrerosTable.refresh();
                }
                
            }
        });
    }

    @FXML
    private void handleResto() {
    	System.out.println("Se presiono resto");
    	try {
			this.jsDriver.addPotreroRestante();
			this.mainApp.mostrarAlerta("Potreros", "Alta de potrero restante", "Se dieron de alta potreros con el área que había libre", AlertType.INFORMATION);
		} catch (DatosInvalidosException e) {
			this.mainApp.mostrarAlerta(e, AlertType.ERROR);
		}
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    
    public MainApp getMainApp() {
		return mainApp;
	}

	public void hacerAlgoAlSeleccionarPotrero(PotreroFx potreroFx) {
    	System.out.println("Seleccionaste un potrero");
    	if (potreroFx != null) {
    		this.jsDriver.mostrarTooltip(potreroFx);
    		//CARGAR TABLA HISTORICO INDICADORPOTREROS de. POTRERO
        	this.historialTable.setItems(potreroFx.getIndicadorPotrerosFx());
    	}
    	
    	
    	
    }
	
	private boolean predioCompleto() throws DatosInvalidosException {
		//Aca tenemos que comprobar que el área del predio coincida con el area de las zonas.
    	double area = 0;
    	for (PotreroFx potrero : this.potrerosData) {
			area = area + potrero.getPotrero().getAreaEnHectareas();
		}
    	
    	Long areaPredio = Math.round(this.jsDriver.getPredioFx().getPredio().getAreaEnHectareas());
    	Long areaPotrero = Math.round(area);
    	System.out.println("Area predio: " + areaPredio);
    	System.out.println("Area potreros : " + areaPotrero);
    	if (areaPotrero > areaPredio) {
    		throw new DatosInvalidosException("El área de los potreros supera el área del predio");
    	}
    	
    	if (areaPotrero < areaPredio) {
    		return false;
    	}
    	
		return true;
	}
    
    @FXML
    public void handleOk() {
    	okClicked = false;
    	boolean ejecutoBean = false;
    	
    	try {
    		if (!this.predioCompleto()) {
        		//Si no coincide el área de la suma de zonasgeograficas con el área del predio...
        		this.mainApp.mostrarAlerta("Potreros", "Edicion de Potreros", "Los potreros no cubren la totalidad del predio", AlertType.ERROR);
        		return;
        	}
    		
    		HashSet<String> listaNombres = new HashSet<String>();
    		for (PotreroFx potrero : this.potrerosData) {
    			//Chequea los campos de cada zona antes de llamar
				potrero.losCamposSonValidos();
				if (listaNombres.contains(potrero.getNombre())) {
					throw new DatosInvalidosException("El nombre " + potrero.getNombre() + " se encuentra repetido");
				}
				listaNombres.add(potrero.getNombre());
			}
    		//si no saltó excepcion en el for
    		ejecutoBean = true;
			this.potreroClient.guardarTodo();
			okClicked = true;
		    dialogStage.close();
		} catch (DatosInvalidosException e) {
			this.mainApp.mostrarAlerta(e, AlertType.WARNING);
			e.printStackTrace();
			if (ejecutoBean) {
				//Si la excepcion saltó en la ejecución del bean, se cierra la ventana
				dialogStage.close();
			}
		}
    }
    
    @FXML
    public void handleCancel() {
    	dialogStage.close();
    }

    public ObservableList<ZonaGeograficaFx> getZonaGeograficasData() {
		return zonaGeograficasData;
	}

	public ObservableList<PotreroFx> getPotrerosData() {
		return potrerosData;
	}

	public TableView<PotreroFx> getPotrerosTable() {
		return potrerosTable;
	}


}