
package com.OndaByte.GestionComercio.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemRemito extends ObjetoBD{

    private Integer id;
    private Integer remito_id;
    private String descripcion;
    private Integer cantidad;
    private Float precio;

    public ItemRemito() {
        descripcion = "";
    }

    // Constructor completo
    /*
     * SIN USO
     */
    public ItemRemito(
        @JsonProperty("id") Integer id,
        @JsonProperty("remito_id") Integer remito_id,
        @JsonProperty("descripcion") String descripcion,
        @JsonProperty("cantidad") Integer cantidad,
        @JsonProperty("precio") Float precio) {
        this.id = id;
        this.remito_id = remito_id;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public Integer getRemito_id() {
        return remito_id;
    }

    public void setRemito_id(Integer remito_id) {
        this.remito_id = remito_id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }
}
