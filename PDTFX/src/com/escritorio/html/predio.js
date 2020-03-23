	var osmUrl = 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
	var osmAttrib = '&copy; <a href="http://openstreetmap.org/copyright">OpenStreetMap</a> contributors';
	var osm = L.tileLayer(osmUrl, { maxZoom: 18, attribution: osmAttrib });
	var map = new L.Map('mapid', { center: new L.LatLng(-32.241, -55.838), zoom: 8 });
	var drawnItems = L.featureGroup().addTo(map);

function crearMapaPrueba(){

	
	L.control.layers({
		"google": L.tileLayer('http://www.google.cn/maps/vt?lyrs=s@189&gl=cn&x={x}&y={y}&z={z}', {
		    attribution: 'google'
		}).addTo(map),
		'osm': osm,
	}, { 'predio': drawnItems }, { position: 'topleft', collapsed: false }).addTo(map);
	
	if (window.predioFx.isMapaEditable()) {
		map.addControl(new L.Control.Draw({
			position: 'topright',
			edit: {
			    featureGroup: drawnItems,
			    poly: {
			        allowIntersection: false
			    },
			},
			draw: {
			    polygon: true,
			    circle: false,
			    circlemarker: false,
			    polyline: false,
			    rectangle: false,
			    marker: false,
			}
		}));
	}
	
	
	if (window.predioFx.getId() != 0){
		//CARGAR ELEMENTOS YA EXISTENTES
		coordenadas = window.predioFx.getForma();
		largo = coordenadas.length -1;	//La ultima coordenada no va
		if (largo>0){
			arrayConstructor = [largo];
			for (i = 0; i < largo; i++) {
				coordenada = coordenadas[i];
				lat = coordenada.x;
				lng = coordenada.y;
				arrayConstructor[i] = [lat,lng];
			}
			this.predioFx.syso(arrayConstructor);
			marker = L.polygon(arrayConstructor).addTo(map);
			drawnItems.addLayer(marker);
			bounds = marker.getBounds()
			map.fitBounds(bounds)
		}

	}
	
	map.on(L.Draw.Event.CREATED, function (event) {
		window.predioFx.syso("L.Draw.Event.CREATED");
		
		layer = event.layer;
		
		if (layer instanceof L.Polygon) {
			window.predioFx.syso("Instancia de L.Polygon");
			//Borrar todos los polígonos que hayan
			drawnItems.eachLayer(function (elemento) {
				if (elemento instanceof L.Polygon) {
					drawnItems.removeLayer(elemento);
				}
			});
			
			drawnItems.addLayer(layer);	//Se muestra el dibujo
			window.predioFx.syso("Se mostró dibujo");
	        latlngs = layer.getLatLngs();
	        poligonos =  new Array(latlngs.length);
	        for (i = 0; i < latlngs.length; i++) {
	        	ll=latlngs[i];

	            poligonos[i] =  new Array(ll.length);
	        	for (j = 0; j < ll.length; j++) {
	        		lat = ll[j].lat;
	        		lng = ll[j].lng;
		            poligonos[i][j] = new Array(2);
		            poligonos[i][j][0] = Number(lat);
		            poligonos[i][j][1] = Number(lng);
	        		
		        }
	        }
	        window.predioFx.setForma(poligonos[0], poligonos[0].length);
	        
		}
		
	});

	map.on('draw:deleted', function (e) {
        layers = e.layers;
        window.predioFx.syso("draw:deleted");
       
        layers.eachLayer(function (layer) {
           if (layer instanceof L.Polygon) {
               //Borrar las coordenadas de la forma
        	   window.predioFx.borrarForma();
           }
           
        });

    });
	
	map.on('draw:edited', function (e) {
		window.predioFx.syso("draw:edited");
		
        layers = e.layers;
        
        layers.eachLayer(function (layer) {
        	if (layer instanceof L.Polygon) {
        		window.predioFx.syso("Instancia de L.Polygon");
        		
        		window.predioFx.borrarForma();
        		
    	        latlngs = layer.getLatLngs();
    	        poligonos =  new Array(latlngs.length);
    	        for (i = 0; i < latlngs.length; i++) {
    	        	ll=latlngs[i];

    	            poligonos[i] =  new Array(ll.length);
    	        	for (j = 0; j < ll.length; j++) {
    	        		lat = ll[j].lat;
    	        		lng = ll[j].lng;
    		            poligonos[i][j] = new Array(2);
    		            poligonos[i][j][0] = Number(lat);
    		            poligonos[i][j][1] = Number(lng);
    	        		
    		        }
    	        }
    	        window.predioFx.setForma(poligonos[0], poligonos[0].length);
    	        window.predioFx.syso("Se setió nueva forma al predio");
    		}
           
        });

    });
    
}