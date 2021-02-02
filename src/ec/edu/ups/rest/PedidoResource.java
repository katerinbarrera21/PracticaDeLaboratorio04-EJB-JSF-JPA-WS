package ec.edu.ups.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ec.edu.ups.EJB.BodegaFacade;
import ec.edu.ups.EJB.CategoriaFacade;
import ec.edu.ups.EJB.FacturaCabeceraFacade;
import ec.edu.ups.EJB.FacturaDetalleFacade;
import ec.edu.ups.EJB.PedidoCabeceraFacade;
import ec.edu.ups.EJB.PedidoDetalleFacade;
import ec.edu.ups.EJB.PersonaFacade;
import ec.edu.ups.EJB.ProductoFacade;
import ec.edu.ups.entidades.Bodega;
import ec.edu.ups.entidades.Categoria;
import ec.edu.ups.entidades.Ciudad;
import ec.edu.ups.entidades.FacturaCabecera;
import ec.edu.ups.entidades.FacturaDetalle;
import ec.edu.ups.entidades.PedidoCabecera;
import ec.edu.ups.entidades.PedidoDetalle;
import ec.edu.ups.entidades.Persona;
import ec.edu.ups.entidades.Producto;
import ec.edu.ups.entidades.Provincia;

@Path("/pedido/")
public class PedidoResource {
 	
