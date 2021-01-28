package ec.edu.ups.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import ec.edu.ups.EJB.FacturaCabeceraFacade;
import ec.edu.ups.EJB.FacturaDetalleFacade;
import ec.edu.ups.EJB.PedidoCabeceraFacade;
import ec.edu.ups.EJB.PedidoDetalleFacade;
import ec.edu.ups.EJB.PersonaFacade;
import ec.edu.ups.EJB.ProductoFacade;
import ec.edu.ups.entidades.FacturaCabecera;
import ec.edu.ups.entidades.FacturaDetalle;
import ec.edu.ups.entidades.PedidoCabecera;
import ec.edu.ups.entidades.PedidoDetalle;
import ec.edu.ups.entidades.Persona;
import ec.edu.ups.entidades.Producto;

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

    
    
    
	//SCORPION CODE START
    @POST
    @Path("crearPedido")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response crearPedido(@FormParam("factura_Cab_ID") String facturaCabID, @FormParam("producto_Id") String productoID, @FormParam("cedula_Id") String cedulaid, @FormParam("cantidad") String cantidad) throws Exception {
    	
    	System.out.println("producto_Id "+ productoID);
        System.out.println("cedula "+ cedulaid);
        System.out.println("cantidad "+ cantidad);
        PedidoCabecera pedidoCab = null;
        Persona persona = null;
        Producto producto = null;
        int cant=0;
        try {
        	pedidoCab = pedidoCabeceraFacade.find(Integer.parseInt(facturaCabID));
        	persona = personaFacade.buscarPorCedula(cedulaid);
            producto = productoFacade.find(Integer.parseInt(productoID));
            cant = Integer.parseInt(cantidad);
		} catch (Exception e) {
			System.out.println("Error al buscar la informacion");
		}
        
        
        if (pedidoCab != null) {
        	
        	PedidoDetalle detalle = new PedidoDetalle();
	        float total = detalle.calcularTotal(cant, producto.getPrecio());
	        detalle.setCantidad(Integer.parseInt(cantidad));
	        detalle.setTotal(total);
	        detalle.setPedidoCabecera(pedidoCab);
	        detalle.setProducto(producto);
	        pedidoDetalleFacade.create(detalle);
	        
	        
	        pedidoCab.setSubtotal(pedidoCab.getSubtotal()+detalle.getTotal());
	        pedidoCab.setTotal(pedidoCab.getTotal()+pedidoCab.getSubtotal());
	        
	        pedidoCabeceraFacade.edit(pedidoCab);
		}else {
			
			PedidoCabecera pedido= new PedidoCabecera();
			pedido.setFecha(new Date());
	        pedido.setIva((float)14.0);
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
	       
	        pedido.setSubtotal(total*pedido.getIva());
	        pedido.setTotal(pedido.getSubtotal());
	       
	        pedidoCabeceraFacade.create(pedido);
	        pedidoDetalleFacade.create(detalle);
		}
        
        
        return Response.ok("Se agrego el detalle del producto: " + productoID+" en el pedidoCabecera"+ facturaCabID + " <--> " + cedulaid)
                .header("Access-Control-Allow-Origins", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .build();
    }

    
    @POST
    @Path("confirmarPedido")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response confirmarPedido(@FormParam("pedidoCabID") String pedidoID, @FormParam("cedula_Id") String cedulaid) throws Exception {
        
    	System.out.println("pedido a confirmar cedula "+ cedulaid);
        PedidoCabecera pedido = null;
        Persona persona;
        try {
			pedido = pedidoCabeceraFacade.find(Integer.parseInt(pedidoID));
			persona = personaFacade.buscarPorCedula(cedulaid);
		} catch (Exception e) {
			System.out.println("Error al obtener informacion");
		}
        float subtotal = pedidoCabeceraFacade.calcularSubtotal(pedido.getPedidosDetale());
        float total = pedidoCabeceraFacade.calcularTotal(subtotal, pedido.getIva());
        pedido.setFecha(new Date());
        pedido.setTotal(total);
        pedido.setSubtotal(subtotal);
        pedido.setEstado("Receptado");

        pedidoCabeceraFacade.edit(pedido);
        pedido = pedidoCabeceraFacade.find(Integer.parseInt(pedidoID));
        
        
        if (pedido.getEstado().equals("Receptado")) {
        	FacturaCabecera facturaCabecera = new FacturaCabecera();
            facturaCabecera = new FacturaCabecera(0, new Date(), (float)0.0, (float)0.0, (float)14.0, 'N', pedido.getPersona());
            FacturaDetalle detalleFactura;
            List<PedidoDetalle> detallesPedido = pedido.getPedidosDetale();
            
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
		}else {
			return Response.ok("No se logro facturar el pedido" )
	                .header("Access-Control-Allow-Origins", "*")
	                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
	                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
	                .build();
		}
        
        

    }

    /*

    //SCORPION CODE ENDS

    @POST
    @Path("/create")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createPedido(@FormParam("personaId") String id, @FormParam("productos") String productos, @FormParam("cantidades") String cantidades) throws Exception{
        GregorianCalendar currentDate = getCurrentDate();
        Persona persona = personaFacade.find(id);
        Pedido pedido = new Pedido("ENVIADO", currentDate, persona, null);
        pedidoFacade.create(pedido);
        pedido = pedidoFacade.getUltimoPedido(persona, currentDate);

        String[] productosArray = productos.split(";");
        String[] cantidadesArray = cantidades.split(";");

        FacturaCabecera facturaCabecera = new FacturaCabecera(currentDate, 'N', 0, 0, 0, 0, null, persona, pedido);
        facturaCabeceraFacade.create(facturaCabecera);

        List<FacturaDetalle> detalleList = new ArrayList<>();

        for (int i = 0; i < productosArray.length; i++) {
            Producto producto = productoFacade.find(Integer.parseInt(productosArray[i]));
            producto.setStock(producto.getStock() - Integer.parseInt(cantidadesArray[i]));
            FacturaDetalle facturaDetalle = new FacturaDetalle(Integer.parseInt(cantidadesArray[i]),
                    (Integer.parseInt(cantidadesArray[i])*producto.getPrecioVenta()), facturaCabecera, producto);
            detalleList.add(facturaDetalle);
        }
        double [] totalSubtotalIva = getTotalSubtotalIva(detalleList);
        facturaCabecera.setListaFacturasDetalles(detalleList);
        facturaCabecera.setTotal(totalSubtotalIva[0]);
        facturaCabecera.setSubtotal(totalSubtotalIva[1]);
        facturaCabecera.setIva_total(totalSubtotalIva[2]);

        pedido.setFacturaCabecera(facturaCabecera);
        pedidoFacade.edit(pedido);

        return Response.ok("OK!" + id + " <--> " + productos)
                .header("Access-Control-Allow-Origins", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .build();
    }

    private double subtotal, iva_total;
    private double[] getTotalSubtotalIva(List<FacturaDetalle> detalleList){
        subtotal = 0;
        iva_total = 0;
        detalleList.forEach(facturaDetalle -> {
            Producto producto = facturaDetalle.getProducto();
            double precioVenta = producto.getPrecioVenta()*facturaDetalle.getCantidad();
            subtotal += (producto.getIva() == 'S') ? (precioVenta-precioVenta*0.12) : precioVenta;
            iva_total += (producto.getIva() == 'S') ? precioVenta*0.12 : 0;
        });
        return new double[]{subtotal+iva_total, subtotal, iva_total};
    }

    private GregorianCalendar getCurrentDate() throws Exception{
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = dateFormat.parse(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }

    @POST
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)

    public Response getPedidos(@FormParam("persona_id") String personaId) {

        Jsonb jsonb = JsonbBuilder.create();
        Persona persona = personaFacade.find(personaId);
        List<Pedido> pedidoList = pedidoFacade.findByPedidosId(persona);

        try {
            List<Pedido> pedidos = Pedido.serializePedidos(pedidoList);
            return Response.ok(jsonb.toJson(pedidos))
                    .header("Access-Control-Allow-Origins", "*")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error al obtener las bodegas ->" + e.getMessage()).build();
        }
    }
    */
}
