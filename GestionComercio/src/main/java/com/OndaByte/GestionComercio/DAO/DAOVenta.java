package com.OndaByte.GestionComercio.DAO;

import org.sql2o.Connection;

import com.OndaByte.GestionComercio.modelo.ItemVenta;
import com.OndaByte.GestionComercio.modelo.Venta;
import com.OndaByte.GestionComercio.util.Log;

public class DAOVenta extends ABMDAO<Venta> {
	public DAOVenta(Connection con) {
		super(con);
		//TODO Auto-generated constructor stub
	}

	private String clave = "id";

	public Class<Venta> getClase() {
		return Venta.class;
	}

	public String getClave() {
		return this.clave;
	}
	/*
	public void altaProductosVenta(Venta venta){
		this.alta(venta);
		DAOItemVenta aux = new DAOItemVenta(this.con);
		DAOProducto aux2 = new DAOProducto(this.con);
		for (ItemVenta p : venta.getItems()) {
			p.setVenta(venta);
			aux.alta(t);
			this.actualizarStock(p.getProducto_id(),-p.getCantidad());
		}
		}*/
}
