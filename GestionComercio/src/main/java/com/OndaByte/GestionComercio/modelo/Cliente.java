package com.OndaByte.GestionComercio.modelo;

public class Cliente extends ObjetoBD{
    private String nombre;
    private String telefono;
    private String direccion;
    private String dni;
    private String cuit_cuil;
    private String localidad;
    private String codigo_postal;
	private String provincia;
    private String cond_iva;
    private String cond_venta;

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
	public int getDni() {
		return dni;
	}
	public void setDni(int dni) {
		this.dni = dni;
	}
	public int getCuit_cuil() {
		return cuit_cuil;
	}
	public void setCuit_cuil(int cuit_cuil) {
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
	public String getCond_venta() {
		return cond_venta;
	}
	public void setCond_venta(String cond_venta) {
		this.cond_venta = cond_venta;
	}
}
