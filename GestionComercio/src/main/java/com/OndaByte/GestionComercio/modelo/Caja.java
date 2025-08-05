package com.OndaByte.GestionComercio.modelo;

public class Caja extends ObjetoBD{
    private String estado_caja = "CERRADA";
    private String nombre;
    private Integer sesion_actual = null;

    public Caja(){}

	public String getEstado_caja() {
		return estado_caja;
	}

	public void setEstado_caja(String estado_caja) {
		this.estado_caja = estado_caja;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Integer getSesion_actual() {
		return sesion_actual;
	}

	public void setSesion_actual(Integer sesion_actual) {
		this.sesion_actual = sesion_actual;
	}
}
