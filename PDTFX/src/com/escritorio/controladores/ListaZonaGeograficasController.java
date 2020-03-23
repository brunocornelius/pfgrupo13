package com.escritorio.controladores;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.ToDoubleBiFunction;

import javax.naming.NamingException;

import com.entidades.TipoZona;
import com.enumerados.TipoDocumento;
import com.escritorio.MainApp;
import com.escritorio.clientesbean.ZonaGeograficasClient;
import com.escritorio.entidadesFx.PredioFx;
import com.escritorio.entidadesFx.ZonaGeograficaFx;
import com.escritorio.entidadesJS.ZonaGeograficasJsDriver;
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
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.ColorTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class ListaZonaGeograficasController {
	
	//Lista de todos los ZonaGeograficaFx
	private ListaZonaGeograficasController fxController;
	FilteredList<ZonaGeograficaFx> filteredData; 
	@FXML 
    private TextField searchTextField;
	
	
	private ZonaGeograficasJsDriver jsDriver;
	private ZonaGeograficasClient zonaGeograficaClient = null;
	
	private ObservableList<TipoZona> listaTipoZonas = FXCollections.observableArrayList();
	
	private ObservableList<ZonaGeograficaFx> zonaGeograficasData = FXCollections.observableArrayList();
	
    @FXML
    private TableView<ZonaGeograficaFx> zonaGeograficasTable;
    @FXML
    private TableColumn<ZonaGeograficaFx, String> nombreColumn = new TableColumn<>("nombre");
    @FXML
    private TableColumn<ZonaGeograficaFx, String> descripcionColumn = new TableColumn<>("descripcion");
    @FXML
    private TableColumn<ZonaGeograficaFx, Double> areaColumn = new TableColumn<ZonaGeograficaFx,Double>("area");
    @FXML
    private TableColumn<ZonaGeograficaFx, TipoZona> tipoZonaColumn = new TableColumn<>("tipoZona");
    @FXML
    private TableColumn<ZonaGeograficaFx, String> colorColumn = new TableColumn<>("color");
    @FXML
    private TableColumn<ZonaGeograficaFx, Button> botonBorrarColumn = new TableColumn<>();
    @FXML
    private WebView webView;
    
    private MainApp mainApp;
    
    private Stage dialogStage;
    
    private boolean okClicked = false;
    

    public ListaZonaGeograficasController() throws ProblemaDeConexionException{
    	this.jsDriver = new ZonaGeograficasJsDriver(this);
    }
    
    public WebEngine getWebViewEngine() {
    	return this.webView.getEngine();
	}

    
	public ZonaGeograficasClient getZonaGeograficaClient() {
		return zonaGeograficaClient;
	}



	public void setZonaGeograficaClient(ZonaGeograficasClient zonaGeograficaClient) {
		this.zonaGeograficaClient = zonaGeograficaClient;
	}



	public ZonaGeograficasJsDriver getJsDriver() {
		return jsDriver;
	}



	public void setJsDriver(ZonaGeograficasJsDriver jsDriver) {
		this.jsDriver = jsDriver;
	}
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setPredio(PredioFx p) throws NamingException, ProblemaDeConexionException {
        this.jsDriver.setPredioFx(p);
        this.zonaGeograficaClient = new ZonaGeograficasClient(this.jsDriver.getPredioFx().getPredio());
        this.zonaGeograficasData = this.zonaGeograficaClient.obtenerListaTodosActivas();
        this.zonaGeograficaClient.cargarListaTipoZonas(this.listaTipoZonas);
        this.zonaGeograficasTable.setItems(this.zonaGeograficasData);
        
        
    }
    
    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void initialize() {

        nombreColumn.setCellValueFactory(new PropertyValueFactory<ZonaGeograficaFx, String>("nombre"));
        nombreColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<ZonaGeograficaFx, String>("descripcion"));
        descripcionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        
        tipoZonaColumn.setCellValueFactory(new PropertyValueFactory<ZonaGeograficaFx, TipoZona>("tipoZona"));
        tipoZonaColumn.setCellFactory(ComboBoxTableCell.forTableColumn(listaTipoZonas));
        
        areaColumn.setCellValueFactory(new PropertyValueFactory<ZonaGeograficaFx, Double>("area"));
        
        botonBorrarColumn.setCellValueFactory(new PropertyValueFactory<ZonaGeograficaFx, Button>("botonBorrar"));
        
        
        nombreColumn.setOnEditCommit(data -> {
        	String strOld = data.getOldValue();
        	String strNew = data.getNewValue();
        	
            ZonaGeograficaFx zonaFx = data.getRowValue();
            
            try {
            	if (strNew == null || strNew.equals("")) {
            		throw new DatosInvalidosException("El nombre ingresado no es válida");
            	}
            	zonaFx.setNombre(strNew);
				zonaGeograficaClient.editarZonaGeografica(zonaFx);
				this.jsDriver.actualizarTooltip(zonaFx);
			} catch (PotrerosException e) {
				zonaFx.setNombre(strOld);	//Deshago la modificación
				this.mainApp.mostrarAlerta(e, AlertType.ERROR);
				data.getTableView().refresh();
			}
        });
        
        descripcionColumn.setOnEditCommit(data -> {
        	
        	String strOld = data.getOldValue();
        	String strNew = data.getNewValue();
        	
            ZonaGeograficaFx zonaFx = data.getRowValue();
            try {
	            
	            if (strNew == null || strNew.equals("")) {
	            	throw new DatosInvalidosException("La descripción ingresada no es válida");
	            }
            	
            	zonaFx.setDescripcion(strNew);
				zonaGeograficaClient.editarZonaGeografica(zonaFx);
			} catch (PotrerosException e) {
				zonaFx.setDescripcion(strOld);	//Deshago la modificación
				this.mainApp.mostrarAlerta(e, AlertType.WARNING);
				data.getTableView().refresh();
			}
        });
        
        tipoZonaColumn.setOnEditCommit(data -> {
        	TipoZona tpOld = data.getNewValue();
        	TipoZona tpNew = data.getNewValue();
        	
            ZonaGeograficaFx zonaFx = data.getRowValue();
            
            try {
            	if (tpNew == null) {
            		throw new DatosInvalidosException("El tipo de zona no puede ser null");
            	}
            	zonaFx.setTipoZona(tpNew);
				zonaGeograficaClient.editarZonaGeografica(zonaFx);
				this.jsDriver.actualizarColor(zonaFx);
			} catch (PotrerosException e) {
				zonaFx.setTipoZona(tpOld);		//Deshago la modificación.
				this.mainApp.mostrarAlerta(e, AlertType.WARNING);
				data.getTableView().refresh();
			}
        });
        
        zonaGeograficasTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> hacerAlgoAlSeleccionarZona(newValue));
        
        zonaGeograficasTable.setEditable(true);
        
        WebEngine engine = this.webView.getEngine();
    	
    	URL miHtml = getClass().getResource("../html/zonasPredio.html");
    	engine.load(miHtml.toExternalForm());
    	this.webView.autosize();
    	System.out.println("Se autosizio el webview");
    	engine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            public void changed(ObservableValue ov, State oldState, State newState) {
            	System.out.println("predio.html: " + newState.toString());
                if (newState == State.SUCCEEDED) {
                	JSObject window = (JSObject) engine.executeScript("window");
//                	window.setMember("predioFx", jsDriver.getPredioFx());
                	window.setMember("jsDriver", jsDriver);
                	System.out.println("Ya se cargo jsDriver a javascript");
                    engine.executeScript("crearMapaPrueba()");
                    System.out.println("Se creo el mapa");
                    jsDriver.agregarZonasExistentesPredio();
                    zonaGeograficasTable.refresh();
                }
                
            }
        });
    }

    @FXML
    private void handleResto() {
//    	System.out.println("Se presiono resto");
//    	this.jsDriver.addZonaGeograficaRestante();
    	try {
    		if (this.predioCompleto()) {
        		//Si no coincide el área de la suma de zonasgeograficas con el área del predio...
        		this.mainApp.mostrarAlerta("Zona Geografica", "Edicion de Zonas geográficas", "Las Zonas geográficas ya cubren la totalidad del predio", AlertType.ERROR);
        		return;
        	}
			this.jsDriver.addZonaGeograficaRestante();
			this.mainApp.mostrarAlerta("Zonas geograficas", "Alta de Zona geografica restante", "Se dieron de alta Zonas geograficas con el área que había libre", AlertType.INFORMATION);
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

	public void hacerAlgoAlSeleccionarZona(ZonaGeograficaFx zona) {
    	System.out.println("Seleccionaste una zona");
    }
	
	private boolean predioCompleto() throws DatosInvalidosException {
		//Aca tenemos que comprobar que el área del predio coincida con el area de las zonas.
    	double area = 0;
    	for (ZonaGeograficaFx zonaFx : this.zonaGeograficasData) {
			area = area + zonaFx.getZonaGeografica().getAreaEnHectareas();
		}
//    	System.out.println("SRID: " + this.jsDriver.getPredioFx().getPredio().getForma().getSRID());
    	
    	Long areaPredio = Math.round(this.jsDriver.getPredioFx().getPredio().getAreaEnHectareas());
    	Long areaZonas = Math.round(area);
    	System.out.println("Area predio: " + areaPredio);
    	System.out.println("Area zonas : " + areaZonas);
    	if (areaZonas > areaPredio) {
    		throw new DatosInvalidosException("El área de las zonas supera el área del predio");
    	}
    	
    	if (areaZonas < areaPredio) {
    		return false;
    	}
    	
		return true;
	}
	
	
    
    @FXML
    public void handleOk() {
    	System.out.println("Se presionó OK");
    	//TODO pedir al bean con estado que guarde todo de una
    	
    	okClicked = false;
    	boolean ejecutoBean = false;
    	try {
    		if (!this.predioCompleto()) {
        		//Si no coincide el área de la suma de zonasgeograficas con el área del predio...
        		this.mainApp.mostrarAlerta("Zona Geografica", "Edicion de Zonas geográficas", "Las Zonas geográficas no cubren la totalidad del predio", AlertType.ERROR);
        		return;
        	}
    		HashSet<String> listaNombres = new HashSet<String>();
    		for (ZonaGeograficaFx zona : this.zonaGeograficasData) {
    			//Chequea los campos de cada zona antes de llamar
				zona.losCamposSonValidos();
				if (listaNombres.contains(zona.getNombre())) {
					throw new DatosInvalidosException("El nombre " + zona.getNombre() + " se encuentra repetido");
				}
				listaNombres.add(zona.getNombre());
			}
    		//si no saltó excepcion en el for
    		ejecutoBean = true;
			this.zonaGeograficaClient.guardarTodo();
			okClicked = true;
		    dialogStage.close();
		} catch (DatosInvalidosException e) {
			okClicked = false;
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

	public TableView<ZonaGeograficaFx> getZonaGeograficasTable() {
		return zonaGeograficasTable;
	}


}