package com.escritorio.controladores;

import com.entidades.Indicador;
import com.escritorio.MainApp;
import com.escritorio.clientesbean.IndicadorsClient;
import com.excepciones.DatosInvalidosException;
import com.excepciones.NoExisteElementoException;
import com.excepciones.ProblemaDeConexionException;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class ListaIndicadoresArbolController {
	
	@FXML
    private TreeView<Indicador> indicadoresTreeView;

    private MainApp mainApp;
    
    private IndicadorsClient indicadorsClient;

    public ListaIndicadoresArbolController() throws ProblemaDeConexionException{
    	this.indicadorsClient = IndicadorsClient.getInstancia();
    	
    	
    }
    
    public void inicializar() {
    	
    }
    
   

    @FXML
    private void initialize() {
       
    }


    public void setMainApp(MainApp mainApp) throws ProblemaDeConexionException, NoExisteElementoException, DatosInvalidosException {
        this.mainApp = mainApp;
        TreeItem<Indicador> indicadorRaiz = this.indicadorsClient.obtenerIndicadorRaiz();
        indicadoresTreeView.setRoot(indicadorRaiz);
        
    }
    
    
}