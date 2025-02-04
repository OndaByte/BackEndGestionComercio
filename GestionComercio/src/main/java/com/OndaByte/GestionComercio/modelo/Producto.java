package com.OndaByte.GestionComercio.modelo;

public class Producto extends ObjetoBD {
    private String nombre;
    private float precio_costo;
    private float precio_venta;
    private long stock_actual;
    private long stock_frizado;
    private String ingredientes_receta;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getPrecio_costo() {
        return precio_costo;
    }

    public void setPrecio_costo(float precio_costo) {
        this.precio_costo = precio_costo;
    }

    public float getPrecio_venta() {
        return precio_venta;
    }

    public void setPrecio_venta(float precio_venta) {
        this.precio_venta = precio_venta;
    }

    public long getStock_actual() {
        return stock_actual;
    }

    public void setStock_actual(long stock_actual) {
        this.stock_actual = stock_actual;
    }

    public String getIngredientes_receta() {
        return ingredientes_receta;
    }

    public void setIngredientes_receta(String ingredientes_receta) {
        this.ingredientes_receta = ingredientes_receta;
    }


    public void copiar(Producto nuevo){
        this.ingredientes_receta = nuevo.getIngredientes_receta();
        this.nombre = nuevo.getNombre();
        this.precio_costo = nuevo.getPrecio_costo();
        this.precio_venta = nuevo.getPrecio_venta();
        this.stock_actual = nuevo.getStock_actual(); 
    }

    public void sumarStock(long cant) throws Exception {
        if(this.stock_actual+cant < 0){
            throw new Exception("Al actualizar stock de producto, el resultado no puede ser negativo.\n");
        }
        this.stock_actual+=cant;
    }

	public long getStock_frizado() {
		return stock_frizado;
	}

	public void setStock_frizado(long stock_frizado) {
		this.stock_frizado = stock_frizado;
	}

    public String toString(){
        return "{"+super.toString()
            +" \"nombre\" : \""+nombre+"\","
            +" \"precio_costo\" : "+precio_costo+","
            +" \"precio_venta\" : "+precio_venta+","
            +" \"stock_actual\" : "+stock_actual+","
            +" \"stock_frizado\" : "+stock_frizado+","
            +" \"ingredientes_receta\" : \""+ingredientes_receta+"\"}";
    }
}
