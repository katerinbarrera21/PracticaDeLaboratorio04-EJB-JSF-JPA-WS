package ec.edu.ups.EJB;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ec.edu.ups.entidades.PedidoCabecera;
import ec.edu.ups.entidades.PedidoDetalle;

@Stateless
public class PedidoCabeceraFacade  extends AbstractFacade<PedidoCabecera>{
	@PersistenceContext(unitName = "PracticaDeLaboratorio04-EJB-JSF-JPA-WS")
    private EntityManager em;

    public PedidoCabeceraFacade() {
        super(PedidoCabecera.class);
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public List<PedidoCabecera> pedidosCabeceraReves(){
    	List<PedidoCabecera> cabeceras=new ArrayList<PedidoCabecera>();
    	String consulta = "Select pC From PedidoCabecera pC order by pc.id desc";
    	try {
    		cabeceras = em.createQuery(consulta).getResultList();
    	}catch(Exception e) {
    		System.out.println(">>>Warning (PersonaFacade:buscarPorCedula: )"+e.getMessage());
    	}
    	return cabeceras;
    }
    
    public List<PedidoCabecera> pedidosCabeceraFiltrada(String cedula){
    	List<PedidoCabecera> cabeceras=new ArrayList<PedidoCabecera>();
    	String consulta = "Select pC From PedidoCabecera pC pc.persona.cedula='"+cedula+"' order by pc.id desc";
    	try {
    		cabeceras = em.createQuery(consulta).getResultList();
    	}catch(Exception e) {
    		System.out.println(">>>Warning (FacturaCabeceraFacade:facturasCabeceraFiltrada: )"+e.getMessage());
    	}
    	return cabeceras;
    }
    
    public float calcularSubtotal(List<PedidoDetalle> pedidosDetalles) {
		float cont = (float) 0.0;
		
    	for (PedidoDetalle pedidoDetalle : pedidosDetalles) {
    		cont = cont+(pedidoDetalle.getCantidad()*pedidoDetalle.getProducto().getPrecio());
		}
    	return cont;
	}
    
    public float calcularTotal(float subtotal, float iva) {		
    	BigDecimal bigDecimal = new BigDecimal(subtotal*iva).setScale(2, RoundingMode.UP);
    	return bigDecimal.floatValue();
	}
    
}