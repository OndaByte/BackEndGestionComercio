package com.OndaByte.GestionComercio.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignorá cualquier campo extra en el JSON
public class Producto extends ObjetoBD { 
    private Integer id;
    @JsonProperty("categoria_id")
    private Integer categoria_id;
    
    @JsonProperty("nombre")
    private String nombre;
    @JsonProperty("codigo_barra")
    private String codigo_barra;
    @JsonProperty("descripcion")
    private String descripcion;
    @JsonProperty("precio_costo")
    private Float precio_costo;
    @JsonProperty("porcentaje_ganancia")
    private Integer porcentaje_ganancia;
    @JsonProperty("porcentaje_descuento")
    private Integer porcentaje_descuento;
    @JsonProperty("stock")
    private Integer stock;

    public Producto() {
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

    public String getCodigo_barra() {
        return codigo_barra;
    }

    public void setCodigo_barra(String codigo_barra) {
        this.codigo_barra = codigo_barra;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Float getPrecio_costo() {
        return precio_costo;
    }

    public void setPrecio_costo(Float precio_costo) {
        this.precio_costo = precio_costo;
    }

    public Integer getPorcentaje_ganancia() {
        return porcentaje_ganancia;
    }

    public void setPorcentaje_ganancia(Integer porcentaje_ganancia) {
        this.porcentaje_ganancia = porcentaje_ganancia;
    }

    public Integer getPorcentaje_descuento() {
        return porcentaje_descuento;
    }

    public void setPorcentaje_descuento(Integer porcentaje_descuento) {
        this.porcentaje_descuento = porcentaje_descuento;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getCategoria_id() {
        return categoria_id;
    }

    public void setCategoria_id(Integer categoria_id) {
        this.categoria_id = categoria_id;
    }


}
