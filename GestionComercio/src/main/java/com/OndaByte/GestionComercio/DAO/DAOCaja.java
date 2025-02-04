package com.OndaByte.GestionComercio.DAO;

import com.OndaByte.GestionComercio.modelo.Caja;

import com.OndaByte.GestionComercio.modelo.ItemCaja;

import java.util.Date;
import java.util.List;

import com.OndaByte.GestionComercio.util.Log;

import org.sql2o.Connection;

public class DAOCaja extends ABMDAO<Caja> {

	public DAOCaja(Connection con) {
		super(con);
	}


	private String clave = "id";
	
	public Class<Caja> getClase() {
		return Caja.class;
	}

	public String getClave() {
		return this.clave;
	}

    public Caja getCaja(){		
        String query = "From Cajas C WHERE C.state = 'ACTIVO' AND C.fecha_cierre is null";
		try(Connection con = DAOSql2o.getSql2o().open()){
			List<Caja> aux = con.createQuery(query).executeAndFetch(this.getClase());
			if (aux.isEmpty()) {
				return null;
			}
			return aux.get(0);
		}
		catch (Exception e){
			Log.log(e, DAOCaja.class);
		}
		return null;
    }

    public List<Caja> getCajas(Date fecha1, Date fecha2) {
		try(Connection con = DAOSql2o.getSql2o().open()){
			if ((fecha1 != null && fecha2 != null) && fecha1.after(fecha2)){
				throw new IllegalArgumentException("'from' no puede ser mayor a 'to'.\n");
			}

			String query = "From Cajas C WHERE C.state = 'ACTIVO' AND (:fecha1 IS null OR C.created_at >= :fecha1) AND (:fecha2 IS null OR C.created_at <= :fecha2)";
			List<Caja> aux = con.createQuery(query)
				.addParameter("fecha1",fecha1)
				.addParameter("fecha2",fecha2)
				.executeAndFetch(this.getClase());
			return aux;
		}
		catch (Exception e){
			Log.log(e, DAOCaja.class);
		}
		return null;
    }
	
    public void cerrarCaja() {
        Caja actual = this.getCaja();
        actual.setFecha_cierre(new Date());
		this.modificar(actual);
    }

    public List<ItemCaja> getItems(long id){
		try(Connection con = DAOSql2o.getSql2o().beginTransaction()){
			List<ItemCaja> resul;
			String query = "FROM Pedidos P WHERE P.caja.id=:id";
			resul = con.createQuery(query).addParameter("id",id).executeAndFetch(ItemCaja.class);
			query = "FROM Movimientos M WHERE M.caja.id=:id";
			resul.addAll(con.createQuery(query).addParameter("id",id).executeAndFetch(ItemCaja.class));
			query = "FROM Ventas V WHERE V.caja.id=:id";
			resul.addAll(con.createQuery(query).addParameter("id",id).executeAndFetch(ItemCaja.class));
			return resul;
		}
		catch (Exception e){
			Log.log(e, ABMDAO.class);
		}
		return null;	
    }

	public List<ItemCaja> getItems(Date fecha1, Date fecha2, Integer tipo, String pago){
        try(Connection con = DAOSql2o.getSql2o().beginTransaction()){
			List<ItemCaja> resul;
			String query = "From Pedidos P WHERE (:fecha1 IS null OR P.created_at >= :fecha1) AND (:fecha2 IS null OR P.created_at <= :fecha2)";
		
			resul = con.createQuery(query)
				.addParameter("fecha1",fecha1)
				.addParameter("fecha2",fecha2)
				.executeAndFetch(ItemCaja.class);
			query = "From Movimientos M WHERE (:fecha1 IS null OR M.created_at >= :fecha1) AND (:fecha2 IS null OR M.created_at <= :fecha2)";

			resul.addAll(con.createQuery(query)
						 .addParameter("fecha1",fecha1)
						 .addParameter("fecha2",fecha2)
						 .executeAndFetch(ItemCaja.class));
			query = "From Ventas V WHERE (:fecha1 IS null OR V.created_at >= :fecha1) AND (:fecha2 IS null OR V.created_at <= :fecha2) AND (:tipo IS null OR V.tipo=:tipo) AND (:pago IS null OR V.metodo_pago=:pago)";
			resul.addAll(con.createQuery(query)
						 .addParameter("fecha1",fecha1)
						 .addParameter("fecha2",fecha2)
						 .addParameter("tipo",tipo)
						 .addParameter("pago",pago)
						 .executeAndFetch(ItemCaja.class));
			return resul;
		}
		catch (Exception e){
			Log.log(e, ABMDAO.class);
		}
		return null;		       
	}

 
	public Caja ultimaCaja(){
		String query = "FROM Cajas C " +
			"WHERE C.fecha_cierre IS NOT null AND " +
			"C.fecha_cierre= (SELECT MAX(fecha_cierre) FROM Cajas)";

		try(Connection con = DAOSql2o.getSql2o().open()){
			List<Caja> aux = con.createQuery(query).executeAndFetch(this.getClase());
			if (aux.isEmpty()) {
				return null;
			}
			return aux.get(0);
		}
		catch (Exception e){
			Log.log(e, DAOCaja.class);
		}
		return null;
	}

	
	public void abrirUltima() {
		String query = "UPDATE Cajas C SET C.fecha_cierre = NULL WHERE C.id = :id";
		try(Connection con = DAOSql2o.getSql2o().open()){
			con.createQuery(query).addParameter("id",this.ultimaCaja().getId()).executeUpdate();
		}
		catch (Exception e){
			Log.log(e, DAOCaja.class);
		}
	}

}
