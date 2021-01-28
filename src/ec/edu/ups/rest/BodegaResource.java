package ec.edu.ups.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ec.edu.ups.EJB.BodegaFacade;
import ec.edu.ups.entidades.Bodega;

@Path("/bodega/")
public class BodegaResource {

	@EJB
    BodegaFacade bodegaFacade;

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBodegas(){
        try{
            Jsonb jsonb = JsonbBuilder.create();
            List<Bodega> bodegaList = bodegaFacade.findAll();

            bodegaList = Bodega.serializeBodegas(bodegaList);
            bodegaList.forEach(System.out::println);

            return Response.ok(jsonb.toJson(bodegaList))
                    .header("Access-Control-Allow-Origins", "*")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                    .build();
        }catch (Exception e){
            return Response.status(Response.Status.BAD_REQUEST).entity("Error al obtener las bodegas ->" + e.getMessage()).build();
        }
    }
}
