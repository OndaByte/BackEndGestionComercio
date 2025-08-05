package com.OndaByte.GestionComercio.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Movimiento extends ObjetoBD {

    private String tipo_mov;
    private String descripcion = null;
    private Integer cliente_id = null;
    private Integer sesion_caja_id;
    private Float total;

    public Movimiento(){
    }

    
    public Movimiento(
        @JsonProperty("cliente_id") Integer cliente_id,
        @JsonProperty("descripcion") String descripcion,
        @JsonProperty("tipo_mov") String tipo_mov,
        @JsonProperty("total") Float total) {
        this.cliente_id = cliente_id;
        this.descripcion = descripcion;
        this.tipo_mov = tipo_mov;
        this.total = total;
    }

    public String getTipo_mov() {
        return tipo_mov;
    }

    public void setTipo_mov(String tipo_mov) {
        this.tipo_mov = tipo_mov;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCliente_id() {
        return cliente_id;
    }

    public void setCliente_id(Integer cliente_id) {
        this.cliente_id = cliente_id;
    }

    public Integer getSesion_caja_id() {
        return sesion_caja_id;
    }

    public void setSesion_caja_id(Integer sesion_caja_id) {
        this.sesion_caja_id = sesion_caja_id;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }
}