	@EJB
    private PedidoCabeceraFacade pedidoCabeceraFacade;
	@EJB
    private PedidoDetalleFacade pedidoDetalleFacade;
    @EJB
    private PersonaFacade personaFacade;
    @EJB
    private ProductoFacade productoFacade;
    @EJB
    private FacturaCabeceraFacade facturaCabeceraFacade;
    @EJB
    private FacturaDetalleFacade facturaDetalleFacade;
    @EJB
    private BodegaFacade bodegaFacade;
    @EJB
    private CategoriaFacade categoriaFacade;

    
    
    
    @POST
    @Path("/agregarPedido")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response agregarPedido(@FormParam("bodega_id") String bodega_id,
    							@FormParam("pedidoCab_id") String pedidoCab_id, 
					    		@FormParam("producto_Id") String productoID, 
					    		@FormParam("cedula_Id") String cedulaid, 
					    		@FormParam("cantidad") String cantidad) throws Exception {
					    	
    	System.out.println("producto_Id "+ productoID);
        System.out.println("cedula "+ cedulaid);
        System.out.println("cantidad "+ cantidad);
        PedidoCabecera pedidoCab = null;
        Persona persona = null;
        Producto producto = null;
        Bodega bodega = null;
        int cant=0;
        
        
        try {
        	bodega = bodegaFacade.find(Integer.parseInt(bodega_id));
        	pedidoCab = pedidoCabeceraFacade.find(Integer.parseInt(pedidoCab_id));
        	persona = personaFacade.buscarPorCedula(cedulaid);
            producto = productoFacade.find(Integer.parseInt(productoID));
            cant = Integer.parseInt(cantidad);
		} catch (Exception e) {
			System.out.println("Error al buscar la informacion");
		}
        
        boolean verificacion = false;
        
        for (Producto pro : bodega.getProductos()) {
        	if (pro.getId() == producto.getId()) {
    			verificacion = true;
    		}
		}
        
        
        if (verificacion) {
        	if (pedidoCab != null) {
            	
            	PedidoDetalle detalle = new PedidoDetalle();
    	        float total = detalle.calcularTotal(cant, producto.getPrecio());
    	        detalle.setCantidad(cant);
    	        detalle.setTotal(total);
    	        detalle.setPedidoCabecera(pedidoCab);
    	        detalle.setProducto(producto);
    	        detalle.setPedidoBodega(bodega);
    	        pedidoDetalleFacade.create(detalle);
    	        
    	        
    	        pedidoCab.setSubtotal(pedidoCab.getSubtotal()+detalle.getTotal());
    	        
    	        pedidoCab.setTotal(pedidoCab.getSubtotal()+(pedidoCab.getIva()*pedidoCab.getSubtotal()));
    	        
    	        pedidoCabeceraFacade.edit(pedidoCab);
    		}else {
    			
    			PedidoCabecera pedido= new PedidoCabecera();
    			pedido.setFecha(new Date());
    	        pedido.setIva((float)0.14);
    	        pedido.setSubtotal((float)0.0);
    	        pedido.setTotal((float)0.0);
    	        pedido.setEstado("Enviando");
    	        pedido.setPersona(persona);
    	        
    	        
    	        PedidoDetalle detalle = new PedidoDetalle();
    	        float total = detalle.calcularTotal(cant, producto.getPrecio());
    	        detalle.setCantidad(Integer.parseInt(cantidad));
    	        detalle.setTotal(total);
    	        detalle.setPedidoCabecera(pedido);
    	        detalle.setProducto(producto);
    	        detalle.setPedidoBodega(bodega);
    	        
    	        float subtotal=(float) (Math.round(detalle.getTotal()*100d)/100d);
    	        
    	        pedido.setSubtotal(subtotal);
    	        pedido.setTotal(pedido.getSubtotal()+(pedido.getIva()*pedido.getSubtotal()));
    	        
    	        detalle.setPedidoCabecera(pedido);
    	        
    	        pedidoCabeceraFacade.create(pedido);
    	        pedidoDetalleFacade.create(detalle);
    		}
        	
        	 return Response.ok("Se agrego el detalle del producto: " + productoID+" en el pedidoCabecera"+ pedidoCab_id + " <--> " + cedulaid)
                     .header("Access-Control-Allow-Origins", "*")
                     .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                     .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                     .build();
            
		}else {
			
			 return Response.ok("No se agrego el detalle del producto: " + productoID+" en el pedidoCabecera"+ pedidoCab_id + " porque no existe este producto en la bodega <--> " + cedulaid)
		                .header("Access-Control-Allow-Origins", "*")
		                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
		                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
		                .build();
		}
        
        
        
       
    }


    
    @POST
    @Path("/crearPedido")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response crearPedido(@FormParam("pedidoCabID") String pedidoID) throws Exception {
        
    	PedidoCabecera pedido = null;
        Persona persona = null;
        try {
			pedido = pedidoCabeceraFacade.find(Integer.parseInt(pedidoID));
		} catch (Exception e) {
			System.out.println("Error al obtener informacion");
		}
        
        if (pedido!= null) {
        	pedido.setEstado("Receptado");
        	pedidoCabeceraFacade.edit(pedido);
        	return Response.ok("Se logro facturar el pedido, se encuentra en estado de En Proceso de revision")
                    .header("Access-Control-Allow-Origins", "*")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                    .build();
        
        }else	
        	return Response.ok("No se logro facturar el pedido" )
	                .header("Access-Control-Allow-Origins", "*")
	                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
	                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
	                .build();
    }
    
    
    @POST
    @Path("/facturarPedido")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response facturarPedido(@FormParam("pedidoCabID") String pedidoID) throws Exception {
        
    	PedidoCabecera pedido = null;
        
    	try {
			pedido = pedidoCabeceraFacade.find(Integer.parseInt(pedidoID));
		} catch (Exception e) {
			System.out.println("Error al obtener informacion");
		}
        
        if (pedido!= null) {
        	pedido.setEstado("En camino");
        	pedidoCabeceraFacade.edit(pedido);
        	pedido = pedidoCabeceraFacade.find(Integer.parseInt(pedidoID));
            
            
            FacturaCabecera facturaCabecera = new FacturaCabecera();
            facturaCabecera = new FacturaCabecera(0, new Date(), (float)0.0, (float)0.0, (float)14.0, 'N', pedido.getPersona());
            FacturaDetalle detalleFactura;
            List<PedidoDetalle> detallesPedido = pedido.getPedidosDetalle();
            
            for (PedidoDetalle pedidoDetalle : detallesPedido) {
            	detalleFactura = new FacturaDetalle(0, pedidoDetalle.getCantidad(), pedidoDetalle.getTotal(), facturaCabecera, pedidoDetalle.getProducto());
            	facturaCabecera.addFacturasDetalle(detalleFactura);
            }
            facturaCabecera.setEstado('A');
            facturaCabecera.setSubtotal(pedido.getSubtotal());
            facturaCabecera.setTotal(pedido.getTotal());
            
            facturaCabeceraFacade.create(facturaCabecera);
            pedido.setEstado("En proceso");
            pedidoCabeceraFacade.edit(pedido);

        	
        	
        	
        	return Response.ok("Se logro facturar el pedido, se encuentra en estado de En Proceso de revision")
                    .header("Access-Control-Allow-Origins", "*")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                    .build();
        
        }else	
        	return Response.ok("No se logro facturar el pedido" )
	                .header("Access-Control-Allow-Origins", "*")
	                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
	                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
	                .build();
    }
    
    
    
    
    
