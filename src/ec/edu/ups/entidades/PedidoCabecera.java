package ec.edu.ups.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: PedidoCabecera
 *
 */
@Entity

public class PedidoCabecera implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private Date fecha;
	private float subtotal;
	private float total;
	private float iva;
	private String estado;
	
	@ManyToOne
	@JoinColumn
	private Persona personaPedidos;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pedidoCabecera")
	private List<PedidoDetalle> pedidosDetalle = new ArrayList<PedidoDetalle>();

	public PedidoCabecera() {
		super();
		
	}
	
	public PedidoCabecera(Date fecha, float subtotal, float total, float iva, String estado, Persona persona) {
		this.setFecha(fecha);
		this.setSubtotal(subtotal);
		this.setTotal(total);
		this.setSubtotal(subtotal);
		this.setTotal(total);
		this.setIva(iva);
		this.setEstado(estado);
		this.setPersona(persona);
		
	}

	
	
	
	public Persona getPersonaPedidos() {
		return personaPedidos;
	}

	public void setPersonaPedidos(Persona personaPedidos) {
		this.personaPedidos = personaPedidos;
	}

	public List<PedidoDetalle> getPedidosDetalle() {
		return pedidosDetalle;
	}

	public void setPedidosDetalle(List<PedidoDetalle> pedidosDetalle) {
		this.pedidosDetalle = pedidosDetalle;
	}
	
	public void addPedidosDetalle(PedidoDetalle pedidoDetalle) {
		this.pedidosDetalle.add(pedidoDetalle);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public float getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(float subtotal) {
		this.subtotal = subtotal;
	}

	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	public float getIva() {
		return iva;
	}

	public void setIva(float iva) {
		this.iva = iva;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Persona getPersona() {
		return personaPedidos;
	}

	public void setPersona(Persona persona) {
		this.personaPedidos = persona;
	}

	@Override
	public String toString() {
		return "PedidoCabecera [id=" + id + ", fecha=" + fecha + ", subtotal=" + subtotal + ", total=" + total
				+ ", iva=" + iva + ", estado=" + estado + ", personaPedidos=" + personaPedidos + ", pedidosDetalle="
				+ pedidosDetalle + "]";
	}

   
}