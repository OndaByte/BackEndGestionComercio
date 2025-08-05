package com.OndaByte.GestionComercio.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignorá cualquier campo extra en el JSON
public class Orden extends ObjetoBD {
    private Integer pedido_id;
    private String descripcion;
    private String estado_orden;
    private String fecha_fin;
    private String tipo;
    private Integer turno_id;

    public Orden(){}

    public Orden(
        @JsonProperty("pedido_id") Integer pedido_id,
        @JsonProperty("descripcion") String descripcion,
        @JsonProperty("estado_orden") String estado_orden,
        @JsonProperty("fecha_fin") String fecha_fin,
        @JsonProperty("tipo") String tipo,
        @JsonProperty("turno_id") Integer turno_id) {
        this.pedido_id = pedido_id;
        this.descripcion = descripcion;
        this.estado_orden = estado_orden;
        this.fecha_fin = fecha_fin;
        this.tipo = tipo;
        this.turno_id = turno_id;
    }

    public Integer getTurno_id() {
        return turno_id;
    }

    public void setTurno_id(Integer turno_id) {
        this.turno_id = turno_id;
    }
    
    public String getFecha_fin() {
        return fecha_fin;
    }
    public void setFecha_fin(String fecha_fin) {
        this.fecha_fin = fecha_fin;
    }
    public Integer getPedido_id() {
        return pedido_id;
    }
    public void setPedido_id(Integer pedido_id) {
        this.pedido_id = pedido_id;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getEstado_orden() {
            return estado_orden;
    }
    public void setEstado_orden(String estado_orden) {
            this.estado_orden = estado_orden;
    }
    public String getTipo() {
            return tipo;
    }
    public void setTipo(String tipo) {
            this.tipo = tipo;
    }   
}
