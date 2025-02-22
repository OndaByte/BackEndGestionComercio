package com.OndaByte.GestionComercio.modelo;

public class Pedido extends ObjetoBD{
    private int cliente_id;
    private String descripcion;
    private String fecha_fin;
    private String estado_pedido;

    
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getFecha_fin() {
		return fecha_fin;
	}
	public void setFecha_fin(String fecha_fin) {
		this.fecha_fin = fecha_fin;
	}
	public int getCliente_id() {
		return cliente_id;
	}
	public void setCliente_id(int cliente_id) {
		this.cliente_id = cliente_id;
	}
    public String getEstado_pedido() {
        return estado_pedido;
    }
    public void setEstado_pedido(String estado_pedido) {
        this.estado_pedido = estado_pedido;
    }
    
}
