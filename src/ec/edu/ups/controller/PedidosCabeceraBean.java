package ec.edu.ups.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
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
	private String estado;
	
	
	
	@PostConstruct
	public void init(){
		cabeceras=ejbPedidoCabecera.pedidosCabeceraReves();
		//detalles = new ArrayList<PedidoDetalle>();
	}
	
	public void obtenerDetalles(PedidoCabecera pedCabecera) {
		
		System.out.println("Detalles>>>>>>>>>>>>.."+pedCabecera.getPedidosDetalle().size());
		PedidoCabecera p =  ejbPedidoCabecera.find(pedCabecera.getId());
		System.out.println("Detalles de p: >>>>>>>>>>>>.."+p.getPedidosDetalle().size());
		detalles = p.getPedidosDetalle(); 
	}
	
	public void cambiarEstado(PedidoCabecera pedCabecera) {
		
		if(pedCabecera.getEstado().equals("Finalizado")!=true) {
			
			pedCabecera.setEstado(estado);
			ejbPedidoCabecera.edit(pedCabecera);
			
			
			if(pedCabecera.getEstado().equals("En Proceso")==true) {
				
				
				FacturaCabecera facturaCabecera = new FacturaCabecera(0, new Date(), pedCabecera.getSubtotal(), 
																	pedCabecera.getTotal(), pedCabecera.getIva(), 
																	'A', pedCabecera.getPersona());
				
				List<FacturaDetalle> facturasDetalle = new ArrayList<FacturaDetalle>();
				
				for(PedidoDetalle pedido : pedCabecera.getPedidosDetalle()) {
					
					
					
					FacturaDetalle facturaDetalle = new FacturaDetalle(0, pedido.getCantidad(), pedido.getTotal(), 
																	 facturaCabecera, pedido.getProducto());
				
					
					facturasDetalle.add(facturaDetalle);
				}
				
				for (PedidoDetalle pedido : pedCabecera.getPedidosDetalle()) {

					for (int i = 0; i < pedido.getPedidoBodega().getProductos().size(); i++) {
						
						if (pedido.getProducto().getId() == pedido.getPedidoBodega().getProductos().get(i).getId()) {
							int nuevoStock=pedido.getPedidoBodega().getProductos().get(i).getStock()-pedido.getCantidad();
							Producto prod = pedido.getPedidoBodega().getProductos().get(i);
							prod.setStock(nuevoStock);
							ejbProducto.edit(prod);
						}
					}
				}
				
				facturaCabecera.setFacturasDetalle(facturasDetalle);
				ejbFacturaCabecera.create(facturaCabecera);
					
			}
		}
	}
	
	public void filtrarFacturaCabecera() {
		System.out.println("si esta entrando");
		cabeceras=ejbPedidoCabecera.pedidosCabeceraFiltrada(cedula);
		if(cabeceras==null || cabeceras.size() == 0) {
			System.out.println("no consigue nada");
			cabeceras=ejbPedidoCabecera.pedidosCabeceraReves();
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

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
}
