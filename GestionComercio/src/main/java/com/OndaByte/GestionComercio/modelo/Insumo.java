package com.OndaByte.GestionComercio.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignorá cualquier campo extra en el JSON
public class Insumo extends ObjetoBD { 

    private Integer id;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("precio")
    private Float precio;

    @JsonProperty("stock")
    private Integer stock;

    public Insumo() {
        // Constructor vacío obligatorio para que Jackson pueda instanciarlo
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
