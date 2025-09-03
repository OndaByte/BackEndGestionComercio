/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.OndaByte.GestionComercio.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 *
 * @author luciano
 */
@JsonIgnoreProperties(ignoreUnknown = true) // Ignorá cualquier campo extra en el JSON
public class ItemVenta extends ObjetoBD {
    
    private Integer id;
    @JsonProperty("venta_id")
    private Integer venta_id;
    @JsonProperty("producto_id")
    private Integer producto_id;
    @JsonProperty("producto_precio")
    private Float producto_precio;
            
    @JsonProperty("nombre")
    private String nombre;
    @JsonProperty("cantidad")
    private Integer cantidad;
    @JsonProperty("porcentaje_descuento")
    private Integer porcentaje_descuento;
    @JsonProperty("subtotal")
    private Float subtotal;

    public ItemVenta() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVenta_id() {
        return venta_id;
    }

    public void setVenta_id(Integer venta_id) {
        this.venta_id = venta_id;
    }

    public Integer getProducto_id() {
        return producto_id;
    }

    public void setProducto_id(Integer producto_id) {
        this.producto_id = producto_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getPorcentaje_descuento() {
        return porcentaje_descuento;
    }

    public void setPorcentaje_descuento(Integer porcentaje_descuento) {
        this.porcentaje_descuento = porcentaje_descuento;
    }

    public Float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Float subtotal) {
        this.subtotal = subtotal;
    }
    
    public Float getProducto_precio() {
        return producto_precio;
    }

    public void setProducto_precio(Float producto_precio) {
        this.producto_precio = producto_precio;
    }

}
