package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import utils.TipoVid;

@Entity
@Table(name= "vid")
public class Vid {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = true)
	private int id;
	@Column(name = "tipo_vid", nullable = true)
	private String vid;
	@Column(name = "cantidad", nullable = true)
	private String cantidad;
	@ManyToOne
	@JoinColumn(name = "id_bodega") 
    private Bodega bodega;
	
	public Vid() {}
		
	public Vid(String vid, String cantidad) {
		this.vid = vid;
		this.cantidad = cantidad;
	}
	public int getId() {
		return this.id;
	}
	public String getVid() {
		return vid;
	}
	public String getCantidad() {
		return cantidad;
	}
	
	public Bodega getBodega() {
		return this.bodega;	
	}
	
	@Override
	public String toString() {
		return "Vid [vid=" + (vid.equals("0") ? "blanca" : "negra")  + ", cantidad=" + cantidad + "]";
	}
	
	public void setBodega(Bodega bodega) {
		this.bodega = bodega;
	}
}
