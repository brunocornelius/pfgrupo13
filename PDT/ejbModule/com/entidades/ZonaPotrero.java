package com.entidades;

import java.io.Serializable;
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
import javax.persistence.UniqueConstraint;
import com.entidades.Potrero;
import com.interfaces.EntidadConForma;
import com.vividsolutions.jts.geom.Polygon;

@Entity
@Table(
	name = "ZONAPOTREROS",
//	uniqueConstraints = {
//		@UniqueConstraint(name = "UK_POTRERO_ZONA", columnNames = { "ID_POTRERO", "ID_ZONAGEOGRAFICA" }) 
//	}, 
	indexes = {
		//Aca definimos los indices necesarios, ya que hibernate no genera índices automáticamente por lo visto
		@Index(name = "IDX_ZONAPOTRERO_POTRERO", columnList = "ID_POTRERO"),
		@Index(name = "IDX_ZONAPOTRERO_ZONAGEO", columnList = "ID_ZONAGEOGRAFICA"),
	}
)
public class ZonaPotrero implements Serializable, EntidadConForma {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ZONAPOTREROSSG")
	@SequenceGenerator(sequenceName = "ZONAPOTREROS_SEQ", allocationSize = 1, name = "ZONAPOTREROSSG")
	@Column(name = "ID_ZONAPOTRERO")
	private Integer id;
	
	@Column(name="ACTIVO", nullable = false)
	private Boolean activo;

	@Column(name = "DESDE", nullable = false)
	private Date desde;

	@Column(name = "HASTA", nullable = true)
	private Date hasta;

	@Column(name="FORMA", nullable = false)
	private Polygon forma;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {CascadeType.ALL})	//El cascade type ALL es para que no salte la excepcion org.hibernate.TransientPropertyValueException: Not-null property references a transient value
	@JoinColumn(name = "ID_ZONAGEOGRAFICA",foreignKey = @ForeignKey(name = "FK_ZONAPOTRERO_ZONAGEOGRAFICA"))
	private ZonaGeografica zonaGeografica;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)	
	@JoinColumn(name = "ID_POTRERO",foreignKey = @ForeignKey(name = "FK_ZONAPOTRERO_POTRERO"))
	private Potrero potrero;

	public ZonaPotrero() {
		super();
		this.desde = new Date(); 	//Lo seteo con la fecha actual
		//No hay coleccciones para inicializar
	}
	
	public ZonaPotrero(Potrero potrero, ZonaGeografica zona, Date desde, boolean activo, Polygon forma) {
		super();
		this.desde = desde;
		this.potrero = potrero;
		this.zonaGeografica = zona;
		this.activo = activo;
		this.forma = forma;
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

	public ZonaGeografica getZonaGeografica() {
		return zonaGeografica;
	}

	public void setZonaGeografica(ZonaGeografica zonaGeografica) {
		this.zonaGeografica = zonaGeografica;
	}

	public Potrero getPotrero() {
		return potrero;
	}

	public void setPotrero(Potrero potrero) {
		this.potrero = potrero;
	}

	public Integer getId() {
		return id;
	}
	
	

	public Polygon getForma() {
		return forma;
	}

	public void setForma(Polygon forma) {
		this.forma = forma;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((potrero == null) ? 0 : potrero.hashCode());
		result = prime * result + ((zonaGeografica == null) ? 0 : zonaGeografica.hashCode());
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
		ZonaPotrero other = (ZonaPotrero) obj;
		if (potrero == null) {
			if (other.potrero != null)
				return false;
		} else if (!potrero.equals(other.potrero))
			return false;
		if (zonaGeografica == null) {
			if (other.zonaGeografica != null)
				return false;
		} else if (!zonaGeografica.equals(other.zonaGeografica))
			return false;
		return true;
	}

	@Override
	public double getAreaEnHectareas() {
		return this.forma.getArea() * 1000000;
	}
	

}
