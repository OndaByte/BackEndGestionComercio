package com.OndaByte.GestionComercio.modelo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignorá cualquier campo extra en el JSON
public class Cliente extends ObjetoBD implements Serializable {
    private String nombre;
    private String email;
    private String telefono; 
    private String direccion;
    private String dni;
    private String cuit_cuil;
    private String localidad;
    private String codigo_postal;
    private String provincia;
    private String cond_iva;

    public Cliente() {
        // Constructor vacío necesario para la deserialización
    }

    @JsonCreator
    public Cliente(
        @JsonProperty("nombre") String nombre,
        @JsonProperty("email") String email,
        @JsonProperty("telefono") String telefono,
        @JsonProperty("direccion") String direccion,
        @JsonProperty("dni") String dni,
        @JsonProperty("cuit_cuil") String cuit_cuil,
        @JsonProperty("localidad") String localidad,
        @JsonProperty("codigo_postal") String codigo_postal,
        @JsonProperty("provincia") String provincia,
        @JsonProperty("cond_iva") String cond_iva) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.dni = dni;
        this.cuit_cuil = cuit_cuil;
        this.localidad = localidad;
        this.codigo_postal = codigo_postal;
        this.provincia = provincia;
        this.cond_iva = cond_iva;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        this.dni = dni;
    }
    public String getCuit_cuil() {
        return cuit_cuil;
    }
    public void setCuit_cuil(String cuit_cuil) {
        this.cuit_cuil = cuit_cuil;
    }
    public String getLocalidad() {
        return localidad;
    }
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
    public String getCodigo_postal() {
        return codigo_postal;
    }
    public void setCodigo_postal(String codigo_postal) {
        this.codigo_postal = codigo_postal;
    }
    public String getProvincia() {
        return provincia;
    }
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    public String getCond_iva() {
        return cond_iva;
    }
    public void setCond_iva(String cond_iva) {
        this.cond_iva = cond_iva;
    }
}
