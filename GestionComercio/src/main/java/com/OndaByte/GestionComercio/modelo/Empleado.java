package com.OndaByte.GestionComercio.modelo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignorá cualquier campo extra en el JSON
public class Empleado extends ObjetoBD {

    private String dni;
    private String nombre;
    private String telefono;
    private String direccion;

    public Empleado() {
        // Constructor vacío necesario para la deserialización
    }

    @JsonCreator
    public Empleado(
            @JsonProperty("dni") String dni,
            @JsonProperty("nombre") String nombre,
            @JsonProperty("telefono") String telefono,
            @JsonProperty("direccion") String direccion){
        this.dni = dni;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        this.dni = dni;
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
}
