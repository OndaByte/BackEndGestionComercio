
package com.OndaByte.GestionComercio.modelo;

import java.util.List;

public class Venta extends ItemCaja {
    private Cliente cliente;
    private String metodo_pago = "";
    
    //Nos va a ayudar a filtrar zonas en modulo BI
    private String domicilio = "";
    // 0 = P/LLEVAR 1 = DELIVERY 2 = SALON
    private Integer tipo = 0;
    //Con cuanto paga
    private Float pago = 0f;
    private Caja caja;
    
    private List<ItemVenta> items;

    public Venta(String tipo) {
        super(tipo);
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setPago(float pago) {
        this.pago = pago;
    }

	public String getMetodo_pago() {
		return metodo_pago;
	}

    public String getDomicilio() {
        return domicilio;
    }

    public Float getPago() {
        return pago;
    } 

    public String getTipo() {
         if(this.tipo==0)
            return "DELIVERY";
         if(this.tipo==1)
            return "P/llevar";
        if(this.tipo==2)
            return "LOCAL";
        return "SALÓN";
    }
    public void setMetodo_pago(String metodo_pago) {
        this.metodo_pago = metodo_pago;
    }

    public void setCaja(Caja caja) {
        this.caja = caja;
    }

    public List<ItemVenta> getItems() {
        return items;
    }

    public long getCaja() {
        return caja.getId();
    }
    public void setitems(List<ItemVenta> items){
        for (ItemVenta p: items) {
            p.setVenta(this);
        }
        this.items=items;
    }
    
}