    @POST
    @Path("/listarProductos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response listprods(@FormParam("idBodega") String id) {
    	Jsonb jsonb = JsonbBuilder.create();
    	int idBodega = Integer.parseInt(id);
    	List<Producto> productos = new ArrayList<Producto>();;
    	List<Integer> idCategorias = new ArrayList<Integer>();
    	

    	Bodega bodega = bodegaFacade.find(idBodega);
    	List<Producto> productosTemp = bodega.getProductos();
    	
    	
		for (int i = 0; i < bodega.getProductos().size(); i++) {
			idCategorias .add(bodega.getProductos().get(i).getCategoria().getId());
			
		}
    	
    	Collections.sort(idCategorias);
    	
    	List<Integer> categoriasID = idCategorias.stream().distinct().collect(Collectors.toList());
    	List<Categoria> categorias = new ArrayList<Categoria>();
    	boolean bandera = true;
    	
    	
    	for (Integer idCat : categoriasID) {
			Categoria c = categoriaFacade.find(idCat);
			Categoria categoriaSerealizable = new Categoria(c.getId(), c.getNombre());
			
			for (int i = 0; i < bodega.getProductos().size(); i++) {

				if (bodega.getProductos().get(i).getCategoria().getId() == c.getId()) {
					
					Producto pro = new Producto();
					pro.setId(bodega.getProductos().get(i).getId());
					pro.setNombre(bodega.getProductos().get(i).getNombre());
					pro.setPrecio(bodega.getProductos().get(i).getPrecio());
					pro.setStock(bodega.getProductos().get(i).getStock());
					pro.setEstado(bodega.getProductos().get(i).getEstado());
					
					categoriaSerealizable.addProductos(pro);
				}
			}

			categorias.add(categoriaSerealizable);
		}
    	
    	 
		return Response.status(201).entity(jsonb.toJson(categorias))
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE").build();
    
    }
    
    @POST
    @Path("/listarPedidos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response pedidosEstados(@FormParam("cedula") String cedula) {
	
    	Persona cliente = personaFacade.buscarPorCedula(cedula);
    	
    	List<PedidoCabecera> pedido = new ArrayList<PedidoCabecera>();
    	
    	
    	
    	for (PedidoCabecera pedidoCabecera : cliente.getPersonaPedidos()) {
    		
    		Persona p = new Persona(pedidoCabecera.getPersona().getNombre(), pedidoCabecera.getPersona().getApellido(), pedidoCabecera.getPersona().getCedula()
    								, pedidoCabecera.getPersona().getDireccion(), pedidoCabecera.getPersona().getTelefono()
    								, pedidoCabecera.getPersona().getCorreo(), pedidoCabecera.getPersona().getPassword(), pedidoCabecera.getPersona().getRol());
    		
			PedidoCabecera ped = new PedidoCabecera(pedidoCabecera.getFecha(), pedidoCabecera.getSubtotal(), pedidoCabecera.getTotal()
									, pedidoCabecera.getIva(), pedidoCabecera.getEstado(), p);
			
			pedido.add(ped);
		}
    	
    	
    	Jsonb jsonb = JsonbBuilder.create();
    	return Response.status(201).entity(jsonb.toJson(pedido))
    		.header("Access-Control-Allow-Origin", "*")
    		.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
    		.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE").build();
    }

   
}
