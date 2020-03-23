package com.escritorio.controladores;

import com.escritorio.MainApp;
import com.escritorio.clientesbean.IndicadorsClient;
import com.escritorio.entidadesFx.IndicadorFx;
import com.excepciones.ProblemaDeConexionException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ListaIndicadoresController {
	
	//Lista de todos los IndicadorFx
	private ObservableList<IndicadorFx> indicadorsData;
	FilteredList<IndicadorFx> filteredData; 
	@FXML 
    private TextField searchTextField;
	
    @FXML
    private TableView<IndicadorFx> indicadorTable;
    @FXML
    private TableColumn<IndicadorFx, String> nombreColumn;

    @FXML
    private Label nombreLabel;
    @FXML
    private Label descripcionLabel;
    
    @FXML
    private Label idLabel;

    private MainApp mainApp;
    
    private IndicadorsClient indicadorsClient;

    public ListaIndicadoresController() throws ProblemaDeConexionException{
    	this.indicadorsClient = IndicadorsClient.getInstancia();
    	this.indicadorsData = this.indicadorsClient.obtenerTodos();
    	
    }
    
    public void inicializar() {
    	
    }
    
    public ObservableList<IndicadorFx> getIndicadorsData() {
        return indicadorsData;
    }

    @FXML
    private void initialize() {
        nombreColumn.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        
        showDetallesIndicador(null);

        indicadorTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDetallesIndicador(newValue));
    }


    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.indicadorTable.setItems(this.indicadorsData);
    }
    

    private void showDetallesIndicador(IndicadorFx person) {
        if (person != null) {
            nombreLabel.setText(person.getNombre());
            descripcionLabel.setText(person.getDescripcion());
            idLabel.setText(Integer.toString(person.getId()));
        } else {
            nombreLabel.setText("");
            idLabel.setText("");
        }
    }
    
}