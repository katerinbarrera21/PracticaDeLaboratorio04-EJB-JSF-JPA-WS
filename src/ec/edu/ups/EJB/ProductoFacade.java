package ec.edu.ups.EJB;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ec.edu.ups.entidades.Categoria;
import ec.edu.ups.entidades.Producto;
import static java.lang.String.valueOf;
@Stateless
public class ProductoFacade extends AbstractFacade<Producto> {
	@PersistenceContext(unitName = "PracticaDeLaboratorio04-EJB-JSF-JPA-WS")
    private EntityManager em;

    public ProductoFacade() {
        super(Producto.class);
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public List<Producto> buscarPorNombre (String nombre) {
    	List<Producto> productos=new ArrayList<Producto>();
    	String consulta = "Select p From Producto p Where p.nombre like '%"+nombre+"%'";
    	try {
    		productos= em.createQuery(consulta).getResultList();
    	}catch(Exception e) {
    		System.out.println(">>>Warning (ProductoFacade:buscarPorNombre: )"+e.getMessage());
    	}
    	return productos;
    }
    
    public List<Producto> buscarPorNombreYBodega(String nombre,int id) {
    	List<Producto> productos=new ArrayList<Producto>();
    	String consulta = "Select p From Producto p Where p.nombre like '%"+nombre+"%' and p.bodegas.id="+id;
    	try {
    		productos= em.createQuery(consulta).getResultList();
    	}catch(Exception e) {
    		System.out.println(">>>Warning (ProductoFacade:buscarPorNombre: )"+e.getMessage());
    	}
    	return productos;
    }
    
    
    
    private List<Categoria> categoriasList;
    private List<Integer> codigoProductos;
    
    @SuppressWarnings("unchecked")
	public List<Integer> getProductosPorBodega(int codigoBodega){
        
    	List<Producto> productos=new ArrayList<Producto>();
    	String consulta = "Select p From Producto p Where p.bodegas.id="+codigoBodega;
    	try {
    		productos= em.createQuery(consulta).getResultList();
    	}catch(Exception e) {
    		System.out.println(">>>Warning (ProductoFacade:getProductosPorBodega: )"+e.getMessage());
    	}
    	
        if(!productos.isEmpty()){
        	productos.forEach(e-> {
                codigoProductos.add(e.getId());
            });
        }
        return codigoProductos;
    }
    
    public List<Categoria> getCategorias(int codigoBodega){
        categoriasList = new ArrayList<Categoria>();
        List<Integer> codigosProductos = getProductosPorBodega(codigoBodega);
        
        System.out.println("Size codigos: "+codigosProductos.size());
        
        if(!codigosProductos.isEmpty()){
            codigosProductos.forEach(e->{
                categoriasList.add(super.find(e).getCategoria());
            });
            
            
            Set<Categoria> categorias = new HashSet<>(categoriasList);
            categoriasList.clear();
            
            categorias.forEach(e->{e.setProductos(null); categoriasList.add(e);});
            
            System.out.println("Size cate: "+categoriasList.size());
            return categoriasList;
        }else
            return categoriasList;
    }
}
