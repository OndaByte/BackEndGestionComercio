package com.OndaByte.GestionComercio.modelo;

public class ItemVenta extends ObjetoBD {
    private long cantidad;
    private long subtotal;
    private long producto_id;

    public long getCantidad() {
        return cantidad;
    }

    public void setCantidad(long cantidad) {
        this.cantidad = cantidad;
    }

    public long getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(long subtotal) {
        this.subtotal = subtotal;
    }

    public long getProducto_id() {
        return producto_id;
    }

    public void setProducto_id(long producto_id) {
        this.producto_id = producto_id;
    }

    private Venta venta;

    public long getVenta(){
        return this.venta.getId();
    }

    public void setVenta(Venta venta){
        this.venta=venta;
    }

}
