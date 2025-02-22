package com.OndaByte.GestionComercio.modelo;

public class Orden extends ObjetoBD {
    private int pedido_id;
    private int turno_id;
    private String descripcion;
    private String precio_final;
    private String costo_total;
    private String estado_orden;
    private String fecha_fin;
    
	public String getFecha_fin() {
		return fecha_fin;
	}
	public void setFecha_fin(String fecha_fin) {
		this.fecha_fin = fecha_fin;
	}
	public int getPedido_id() {
		return pedido_id;
	}
	public void setPedido_id(int pedido_id) {
		this.pedido_id = pedido_id;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
    
	public int getTurno_id() {
		return turno_id;
	}
	public void setTurno_id(int turno_id) {
		this.turno_id = turno_id;
	}
	public String getPrecio_final() {
		return precio_final;
	}
	public void setPrecio_final(String precio_final) {
		this.precio_final = precio_final;
	}
	public String getCosto_total() {
		return costo_total;
	}
	public void setCosto_total(String costo_total) {
		this.costo_total = costo_total;
	}
	public String getEstado_orden() {
		return estado_orden;
	}
	public void setEstado_orden(String estado_orden) {
		this.estado_orden = estado_orden;
	}

    
    
}
