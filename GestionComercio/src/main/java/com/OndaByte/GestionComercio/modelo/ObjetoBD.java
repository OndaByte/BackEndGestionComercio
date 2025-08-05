package com.OndaByte.GestionComercio.modelo;

public abstract class ObjetoBD {
    private Integer id;
    private String creado= "";
    private String ultMod= "";
    private String estado="ACTIVO";

    public Integer getId() {
            return id;
    }
    public void setId(Integer id) {
            this.id = id;
    }
    public String getCreado() {
            return creado;
    }
    public void setCreado(String creado) {
            this.creado = creado;
    }
    public String getUltMod() {
            return ultMod;
    }
    public void setUltMod(String ultMod) {
            this.ultMod = ultMod;
    }
    public String getEstado() {
            return estado;
    }

    public void setEstado(String estado) {
            this.estado = estado;
    }

    public String toString(){
        return "\"id\" : "+id+", "+"\"creado\": \""+creado.toString()+"\", \"ultMod\" : \""+ultMod.toString()+"\", \"estado\" : \""+estado+"\",";
    }
}
