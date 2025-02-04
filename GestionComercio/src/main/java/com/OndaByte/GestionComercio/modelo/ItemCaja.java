package com.OndaByte.GestionComercio.modelo;

public abstract class ItemCaja extends ObjetoBD{
    public String tipo;
    private float total;

    public ItemCaja(String tipo) {
        this.tipo = tipo;
    }

    public void setTotal(float total){
        this.total=total;
    }
    public float getTotal(){
        return this.total;
    }
    public String toString() {
        return super.toString()+", \"total\" : "+total;
    }
}
