package ec.edu.ups.controller;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.annotation.FacesConfig;
import javax.inject.Named;

import ec.edu.ups.EJB.FacturaCabeceraFacade;
import ec.edu.ups.EJB.FacturaDetalleFacade;
import ec.edu.ups.EJB.PedidoCabeceraFacade;
import ec.edu.ups.EJB.ProductoFacade;
import ec.edu.ups.entidades.FacturaCabecera;
import ec.edu.ups.entidades.FacturaDetalle;
import ec.edu.ups.entidades.PedidoCabecera;
import ec.edu.ups.entidades.PedidoDetalle;
import ec.edu.ups.entidades.Producto;

@FacesConfig(version = FacesConfig.Version.JSF_2_3)
@Named
@RequestScoped
public class PedidosCabeceraBean implements Serializable{

	private static final long serialVersionUID = 1L;
	@EJB
	private PedidoCabeceraFacade ejbPedidoCabecera;
	@EJB
	private FacturaCabeceraFacade ejbFacturaCabecera;
	@EJB
	private FacturaDetalleFacade ejbFacturaDetalle;
	@EJB
	private ProductoFacade ejbProducto;
	
	public static List<PedidoCabecera> cabeceras = new ArrayList<PedidoCabecera>();
	private List<PedidoDetalle> detalles = new ArrayList<PedidoDetalle>();
	private String cedula;
	
	@PostConstruct
	public void init(){
		cabeceras=ejbPedidoCabecera.pedidosCabeceraReves();
		//detalles = new ArrayList<PedidoDetalle>();
	}
	
	public void obtenerDetalles(PedidoCabecera pedCabecera) {
		
		System.out.println("Detalles>>>>>>>>>>>>.."+pedCabecera.getPedidosDetalle().size());
		detalles = ejbPedidoCabecera.pedidosDetalleCabecera(pedCabecera.getId()); 
	}
	
	
	public void finalizarPedido(PedidoCabecera pedCabecera) {

		if(pedCabecera.getEstado().equals("Finalizado")!=true) {
			if (pedCabecera.getEstado().equals("En Proceso")!=true) {
				if (pedCabecera.getEstado().equals("En camino")==true) {

					pedCabecera.setEstado("Finalizado");
					ejbPedidoCabecera.edit(pedCabecera);

				}
			}
		}
	}
	
	public void enviarPedido(PedidoCabecera pedCabecera) {

		if(pedCabecera.getEstado().equals("Finalizado")!=true) {
			
			if (pedCabecera.getEstado().equals("En Proceso")==true) {

				pedCabecera.setEstado("En camino");
				ejbPedidoCabecera.edit(pedCabecera);

			}
			
		}
	}
	
	public void facturarPedido(PedidoCabecera pedCabecera) {
		
		if(pedCabecera.getEstado().equals("Finalizado")!=true ) {
			
			if (pedCabecera.getEstado().equals("En camino")!= true) {
				
				pedCabecera.setEstado("En Proceso");
				ejbPedidoCabecera.edit(pedCabecera);
				
				PedidoCabecera cab = ejbPedidoCabecera.find(pedCabecera.getId());
				FacturaCabecera facturaCabecera = new FacturaCabecera(0, new Date(), cab.getSubtotal(), 
																	cab.getTotal(), cab.getIva(), 
																	'A', cab.getPersona());
				ejbFacturaCabecera.create(facturaCabecera);

				
				List<FacturaDetalle> facturasDetalle = new ArrayList<FacturaDetalle>();
				
				for(PedidoDetalle pedido : ejbPedidoCabecera.pedidosDetalleCabecera(cab.getId())) {
				
					int newStock = pedido.getProducto().getStock() - pedido.getCantidad();
					Producto pro = pedido.getProducto();
					pro.setStock(newStock);
					ejbProducto.edit(pro);
					
					
					FacturaDetalle facturaDetalle = new FacturaDetalle(0, pedido.getCantidad(), pedido.getTotal(), 
																	 facturaCabecera, pro);
				
					facturasDetalle.add(facturaDetalle);
					ejbFacturaDetalle.create(facturaDetalle);
				}
				
				
				facturaCabecera.setFacturasDetalle(facturasDetalle);
				ejbFacturaCabecera.edit(facturaCabecera);
			}
		}
		
	}
	



	public PedidoCabeceraFacade getEjbPedidoCabecera() {
		return ejbPedidoCabecera;
	}

	public void setEjbPedidoCabecera(PedidoCabeceraFacade ejbPedidoCabecera) {
		this.ejbPedidoCabecera = ejbPedidoCabecera;
	}

	public List<PedidoCabecera> getCabeceras() {
		return cabeceras;
	}

	public void setCabeceras(List<PedidoCabecera> cabeceras) {
		this.cabeceras = cabeceras;
	}

	public List<PedidoDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<PedidoDetalle> detalles) {
		this.detalles = detalles;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	
	
}
