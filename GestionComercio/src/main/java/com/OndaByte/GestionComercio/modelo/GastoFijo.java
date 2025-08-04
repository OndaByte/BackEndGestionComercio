package com.OndaByte.GestionComercio.modelo;

public class GastoFijo extends ObjetoBD {

    private String nombre;
    private String inicio;
    private String ult_pausa;
    private Integer repeticion;

    public GastoFijo() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getRepeticion() {
        return repeticion;
    }

    public void setRepeticion(Integer repeticion) {
        this.repeticion = repeticion;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getUlt_pausa() {
        return ult_pausa;
    }

    public void setUlt_pausa(String ult_pausa) {
        this.ult_pausa = ult_pausa;
    }
}
