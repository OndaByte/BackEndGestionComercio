
package com.OndaByte.GestionComercio.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignorá cualquier campo extra en el JSON
public class Categoria extends ObjetoBD {
    private Integer id;
    @JsonProperty("nombre")
    private String nombre;    
    @JsonProperty("porcentaje_descuento")
    private Integer porcentaje_descuento;    
    @JsonProperty("tipo")
    private String tipo;
    @JsonProperty("padre_id")
    private String padre_id;

    public Categoria() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getPorcentaje_descuento() {
        return porcentaje_descuento;
    }

    public void setPorcentaje_descuento(Integer porcentaje_descuento) {
        this.porcentaje_descuento = porcentaje_descuento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPadre_id() {
        return padre_id;
    }

    public void setPadre_id(String padre_id) {
        this.padre_id = padre_id;
    }
}
