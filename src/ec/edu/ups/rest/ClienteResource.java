package ec.edu.ups.rest;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ec.edu.ups.EJB.PersonaFacade;
import ec.edu.ups.entidades.Persona;

@Path("/cliente")
public class ClienteResource {
	
	@EJB
    private PersonaFacade ejbPersona;
    
    public ClienteResource() {

    }


    
    @POST
    @Path("/loginCliente")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response loginCliente(@FormParam("usuario") String usuario, @FormParam("password") String password) {	
    	
    	System.out.println("Usuario: "+usuario+ " password: "+password);
    	
    	Persona persona = ejbPersona.inicioSesion(usuario, password);
    	if(persona==null) {
    		return Response.ok("Persona no encotrada").build();
    	}else {
    		
    		if (persona.getRol() == 'C' && persona.getEstado() == "Activo" ) {
    			return Response.ok("Welcome: "+persona.getNombre()+"!!").build();
			}else {
				
				return Response.ok("Cliente no encontrado").build();
			}
    	}    	
    }
    
    
    @POST
    @Path("/crearCuenta")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response crearCuenta(@FormParam("correo") String correo, @FormParam("password") String password, @FormParam("cedula") String cedula, @FormParam("nombre") String nombre, @FormParam("apellido") String apellido, @FormParam("direccion") String direccion,@FormParam("telefono") String telefono) {		
    	
    	Persona per = ejbPersona.buscarPorCedula(cedula);
    	
    	if (per != null) {
    		
        	try {
        		//persona = new Persona(nombre, apellido, cedula, direccion, telefono, correo, password, 'C', "Activo");
        		
        		per.setCorreo(correo);
        		per.setTelefono(telefono);
        		per.setPassword(password);
        		per.setNombre(nombre);
        		per.setApellido(apellido);
        		per.setDireccion(direccion);
        		per.setEstado("Activo");
        		ejbPersona.edit(per);
        		return Response.ok("Usuario Creado, gracias por unise a nosotros: "+per.getNombre()+" "+per.getApellido()).build();
        	}catch (Exception e) {
    			return Response.ok("Usuario no creado").build();
    		}
		}else {
			return Response.ok("El usuario han no a sido registrado en la base de datos").build();
			
		}
    	
    	
    	
    }
    
    
    @POST
    @Path("/eliminarCuenta")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response eliminarCuenta(@FormParam("cedula") String cedula) {
    	Persona persona = ejbPersona.buscarPorCedula(cedula);
    	if(persona==null) {
    		return Response.ok("Cliente no encontrado!").build();
    	}else {
    		persona.setEstado("Inactivo");
    		ejbPersona.edit(persona);
    		return Response.ok("Se elimino su cuenta exitosamente").build();
    	}
    	
    }
    
    
    @POST
    @Path("/modificarCuenta")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response modificarCuenta(@FormParam("correo") String correo, @FormParam("password") String password, @FormParam("cedula") String cedula, @FormParam("nombre") String nombre, @FormParam("apellido") String apellido, @FormParam("direccion") String direccion,@FormParam("telefono") String telefono){
    	Persona persona=ejbPersona.buscarPorCedula(cedula);
    	if(persona==null) {
    		return Response.ok("Cliente no encontrada!").build();
    	}else {
    		persona.setCorreo(correo);
    		persona.setPassword(password);
       		persona.setNombre(nombre);
    		persona.setApellido(apellido);    		
    		persona.setDireccion(direccion);
    		persona.setTelefono(telefono);
    		ejbPersona.edit(persona);
    		return Response.ok("Sus datos han sido Modificados!").build();
    	}
    	
    }

    
    
    

}