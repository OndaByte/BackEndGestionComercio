package com.OndaByte.GestionComercio.modelo;

import com.fasterxml.jackson.annotation.*;

public class Usuario extends ObjetoBD {

    private String usuario;
    private String contra;

    public Usuario(){}
    
    @JsonCreator
    public Usuario(@JsonProperty("usuario") String usuario, @JsonProperty("contra") String contra){
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
}
