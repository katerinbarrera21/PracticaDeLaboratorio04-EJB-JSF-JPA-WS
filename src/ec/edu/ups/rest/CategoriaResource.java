package ec.edu.ups.rest;


import javax.ejb.EJB;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ec.edu.ups.EJB.BodegaFacade;
import ec.edu.ups.EJB.ProductoFacade;
import ec.edu.ups.entidades.Bodega;
import ec.edu.ups.entidades.Categoria;
import ec.edu.ups.entidades.Producto;

import java.util.List;

@Path("/categoria/")
public class CategoriaResource {

    @EJB
    ProductoFacade productoFacade;
    @EJB
    BodegaFacade ejbBodega;
    
    @POST
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
   // @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getCategorias(@FormParam("bodega_id") Integer bodegaId){
        Jsonb jsonb = JsonbBuilder.create();
        try {
        	Bodega bod = ejbBodega.find(bodegaId);
        	List<Producto> productos= bod.getProductos();
        	List<Categoria> categorias = productoFacade.getCategorias(productos);

            if(!categorias.isEmpty())
                return Response.status(Response.Status.ACCEPTED).entity(jsonb.toJson(categorias))
                        .header("Access-Control-Allow-Origins", "*")
                        .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                        .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                        .build();
            else
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se ha encontrado ninguna categoria!").build();
        }catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("ERROR AL OBTENER LAS CATEGORIAS!").build();
        }
    }

}












