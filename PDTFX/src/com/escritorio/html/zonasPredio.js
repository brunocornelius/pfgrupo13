	/*****************************************
	 * GENERAR EL MAPA
	 *****************************************/
	var osmUrl = 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
	var osmAttrib = '&copy; <a href="http://openstreetmap.org/copyright">OpenStreetMap</a> contributors';
	var osm = L.tileLayer(osmUrl, { maxZoom: 18, attribution: osmAttrib });
	var map = new L.Map('mapid', { center: new L.LatLng(-32.241, -55.838), zoom: 8 });
	var drawnPredio = L.featureGroup().addTo(map);
	
	var drawnItems = L.featureGroup().addTo(map);

function crearMapaPrueba(){
	

	
	//CAPAS BASE DE GOOGLE Y OpenStreet MAPS
	L.control.layers(
		{
			"google": L.tileLayer('http://www.google.cn/maps/vt?lyrs=s@189&gl=cn&x={x}&y={y}&z={z}', {attribution: 'google'	}).addTo(map),
			'osm': osm,
		}, 
		{ 	'formaPredio': drawnPredio, 
			'zonas': drawnItems 
		}, 
		{ 	position: 'topleft', 
			collapsed: false 
		}
	
	)
	.addTo(map);
	
	map.addControl(new L.Control.Draw({
		position: 'topright',
//		edit: {
//		    featureGroup: drawnItems,
//		    remove: false,
//		    poly: {
//		        allowIntersection: false
//		    },
//		},
		draw: {
			polygon: {
//                guideLayers: guideLayers, 
//                snapDistance: 5,
//				allowIntersection: true, // Restricts shapes to simple polygons
				drawError: {
					color: '#e1e100', // Color the shape will turn when intersects
					message: '<strong>Error<strong> No es posible dibujar esa forma' // Message that will show when intersect
				},
				shapeOptions: {
					color: '#bada55'
				},
                showArea: true,
			},
		    circle: false,
		    circlemarker: false,
		    polyline: false,
		    rectangle: false,
		    marker: false,
		}
		
	}));
	
	
/*****************************************
 * CARGAR LA FORMA DEL PREDIO
 *****************************************/
	coordenadas = window.jsDriver.getPredioFx().getForma();
	largo = coordenadas.length -1;	//La ultima coordenada no va
	if (largo>0){
		arrayConstructor = [largo];
		for (i = 0; i < largo; i++) {
			coordenada = coordenadas[i];
			lat = coordenada.x;
			lng = coordenada.y;
			arrayConstructor[i] = [lat,lng];
		}
		
		marker = L.polygon(arrayConstructor).addTo(map);
		drawnPredio.addLayer(marker);
		bounds = marker.getBounds()
		map.fitBounds(bounds)
	}


/*****************************************
 * EVENTOS para la capa editable
 *****************************************/
	
	map.on(L.Draw.Event.CREATED, function (event) {
		//solo se informa que se creo, el jsDriver decide si dibujar o no
		window.jsDriver.getPredioFx().syso("L.Draw.Event.CREATED");
		
		layer = event.layer;
		
		if (layer instanceof L.Polygon) {
			
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
	        
	        
	        window.jsDriver.addZonaGeografica(poligonos[0], poligonos[0].length);
	        
		}
		
	});
    
}

function borrarZona(idZona){
	drawnItems.eachLayer(function (elemento) {
		
		if (elemento.idLayer == idZona) {
			window.jsDriver.getPredioFx().syso("IdLayer a borrar: ");
			window.jsDriver.getPredioFx().syso(elemento.idLayer);
			drawnItems.removeLayer(elemento);
		}
	});
}

function dibujarZona(){
//	window.jsDriver.getPredioFx().syso("");
//	window.jsDriver.getPredioFx().syso("");
//	window.jsDriver.getPredioFx().syso("");
	zonaFx = window.ultimaZona;
	coordenadas = zonaFx.getForma();
	largo = coordenadas.length;	//La ultima coordenada si va
	if (largo>0){
		str="Cant coordenadas: ";
//		window.jsDriver.getPredioFx().syso(str.concat(largo));
		arrayConstructor = [largo];
		for (i = 0; i < largo; i++) {
			coordenada = coordenadas[i];
			if (typeof coordenada.x === 'undefined' || coordenada.x === null) {
				str="coordenada.x es UNDEFINED en coordenada i=";
				window.jsDriver.getPredioFx().syso(str.concat(i));
			}else{
				lat = coordenada.x;
				lng = coordenada.y;
				arrayConstructor[i] = [lat,lng];
			}
		}
		marker = L.polygon(arrayConstructor).addTo(map);
		marker.idLayer = zonaFx.getIdJs();
		
		nomLayer = zonaFx.toString();
		
		
		marker.bindTooltip(nomLayer, {permanent: true}).openTooltip();
		drawnItems.addLayer(marker);
		colorZona = zonaFx.getColor();
		marker.setStyle({
		    color: colorZona
		});
	}
}

function actualizarTooltip(idZona,cadena){
	drawnItems.eachLayer(function (elemento) {
		if (elemento.idLayer == idZona) {
			marker.bindTooltip(cadena, {permanent: true}).openTooltip();
		}
	});
}

function actualizarColor(idZona,colorZona){
	drawnItems.eachLayer(function (elemento) {
		if (elemento.idLayer == idZona) {
			elemento.setStyle({
				color: colorZona	//color del relleno
			});
		}
	});
}