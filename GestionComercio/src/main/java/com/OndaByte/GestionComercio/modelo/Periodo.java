package com.OndaByte.GestionComercio.modelo;

public class Periodo {

    private Integer id;
    private Float costo;
    private String periodo;
    private Integer gasto_id;
    private String estado;

    public Float getCosto() {
        return costo;
    }

    public void setCosto(Float costo) {
        this.costo = costo;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Integer getGasto_id() {
        return gasto_id;
    }

    public void setGasto_id(Integer gasto_id) {
        this.gasto_id = gasto_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
