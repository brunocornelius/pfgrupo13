package com.entidades;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import com.entidades.ZonaPotrero;
import com.interfaces.EntidadConForma;
import com.vividsolutions.jts.geom.Polygon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.OneToMany;

@Entity
@Table(name="ZONAGEOGRAFICAS", 
	uniqueConstraints = {
		@UniqueConstraint(name = "UK_ZONAS_NOMBRE", columnNames = {"NOMBRE", "ID_PREDIO","ACTIVO","HASTA"})
		//Uniques con los desde hasta?
	},
	indexes = {
			@Index(name = "IDX_ZONAGEOGRAFICA_PREDIO",  columnList="ID_PREDIO"),
			@Index(name = "IDX_ZONAGEOGRAFICA_TIPOZONA",  columnList="ID_TIPOZONA"),
	}
)
public class ZonaGeografica implements Serializable, EntidadConForma {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ZONAGEOGRAFICASG")
    @SequenceGenerator(sequenceName = "ZONAGEOGRAFICA_SEQ", allocationSize = 1, name = "ZONAGEOGRAFICASG")
	@Column(name="ID_ZONAGEOGRAFICA")
	private Integer id;
	
	@Column(name="NOMBRE", length = 40, nullable = false)
	private String nombre;
	
	@Column(name="DESCRIPCION", length = 120, nullable = false)
	private String descripcion;
	
	@Column(name="ACTIVO", nullable = false)
	private Boolean activo;
	
	@Column(name="DESDE", nullable = false)
	private Date desde;
	
	@Column(name="HASTA",nullable = true)
	private Date hasta;
	
	@Column(name="FORMA", nullable = false)
	private Polygon forma;
	
	@ManyToOne(optional = false,fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
	@JoinColumn(name = "ID_PREDIO",foreignKey = @ForeignKey(name = "FK_ZONAGEOGRAFICA_PREDIO"))
	private Predio predio;
	
	@ManyToOne(optional = false,fetch = FetchType.EAGER)
	@JoinColumn(name = "ID_TIPOZONA",foreignKey = @ForeignKey(name = "FK_ZONAGEOGRAFICA_TIPOZONA"))
	private TipoZona tipoZona;

	@OneToMany(mappedBy = "zonaGeografica",fetch = FetchType.LAZY)
	private Collection<ZonaPotrero> zonaPotreros;
	
	@Transient
	private Integer idJs;

	public ZonaGeografica() {
		super();
		this.desde = new Date(); 	//Lo seteo con la fecha actual
		this.zonaPotreros = new ArrayList<ZonaPotrero>();
	}
	
	
	
	public ZonaGeografica(String nombre, String descripcion, Boolean activo, Date desde, Polygon forma,TipoZona tipoZona) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.activo = activo;
		this.desde = desde;
		this.forma = forma;
		this.tipoZona = tipoZona;
		this.zonaPotreros = new ArrayList<ZonaPotrero>();
	}



	public ZonaGeografica(Predio predio) {
		super();
		this.desde = new Date(); 	//Lo seteo con la fecha actual
		this.predio = predio;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}

	public Polygon getForma() {
		return forma;
	}

	public void setForma(Polygon forma) {
		this.forma = forma;
	}

	public Predio getPredio() {
		return predio;
	}

	public void setPredio(Predio predio) {
		this.predio = predio;
	}

	public TipoZona getTipoZona() {
		return tipoZona;
	}

	public void setTipoZona(TipoZona tipoZona) {
		this.tipoZona = tipoZona;
	}

	public Collection<ZonaPotrero> getZonaPotreros() {
		return zonaPotreros;
	}

	public void setZonaPotreros(Collection<ZonaPotrero> zonaPotreros) {
		this.zonaPotreros = zonaPotreros;
	}

	public Integer getId() {
		return id;
	}
	
	public void addZonaPotrero(ZonaPotrero zp) {
		if (!this.zonaPotreros.contains(zp)) {
			this.zonaPotreros.add(zp);
			zp.setZonaGeografica(this);
		}
	}
	public void removeZonaPotrero(ZonaPotrero zp) {
		if (this.zonaPotreros.contains(zp)) {
			this.zonaPotreros.remove(zp);
			zp.setZonaGeografica(null);
		}
	}
	
	
	

	public Integer getIdJs() {
		return idJs;
	}
	public void setIdJs(Integer idLabel) {
		this.idJs = idLabel;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((desde == null) ? 0 : desde.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
		result = prime * result + ((predio == null) ? 0 : predio.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZonaGeografica other = (ZonaGeografica) obj;
		if (desde == null) {
			if (other.desde != null)
				return false;
		} else if (!desde.equals(other.desde))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		if (predio == null) {
			if (other.predio != null)
				return false;
		} else if (!predio.equals(other.predio))
			return false;
		return true;
	}
	@Override
	public double getAreaEnHectareas() {
		return this.forma.getArea() * 1000000;
	}
	
}