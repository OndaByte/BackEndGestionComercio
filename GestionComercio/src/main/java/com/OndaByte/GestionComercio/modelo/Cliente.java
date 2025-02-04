package com.OndaByte.GestionComercio.modelo;

public class Cliente extends ObjetoBD{
    private String nombre;
    private String telefono;
    private String direccion;
    private String dni;
    private String cuit_cuil;

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

    public void copiar(Cliente nuevo){
        this.nombre = nuevo.getNombre();
        this.telefono = nuevo.getTelefono();
        this.direccion = nuevo.getDireccion();
        this.dni = nuevo.getDni();
        this.cuit_cuil = nuevo.getCuit_cuil();
    }
}
