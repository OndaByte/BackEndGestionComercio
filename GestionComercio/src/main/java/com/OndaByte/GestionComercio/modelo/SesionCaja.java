package com.OndaByte.GestionComercio.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignorá cualquier campo extra en el JSON
public class SesionCaja extends ObjetoBD {
    private Integer id;

    @JsonProperty("monto_inicial")
    private Float monto_inicial;

    @JsonProperty("monto_final")
    private Float monto_final;

    @JsonProperty("apertura")
    private String apertura;

    @JsonProperty("cierre")
    private String cierre;

    @JsonProperty("cajero_id")
    private Integer cajero_id;

    @JsonProperty("caja_id")
    private Integer caja_id;

    public SesionCaja() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getMonto_inicial() {
        return monto_inicial;
    }

    public void setMonto_inicial(Float monto_inicial) {
        this.monto_inicial = monto_inicial;
    }

    public Float getMonto_final() {
        return monto_final;
    }

    public void setMonto_final(Float monto_final) {
        this.monto_final = monto_final;
    }

    public String getApertura() {
        return apertura;
    }

    public void setApertura(String apertura) {
        this.apertura = apertura;
    }

    public String getCierre() {
        return cierre;
    }

    public void setCierre(String cierre) {
        this.cierre = cierre;
    }

    public Integer getCajero_id() {
        return cajero_id;
    }

    public void setCajero_id(Integer cajero_id) {
        this.cajero_id = cajero_id;
    }

    public Integer getCaja_id() {
        return caja_id;
    }

    public void setCaja_id(Integer caja_id) {
        this.caja_id = caja_id;
    }
}