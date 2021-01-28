package ec.edu.ups.controller;

import java.io.Serializable;

import javax.faces.context.FacesContext;

public class LoguinBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	
	public void login() {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("accesos", 1);
	}
	
	public void logout() {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("accesos", null);
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
	}
}