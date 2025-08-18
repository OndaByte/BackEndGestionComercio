package com.OndaByte.GestionComercio.control;

import com.OndaByte.GestionComercio.util.Parsero;
import com.OndaByte.GestionComercio.util.Seguridad;
import com.OndaByte.GestionComercio.modelo.Caja;
import com.OndaByte.GestionComercio.modelo.Movimiento;
import com.OndaByte.GestionComercio.DAO.DAOCaja;
import com.OndaByte.GestionComercio.DAO.DAOVenta;
import com.OndaByte.GestionComercio.modelo.ItemVenta;
import com.OndaByte.GestionComercio.modelo.Venta;
import static com.OndaByte.GestionComercio.util.Respuesta.buildRespuesta;
import com.OndaByte.config.Constantes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.javalin.http.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CajaControlador {

    private static Logger logger = LogManager.getLogger(CajaControlador.class.getName());

    public static void alta(Context ctx) {
        try {
            logger.debug("Alta:\n" + ctx.body());
            JSONObject bodyJson = new JSONObject(ctx.body());

            DAOCaja dao = new DAOCaja();
            Caja nueva = new Caja();
            nueva.setNombre(bodyJson.getString("nombre"));
            logger.debug(nueva.getNombre());
            Integer alta_id = dao.alta(nueva);

            logger.debug(alta_id);
            if (alta_id > -1) {
                ctx.status(201).result(buildRespuesta(201, "{\"id\":" + alta_id + "}", "Alta exitosa"));
                logger.debug("Alta res: " + 201);
            } else {
                ctx.status(500).result(buildRespuesta(500, null, "Error al dar de alta la caja"));
                logger.debug("Alta res: " + 500);
            }
        } catch (JSONException e) {
            ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
            logger.debug("Alta res: " + 400);
        } catch (Exception e) {
            logger.error("Alta :" + e.getMessage());
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.debug("Alta res: " + 500);
            }
        }
    }

    public static void baja(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            logger.debug("Baja: " + id);
            if (id == null || id.isEmpty()) {
                ctx.status(400).result(buildRespuesta(400, null, "ID"));
                logger.debug("Baja res: " + 400);
                return;
            }
            DAOCaja dao = new DAOCaja();
            boolean resultado = dao.baja(id, false);
            if (resultado) {
                ctx.status(200).result(buildRespuesta(201, null, "Baja exitosa"));
                logger.debug("Baja res: " + 201);
            } else {
                ctx.status(404).result(buildRespuesta(404, null, "No se encontró el registro"));
                logger.debug("Baja res: " + 404);
            }

        } catch (Exception e) {
            logger.error("Baja: " + e.getMessage());
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.debug("Baja res: " + 500);
            }
        }
    }

    public static void obtenerUltimaSesion(Context ctx) {
        try {
            String caja_id = ctx.pathParam("id");

            if (caja_id == null || caja_id.isEmpty()) {
                ctx.status(400).result(buildRespuesta(400, null, "Error: Parametro incorrecto"));
                logger.debug("ObtenerUltimaSesion res: " + 400);
                return;
            }

            DAOCaja dao = new DAOCaja();
            Integer ultimaSesion = dao.obtenerUltimaSesion(caja_id);

            if (ultimaSesion != null && ultimaSesion > -1) {
                ctx.status(200).result(buildRespuesta(200, "{\"id\":"+ultimaSesion+"}", "Última sesión obtenida con éxito"));
                logger.debug("ObtenerUltimaSesion res: " + 200);
            } else {
                ctx.status(404).result(buildRespuesta(404, null, "No se encontró ninguna sesión para la caja"));
                logger.debug("ObtenerUltimaSesion res: " + 404);
            }

        } catch (Exception e) {
            logger.error("ObtenerUltimaSesion: " + e.getMessage(), e);
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.debug("ObtenerUltimaSesion res: " + 500);
            }
        }
    }


    public static void abrir(Context ctx) {
        try {
            String token = ctx.header("token");
            String cajero_id = Seguridad.validar(token).getSubject();
            String id = ctx.pathParam("id");
            logger.debug("Abrir: " + id);
            if (id == null || id.isEmpty()) {
                ctx.status(400).result(buildRespuesta(400, null, "Error: Parametro incorrecto"));
                logger.debug("Baja res: "+400);
                return;
            }

            Float monto = 0.f;
            try{
                JSONObject bodyJson = new JSONObject(ctx.body());
                monto = bodyJson.getFloat("monto");
            }catch(JSONException e1){
                logger.debug("Abrir: sin body");
            }
            
            DAOCaja dao = new DAOCaja();
            Integer sesion = dao.abrir(id, cajero_id, monto);
            if (sesion>-1) {
                ctx.status(201).result(buildRespuesta(201, "{\"id\":"+sesion+"}", "Caja abierta con exito"));
                logger.debug("Abrir res: "+201);
            } else {
                ctx.status(404).result(buildRespuesta(404,  null, "No se encontró el registro"));
                logger.debug("Abrir res: "+404);
            }

        } catch(SQLException e){
            ctx.status(409).result(buildRespuesta(409,  null, e.getMessage()));
        } catch (Exception e) {
            logger.error("Abrir: " + e.getMessage(), e);
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.debug("Abrir res: "+500);
            }
        }
    }
    
    public static void cerrar(Context ctx) {
        try {
            String cajero_id = ctx.sessionAttribute("usuario");
            String caja_id = ctx.pathParam("id");
            Boolean esAdmin = ctx.sessionAttribute("rol").equals("ADMIN");
                        
            if (caja_id == null || caja_id.isEmpty()) {
                ctx.status(400).result(buildRespuesta(400, null, "Error: Parametro parametro incorrecto"));
                logger.debug("Cerrar res: "+400);
                return;
            }          
            
            DAOCaja dao = new DAOCaja();
            Boolean res = dao.cerrar(caja_id, cajero_id, esAdmin);
            if (res) {
                ctx.status(201).result(buildRespuesta(201,null, "Caja cerrada con exito"));
                logger.debug("Cerrar res: "+201);
            } else {
                ctx.status(404).result(buildRespuesta(404, null, "No se encontró el registro"));
                logger.debug("Cerrar res: "+404);
            }

        } catch (Exception e) {
            logger.error("Cerrar: " + e.getMessage(), e);
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.debug("Cerrar res: "+500);
            }
        }
    }
    public static void movimiento(Context ctx) {
        try {
            Movimiento nuevo = ctx.bodyAsClass(Movimiento.class);
            DAOCaja dao = new DAOCaja();
            Boolean res = dao.movimiento(nuevo);
            if (res) {
                ctx.status(201).result(buildRespuesta(201, null, "Movimiento registrado con exito."));
                logger.debug("Movimiento res: " + 201);
            } else {
                ctx.status(400).result(buildRespuesta(404, null, "Error inesperado"));
                logger.debug("Movimiento res: " + 404);
            }

        } catch (SQLException e) {
            ctx.status(409).result(buildRespuesta(409, null, e.getMessage()));
        } catch (Exception e) {
            logger.error("Movimiento: " + e.getMessage(), e);
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.debug("Movimiento res: " + 500);
            }
        }
    }

    public static void movimientosCaja(Context ctx) {
        logger.debug("MovimientosCaja");
        try {
            DAOCaja dao = new DAOCaja();
            List<Movimiento> movs = dao.movimientosCaja(ctx.pathParam("id"));
            JSONArray data = new JSONArray(movs);
            ctx.status(200).json(buildRespuesta(200, data.toString(), "OK"));
        } catch (Exception e) {
        }
    }

    public static void movimientosCajaOP(Context ctx) {
        logger.debug("movimientosCajaOP");
        try {
            Integer pagina = Parsero.safeParse(ctx.queryParam("pagina"));
            Integer cantElementos = Parsero.safeParse(ctx.queryParam("elementos"));
            DAOCaja dao = new DAOCaja();
            String filtro = ctx.queryParam("filtro");
            String desde = ctx.queryParam("desde");
            String hasta = ctx.queryParam("hasta");
            
            logger.debug("movimientosCajaOP:\n" + filtro);
            ArrayList<String> campos = new ArrayList<>();
            ArrayList<String> valores = new ArrayList<>();
            ArrayList<Integer> condiciones = new ArrayList<>();
            ArrayList<Boolean> conectores = new ArrayList<>();

             
            if (desde != null && desde.isEmpty()) {
                desde = null;
            }
            if (hasta != null && hasta.isEmpty()) {
                hasta = null;
            }
             
            if (desde != null && hasta != null) {
                desde += " 00:00:00";
                hasta += " 23:59:59";
                campos.add("creado");
                valores.add(desde);
                condiciones.add(Constantes.SQL_MAYOR_IGUAL); // fecha >= desde
                conectores.add(Constantes.SQL_AND);
                campos.add("creado");
                valores.add(hasta);
                condiciones.add(Constantes.SQL_MENOR_IGUAL); // fecha <= hasta
                conectores.add(Constantes.SQL_AND);
            }
            
            if (filtro != null && !filtro.isEmpty()) {
                campos.add("descripcion");
                valores.add("%" + filtro + "%");
                condiciones.add(Constantes.SQL_LIKE);
                conectores.add(Constantes.SQL_AND);
            }
                        
            HashMap<String, Object> resultDao = dao.movimientosCaja(campos, valores, condiciones, conectores, pagina, cantElementos);

            List<Movimiento> filas = (List<Movimiento>) resultDao.get("data");

            if (filas == null) {
                ctx.status(404).result(buildRespuesta(404, null, "No se encontraron datos"));
                return;
            }
            JSONArray data = new JSONArray(filas);
            ctx.status(200).json(buildRespuesta(
                    200,
                    data.toString(),
                    "",//mensaje
                    resultDao.get("pagina") + "",
                    resultDao.get("elementos") + "",
                    resultDao.get("t_elementos") + "",
                    resultDao.get("t_paginas") + ""
            )
            );

        } catch (Exception e) {
            e.printStackTrace();
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("movimientosCajaOP: " + e.getMessage());
        }
    }

    public static void resumenCaja(Context ctx) {
        logger.debug("resumenCajaOP");
        try {
            String filtro = ctx.queryParam("filtro");
            String desde = ctx.queryParam("desde");
            String hasta = ctx.queryParam("hasta");
            if (filtro != null && filtro.isEmpty()) {
                filtro = null;
            }
            if (desde != null && desde.isEmpty()) {
                desde = null;
            }
            if (hasta != null && hasta.isEmpty()) {
                hasta = null;
            }
            DAOCaja dao = new DAOCaja();
            HashMap<String, Object> resumen = dao.resumenCaja(filtro, desde, hasta);

            if (resumen == null) {
                ctx.status(404).result(buildRespuesta(404, null, "No se pudo generar el resumen"));
                return;
            }

            ctx.status(200).json(buildRespuesta(
                    200,
                    new JSONObject(resumen).toString(), // Se puede devolver directo como JSON
                    "Resumen generado correctamente"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("resumenCajaOP: " + e.getMessage());
        }
    }

    
    public static void altaVenta(Context ctx) {
        logger.debug("altaVenta");
        try {
            //Lectura body
            JSONObject joBody = new JSONObject(ctx.body());
            JSONObject joVenta = joBody.getJSONObject("venta");
            JSONArray jiArray = joBody.getJSONArray("items");
            //Estructuras de Datos
            Venta nueva = new Venta();
            List<ItemVenta> itemsV = new ArrayList<>();
            DAOVenta dao = new DAOVenta();
            nueva.setCliente_id(joVenta.optIntegerObject("cliente_id",null));
            nueva.setForma_pago(joVenta.getString("forma_pago"));
            nueva.setPunto_venta("00001");
            nueva.setSubtotal(joVenta.getFloat("subtotal"));
            nueva.setPorcentaje_descuento(joVenta.getInt("porcentaje_descuento"));
            nueva.setTotal(joVenta.getFloat("total"));
            nueva.setObservaciones(joVenta.optString("observaciones",null));
            //nueva. etc. null.
            for (int i = 0; i < jiArray.length(); i++) {
                JSONObject itemObj = jiArray.getJSONObject(i);
                ItemVenta iv = new ItemVenta();
                iv.setNombre(itemObj.getString("nombre"));
                iv.setProducto_id(itemObj.getInt("producto_id"));
                iv.setSubtotal(itemObj.getFloat("subtotal"));
                iv.setPorcentaje_descuento(itemObj.getInt("porcentaje_descuento"));
                iv.setCantidad(itemObj.optInt("cantidad",1));
                itemsV.add(iv);
            }

            long alta_id = dao.altaConItems(nueva,itemsV);
            if (alta_id > -1) {
                ctx.status(201).result(buildRespuesta(201, "{\"id\":" + alta_id + "}", "Alta exitosa"));
            } else {
                ctx.status(500).result(buildRespuesta(500, null, "Error al dar de alta la Venta"));
            }
        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("alta()" + e.getMessage());
        }
    }


    
    public static void filtrar(Context ctx) {
        try {
            Integer pagina = Parsero.safeParse(ctx.queryParam("pagina"));
            Integer cantElementos = Parsero.safeParse(ctx.queryParam("elementos")); // cantElementos
            DAOCaja dao = new DAOCaja();
            String filtro = ctx.queryParam("filtro");
            logger.debug("Filtrar:\n" + filtro);
            ArrayList<String> campos = new ArrayList<>();
            ArrayList<String> valores = new ArrayList<>();
            ArrayList<Integer> condiciones = new ArrayList<>();
            ArrayList<Boolean> conectores = new ArrayList<>();

            if (filtro != null && !filtro.isEmpty()) {
                campos.add("nombre");
                valores.add("%" + filtro + "%");
                condiciones.add(5);
                conectores.add(false);
            }
            logger.debug("pag" + pagina + " cant" + cantElementos);
            HashMap<String, Object> resultDao = dao.filtrar(campos, valores, condiciones, conectores, pagina, cantElementos);

            List<Caja> filas = (List<Caja>) resultDao.get("data");

            if (filas == null) {
                ctx.status(404).result(buildRespuesta(404, null, "No se encontraron datos"));
                return;
            }
            JSONArray data = new JSONArray(filas);
            ctx.status(200).json(buildRespuesta(
                    200,
                    data.toString(),
                    "",//mensaje
                    resultDao.get("pagina") + "",
                    resultDao.get("elementos") + "",
                    resultDao.get("t_elementos") + "",
                    resultDao.get("t_paginas") + ""
            )
            );

        } catch (Exception e) {
            e.printStackTrace();
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("Filtrar:" + e.getMessage(), e);
        }
    }
}
