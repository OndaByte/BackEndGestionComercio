
package com.OndaByte.GestionComercio.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignorá cualquier campo extra en el JSON
public class ItemPresupuesto extends ObjetoBD{
    private String descripcion;
    private Integer presupuesto_id;
    private Float precio;
    private Integer cantidad;
 
    public ItemPresupuesto() {
    }

    /*
     * SIN USO
     */
    public ItemPresupuesto(        
        @JsonProperty("descripcion") String descripcion,
        @JsonProperty("presupuesto_id") Integer presupuesto_id,
        @JsonProperty("precio") Float precio,
        @JsonProperty("canidad") Integer canidad) {
        this.descripcion = descripcion;
        this.presupuesto_id = presupuesto_id;
        this.precio = precio;
        this.cantidad = canidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getPresupuesto_id() {
        return presupuesto_id;
    }

    public void setPresupuesto_id(Integer presupuesto_id) {
        this.presupuesto_id = presupuesto_id;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
