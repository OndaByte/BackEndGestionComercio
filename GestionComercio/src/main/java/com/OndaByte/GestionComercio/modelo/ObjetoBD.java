package com.OndaByte.GestionComercio.modelo;

import java.util.Date;

/**
 * ObjetoBD generico, todos los objetos de cualquier BD estara en la tabla ObjetoBD
 * Objetos especifico heredaran de ObjetoBD y tendran sus tablas propias que referenciaran a la tabla ObjetoBD
 * @author Fran
 */
public abstract class ObjetoBD {
    private int id;
    private String creado= "";
    private String ultMod= "";
    private String estado="ACTIVO";

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCreado() {
		return creado;
	}
	public void setCreado(String creado) {
		this.creado = creado;
	}
	public String getUltMod() {
		return ultMod;
	}
	public void setUltMod(String ultMod) {
		this.ultMod = ultMod;
	}
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

    public String toString(){
        return "\"id\" : "+id+", "+"\"creado\": \""+creado.toString()+"\", \"ultMod\" : \""+ultMod.toString()+"\", \"estado\" : \""+estado+"\",";
    }
}
