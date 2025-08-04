
package com.OndaByte.GestionComercio.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignorá cualquier campo extra en el JSON
public class Turno extends ObjetoBD {

    private Integer patron_repeticion;
    private String fecha_inicio;
    private String fecha_fin_e;
    private Integer prioridad = 0;
    private String observaciones;
    private String tipo;
    private String estado_turno;

    public Turno() {
    }

    public Turno(
            @JsonProperty("tipo") String tipo,
            @JsonProperty("observaciones") String observaciones,
            @JsonProperty("patron_repeticion") Integer patron_repeticion,
            @JsonProperty("fecha_inicio") String fecha_inicio,
            @JsonProperty("fecha_fin_e") String fecha_fin_e,
            @JsonProperty("estado_turno") String estado_turno) {
        this.patron_repeticion = patron_repeticion;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin_e = fecha_fin_e;
        this.tipo = tipo;
        this.observaciones = observaciones;
        this.estado_turno = estado_turno;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public Integer getPatron_repeticion() {
        return patron_repeticion;
    }

    public void setPatron_repeticion(Integer patron_repeticion) {
        this.patron_repeticion = patron_repeticion;
    }

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado_turno() {
        return estado_turno;
    }

    public void setEstado_turno(String estado_turno) {
        this.estado_turno = estado_turno;
    }

    public String getFecha_fin_e() {
        return fecha_fin_e;
    }

    public void setFecha_fin_e(String fecha_fin_e) {
        this.fecha_fin_e = fecha_fin_e;
    }
}
