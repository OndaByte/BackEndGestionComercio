package com.OndaByte.GestionComercio.modelo;

import java.util.Date;

public class Caja extends ObjetoBD{
    private float dinero_inicial=0;
    private float dinero_total=0;
    private Date fecha_cierre;

    public Caja(float dinero_inicial){
    	this.dinero_inicial=dinero_inicial;
	    this.dinero_total=this.dinero_inicial;
    }
    public void sumarTotal(float x) throws Exception{
        if(this.dinero_total + x < 0)
            throw  new IllegalArgumentException("La caja no puede quedar con saldo negativo");
        this.dinero_total += x;
    }

    public void setDinero_total(float total){
        this.dinero_total=total;
    }
    public void setDinero_inicial(float inicial){
        this.dinero_inicial = inicial;
    }
    public void setFecha_cierre(Date fecha){
        this.fecha_cierre=fecha;
    }
    public float getDinero_total(){
        return this.dinero_total;
    }
    public float getDinero_inicial(){
        return this.dinero_inicial;
    }
    public Date getFecha_cierre(){
        return this.fecha_cierre;
    }

    @Override
    public String toString() {
        return "{"+super.toString()+", \"dinero_inicial\" : "+dinero_inicial+", \"dinero_total\" : "+dinero_total+", \"fecha_cierre=\" :"+fecha_cierre+'}';
    }
}

