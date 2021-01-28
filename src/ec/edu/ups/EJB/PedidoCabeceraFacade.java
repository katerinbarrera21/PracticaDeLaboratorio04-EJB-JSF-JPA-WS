package ec.edu.ups.EJB;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ec.edu.ups.entidades.PedidoCabecera;

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
    
}