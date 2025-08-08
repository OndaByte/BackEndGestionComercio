package com.OndaByte.GestionComercio.modelo;

import com.fasterxml.jackson.annotation.*;

public class Usuario extends ObjetoBD {

    private String usuario;
    private String contra;
    private String nombre;
    private String telefono;
    private String email;

    public Usuario(){}
    
    @JsonCreator
    public Usuario(@JsonProperty("user") String usuario, @JsonProperty("pass") String contra){
        this.usuario=usuario;
        this.contra=contra;
    }
    
    public String getContra() {
        return contra;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String toString() {
        return "{"+super.toString()+" \"usuario\" : \""+usuario+", \"contra\" : \""+contra+"\"}";
    }

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}    
}
