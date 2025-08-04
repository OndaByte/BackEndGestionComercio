package com.OndaByte.GestionComercio.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignorá cualquier campo extra en el JSON
public class Pedido extends ObjetoBD{
    private Integer cliente_id;
    private String descripcion;
    private String fecha_fin_estimada;
    private String estado_pedido;
    private Integer presupuesto_id;
    private Integer turno_id;

    public Pedido() {
    }

    public Pedido(
        @JsonProperty("cliente_id") Integer cliente_id,
        @JsonProperty("descripcion") String descripcion,
        @JsonProperty("fecha_fin_estimada") String fecha_fin_estimada,
        @JsonProperty("estado_pedido") String estado_pedido,
        @JsonProperty("presupuesto_id") Integer presupuesto_id,
        @JsonProperty("turno_id") Integer turno_id) {
        this.cliente_id = cliente_id;
        this.descripcion = descripcion;
        this.fecha_fin_estimada = fecha_fin_estimada;
        this.estado_pedido = estado_pedido;
        this.turno_id = turno_id;
        this.presupuesto_id = presupuesto_id;
    }
       
    public Integer getTurno_id() {
        return turno_id;
    }

    public void setTurno_id(Integer turno_id) {
        this.turno_id = turno_id;
    }  

    public Integer getPresupuesto_id() {
        return presupuesto_id;
    }

    public void setPresupuesto_id(Integer presupuesto_id) {
        this.presupuesto_id = presupuesto_id;
    }

    public String getDescripcion() {
            return descripcion;
    }
    public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
    }
    public String getFecha_fin_estimada() {
            return fecha_fin_estimada;
    }
    public void setFecha_fin_estimada(String fecha_fin) {
            this.fecha_fin_estimada = fecha_fin;
    }
    public Integer getCliente_id() {
            return cliente_id;
    }
    public void setCliente_id(Integer cliente_id) {
            this.cliente_id = cliente_id;
    }
    public String getEstado_pedido() {
        return estado_pedido;
    }
    public void setEstado_pedido(String estado_pedido) {
        this.estado_pedido = estado_pedido;
    }
}
