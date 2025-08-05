
package com.OndaByte.GestionComercio.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Remito extends ObjetoBD {

    private String fecha_emision;
    private String fecha_pago;
    private Integer nro_remito;
    private String punto_venta;
    private Float total;
    private Integer orden_id;
    private Integer cliente_id;
    private Integer movimiento_id;
    private String cliente_cuit_cuil;
    private String cliente_nombre;
    private String cliente_domicilio;
    private String cliente_localidad;
    private String cliente_telefono;
    private String observaciones;


    public Remito() {}

    public Remito(
        @JsonProperty("fecha_emision") String fecha_emision,
        @JsonProperty("fecha_pago") String fecha_pago,
        @JsonProperty("nro_remito") Integer nro_remito,
        @JsonProperty("punto_venta") String punto_venta,
        @JsonProperty("total") Float total,
        @JsonProperty("orden_id") Integer orden_id,
        @JsonProperty("cliente_id") Integer cliente_id,
        @JsonProperty("movimiento_id") Integer movimiento_id,
        @JsonProperty("observaciones") String observaciones,
        @JsonProperty("cliente_cuit_cuil") String cliente_cuit_cuil,
        @JsonProperty("cliente_nombre") String cliente_nombre,
        @JsonProperty("cliente_domicilio") String cliente_domicilio,
        @JsonProperty("cliente_localidad") String cliente_localidad,
        @JsonProperty("cliente_telefono") String cliente_telefono
//        @JsonProperty("items") List<ItemRemito> items
    ) {
        this.fecha_emision = fecha_emision;
        this.fecha_pago = fecha_pago;
        this.nro_remito = nro_remito;
        this.punto_venta = punto_venta;
        this.total = total;
        this.orden_id = orden_id;
        this.cliente_id = cliente_id;
        this.movimiento_id = movimiento_id;
        this.observaciones = observaciones;
        this.cliente_cuit_cuil = cliente_cuit_cuil;
        this.cliente_nombre = cliente_nombre;
        this.cliente_domicilio =cliente_domicilio;
        this.cliente_localidad = cliente_localidad;
        this.cliente_telefono = cliente_telefono;
//        this.items = items;
    }

    public String getFecha_emision() {
        return fecha_emision;
    }

    public void setFecha_emision(String fecha_emision) {
        this.fecha_emision = fecha_emision;
    }

    public Integer getNro_remito() {
        return nro_remito;
    }

    public void setNro_remito(Integer nro_remito) {
        this.nro_remito = nro_remito;
    }

    public String getPunto_venta() {
        return punto_venta;
    }

    public void setPunto_venta(String punto_venta) {
        this.punto_venta = punto_venta;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Integer getOrden_id() {
        return orden_id;
    }

    public void setOrden_id(Integer orden_id) {
        this.orden_id = orden_id;
    }

    public Integer getCliente_id() {
        return cliente_id;
    }

    public void setCliente_id(Integer cliente_id) {
        this.cliente_id = cliente_id;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getCliente_telefono() {
        return cliente_telefono;
    }

    public void setCliente_telefono(String cliente_telefono) {
        this.cliente_telefono = cliente_telefono;
    }

    public String getCliente_localidad() {
        return cliente_localidad;
    }

    public void setCliente_localidad(String cliente_localidad) {
        this.cliente_localidad = cliente_localidad;
    }

    public String getCliente_domicilio() {
        return cliente_domicilio;
    }

    public void setCliente_domicilio(String cliente_domicilio) {
        this.cliente_domicilio = cliente_domicilio;
    }

    public String getCliente_nombre() {
        return cliente_nombre;
    }

    public void setCliente_nombre(String cliente_nombre) {
        this.cliente_nombre = cliente_nombre;
    }

    public String getCliente_cuit_cuil() {
        return cliente_cuit_cuil;
    }

    public void setCliente_cuit_cuil(String cliente_cuit_cuil) {
        this.cliente_cuit_cuil = cliente_cuit_cuil;
    }

    public String getFecha_pago() {
        return fecha_pago;
    }

    public void setFecha_pago(String fecha_pago) {
        this.fecha_pago = fecha_pago;
    }

	public Integer getMovimiento_id() {
		return movimiento_id;
	}

	public void setMovimiento_id(Integer movimiento_id) {
		this.movimiento_id = movimiento_id;
	}
}
