package com.OndaByte.GestionComercio.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignorá cualquier campo extra en el JSON
public class Presupuesto extends ObjetoBD{
    private Integer pedido_id;
    private String nombre;
    private String descripcion;
    private String estado_presupuesto;
    private Float total;

    public Presupuesto() {
    }

    public Presupuesto(        
        @JsonProperty("pedido_id") Integer pedido_id,
        @JsonProperty("nombre") String nombre,
        @JsonProperty("descripcion") String descripcion,
        @JsonProperty("estado_presupuesto") String estado_presupuesto,
        @JsonProperty("total") Float total
        ) {
        this.pedido_id=pedido_id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado_presupuesto = estado_presupuesto;
        this.total = total;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado_presupuesto() {
        return estado_presupuesto;
    }

    public void setEstado_presupuesto(String estado_presupuesto) {
        this.estado_presupuesto = estado_presupuesto;
    }
    public Integer getPedido_id() {
        return pedido_id;
    }

    public void setPedido_id(Integer pedido_id) {
        this.pedido_id = pedido_id;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }
    
}
