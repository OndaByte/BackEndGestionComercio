package com.OndaByte.GestionComercio.control;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.OndaByte.GestionComercio.DAO.DAOCaja;
import com.OndaByte.GestionComercio.modelo.Caja;
import com.OndaByte.GestionComercio.modelo.ItemCaja;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.http.Context;


public class CajaControl {
/*
    private static ObjectMapper objectMapper = new ObjectMapper();
	private static String dateString = "2025-01-09 21:57:39";
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	//AbrirCaja
	public static void abrirCaja(Context ctx) {
		try{
			Caja n = objectMapper.readValue(ctx.body(), Caja.class);
			DAOCaja dao = new DAOCaja();
			float dinero_inicial = n != null ? n.getDinero_inicial() : 0;
			Caja cajaAbierta = getCaja(ctx);
			if(cajaAbierta == null){
				Caja ultFechaCierre = dao.ultimaCaja();
				if(ultFechaCierre != null && compararFechas(ultFechaCierre.getFecha_cierre(),new Date()) && compararFechas(formatter.parse(ultFechaCierre.getCreado()),new Date()) ){ //Es de hoy
					dao.abrirUltima();
					ctx.status(208).result("Existia una caja cerrada del dia, fue abierta con exito.\n");
				}
				else{
					dao.alta(new Caja(dinero_inicial));
					ctx.status(201).result("Caja abierta con exito.\n");
				}
			}
			else{
				ctx.status(409).result("Error: Ya hay una caja abierta del dia.\n");
			}			 
		}catch(Exception e){
			Log.log(e,CajaControl.class);
		}
	}
	
	//Caja
    public static Caja getCaja(Context ctx){ //DEVUELVE LA CAJA ABIERTA, SI NO HAY CAJAS ABIERTAS O LA CAJA ABIERTA ES DE OTRO DIA, CIERRA LA CAJA Y DEVUELVE NULL
		try{
			DAOCaja dao = new DAOCaja();
			Caja cajaAbierta = dao.getCaja();
			if(cajaAbierta == null){
				ctx.status(404).result("No hay caja abierta");
				return null;
			}
			else if(!compararFechas(new Date(0),formatter.parse(cajaAbierta.getCreado()))) {
				dao.cerrarCaja();
				ctx.status(404).result("No hay caja abierta");
				return null;
			}
			ctx.status(200).json(cajaAbierta);
			return cajaAbierta;
		}catch(Exception e){
			Log.log(e,CajaControl.class);
		}
		return null;
    }

	//CerrarCaja
    public static void cerrarCaja(Context ctx){
        try{
			DAOCaja dao = new DAOCaja();
            dao.cerrarCaja();
            ctx.status(208).result("Caja cerrada con exito.\n");
        } catch (Exception e){
			ctx.status(404).result("Error interno");
        }
    }

	//Cajas	
    public static void getCajas(Context ctx) {
		String fromParam = ctx.queryParam("from");
		String toParam = ctx.queryParam("to");
		Date from = null;
		Date to = null;
		try {
			if (fromParam != null) {
				from = formatter.parse(fromParam);
				from = diaAnterior(from);
			}
			if (toParam != null) {
				to = formatter.parse(toParam);
				to = ultimaHora(to);
			}

            DAOCaja dao = new DAOCaja();			
			List<Caja> cajas;
			if (from == null && to == null) {
				cajas = dao.listar();
			} else {
				cajas = dao.getCajas(from, to);
			}
			ctx.json(cajas);
            
		} catch (ParseException e) {
			ctx.status(400).result("Fechas invalidas");
			return;
		}
		catch(Exception e){
			Log.log(e,CajaControl.class);
		}
    }

	//Caja/{id}
    public static void getMovimientos(Context ctx){
		try{
			DAOCaja dao = new DAOCaja();
			String id = ctx.pathParam("id");
			ctx.json(dao.getItems(Long.parseUnsignedLong(id)));
		}
		catch(Exception e){
			Log.log(e,CajaControl.class);
		}
    }

	//Transacciones
	public static void getMovimientosFechas(Context ctx){
		String fromParam = ctx.queryParam("from");
		String toParam = ctx.queryParam("to");
		String tipoParam = ctx.queryParam("tipo");
		String pagoParam = ctx.queryParam("pago");
		Date from = null;
		Date to = null;
		Integer tipoVenta = null;
		Integer pago = null;
		String pagoV = null;
		try {
			if (fromParam != null) {
				from = formatter.parse(fromParam);
				from = diaAnterior(from);
			}
			if (toParam != null) {
				to = formatter.parse(toParam);
				to = ultimaHora(to);
			}
			if (tipoParam != null) {
				tipoVenta = Integer.parseInt(tipoParam);
			}
			if (pagoParam != null) {
				pago = Integer.parseInt(pagoParam);
			}
            if (pago != null) {
                switch (pago) {
                case 0: pagoV = "Efectivo"; break;
                case 1: pagoV = "Mercado Pago"; break;
                case 2: pagoV = "Debito"; break;
                case 3: pagoV = "Cripto"; break;
                case 4: pagoV = "Otro"; break;
                default:
                    ctx.status(400).result("Pago invalido");
                    return;
                }
            }
            DAOCaja dao = new DAOCaja();
			List<ItemCaja> items = dao.getItems(from, to, tipoVenta, pagoV);
			ctx.json(items);
		} catch (Exception e) {
			ctx.status(400).result("Parametros invalidos");
			Log.log(e,CajaControl.class);
		}
	}	


    //TODOS ESTOS METODOS TENDRIAN QUE IR EN UNA CLASE DE CONTROLES/MANEJO DE EXCEPCIONES
    
    public static Date sinHoras(Date d){
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (Date) cal.getTime();
    }

    public static Date diaAnterior(Date d){
        if (d==null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return (Date) cal.getTime();
    }

    public static Date ultimaHora(Date d){
        if (d==null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.SECOND, -1);
        return (Date) cal.getTime();
    }

    public static boolean compararFechas(Date fecha1, Date fecha2){
        Date fecha1Aux = sinHoras(fecha1);
        Date fecha2Aux = sinHoras(fecha2);
        return fecha1Aux.equals(fecha2Aux);
    }
*/
}
