
package com.OndaByte.GestionComercio.control;


import com.OndaByte.GestionComercio.DAO.DAOOrden;
import com.OndaByte.GestionComercio.DAO.DAOVenta;
import com.OndaByte.GestionComercio.modelo.*;
import com.OndaByte.GestionComercio.util.Parsero;
import static com.OndaByte.GestionComercio.util.Respuesta.buildRespuesta;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VentaControlador {

    private static Logger logger = LogManager.getLogger(VentaControlador.class.getName());

    public static void listar(Context ctx) {
        logger.debug("Listar");
        try {
            DAOVenta dao = new DAOVenta();
            List<Venta> ventas = (List<Venta>) dao.listar();
            JSONArray data = new JSONArray(ventas);

            ctx.status(200).json(buildRespuesta(200, data.toString(), ""));

        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("Listar" + e.getMessage());
        }
    }

    public static void filtrarDetalladoPaginado(Context ctx) {
        logger.debug("FiltrarDetalladoPaginado");
        try {
            Integer pagina = Parsero.safeParse(ctx.queryParam("pagina"));
            Integer cantElementos = Parsero.safeParse(ctx.queryParam("elementos"));
            DAOVenta dao = new DAOVenta();
            String filtro = ctx.queryParam("filtro");
            String desde = ctx.queryParam("desde");
            String hasta = ctx.queryParam("hasta");
            String estado = ctx.queryParam("estado");

            logger.debug("Filtrar:\n" + filtro);

            if (pagina == null || cantElementos == null) {
                ctx.status(400).result(buildRespuesta(400, null, "No ha especificado correctamente los parámetros"));
                return;
            }
            if (filtro != null && filtro.isEmpty()) filtro = null;
            if (desde != null && desde.isEmpty()) desde = null;
            if (hasta != null && hasta.isEmpty()) hasta = null;
            if (estado != null && estado.isEmpty()) estado = null;

            HashMap<String, Object> resultDao = dao.filtrarDetalladoOP(filtro, desde, hasta, estado, pagina, cantElementos);
            List<HashMap<String, Object>> filas = (List<HashMap<String, Object>>) resultDao.get("data");

            if (filas == null) {
                ctx.status(404).result(buildRespuesta(404, null, "No se encontraron ventas"));
                return;
            }

            JSONArray data = new JSONArray();
            ObjectMapper objectMapper = new ObjectMapper();

            for (HashMap<String, Object> fila : filas) {
                Venta venta = (Venta) fila.get("venta");
                Cliente cliente = (Cliente) fila.get("cliente");
                Orden orden = (Orden) fila.get("orden");
                //Presupuesto presupuesto = (Presupuesto) fila.get("presupuesto");

                JSONObject jo = new JSONObject();
                jo.put("venta", new JSONObject(objectMapper.writeValueAsString(venta)));
                jo.put("cliente", new JSONObject(objectMapper.writeValueAsString(cliente)));
                //jo.put("presupuesto", new JSONObject(objectMapper.writeValueAsString(presupuesto)));

                data.put(jo);
            }

            ctx.status(200).json(buildRespuesta(
                    200,
                    data.toString(),
                    "",
                    resultDao.get("pagina") + "",
                    resultDao.get("elementos") + "",
                    resultDao.get("t_elementos") + "",
                    resultDao.get("t_paginas") + ""
            ));

        } catch (Exception e) {
            e.printStackTrace();
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("FiltrarDetalladoPaginado: " + e.getMessage(), e);
        }
    }

   
    public static void actualizar(Context ctx) {
//        try {
//            logger.debug("actualizar");
//            String id = ctx.pathParam("id");
//            JSONObject bodyJson = new JSONObject(ctx.body());
//            String nuevoFechaPago = bodyJson.getString("fecha_pago").toUpperCase();
//            if(nuevoFechaPago == null)
//            {
//                ctx.status(400).result(buildRespuesta(400, null, "Estado de Venta Incorrecto"));
//                return;
//            }
//
//            DAOVenta dao = new DAOVenta();
//            Venta aux = dao.buscar(id);
//
//            if (aux == null) {
//                ctx.status(404).result(buildRespuesta(404, null, "No se encontro el venta requerido."));
//                return;
//            }
//            aux.setFecha_pago(nuevoFechaPago);
//            if (dao.modificar(aux)) {
//                ctx.status(201).result(buildRespuesta(201, "{\"id\":" + aux.getId() + "}", "Actualización exitosa"));
//            } else {
//                ctx.status(500).result(buildRespuesta(500, null, "Error al actualizar el venta"));
//            }
//
//        } catch (JSONException e) {
//            logger.debug("Formulario Incorrecto: " + e.getMessage(), e);
//            ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
//            return;
//        } catch (Exception e) {
//            if (ctx != null) {
//                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
//            }
//            logger.error("Error inesperado en VentaControlador.actualizar(): " + e.getMessage(), e);
//        }
    }

    public static void editar(Context ctx){
        logger.debug("VentaControlador.editar");
//        try {
//            //Lectura path
//            String id = ctx.pathParam("id");
//            //Lectura body
//            JSONObject joBody = new JSONObject(ctx.body());
//            JSONObject joVenta = joBody.getJSONObject("venta");
//            JSONArray jiArray = joBody.getJSONArray("items");
//            //Estructuras de Datos
//            Venta nuevo = new Venta();
//            List<ItemVenta> itemsR = new ArrayList<>();
//            DAOVenta dao = new DAOVenta();
//
//            nuevo.setId(Integer.valueOf(id));
//            nuevo.setTotal(joVenta.getFloat("total"));
//            nuevo.setObservaciones(joVenta.optString("observaciones",null));
//
//            for (int i = 0; i < jiArray.length(); i++) {
//                JSONObject itemObj = jiArray.getJSONObject(i);
//                ItemVenta ir = new ItemVenta();
//                ir.setDescripcion(itemObj.getString("descripcion"));
//                ir.setPrecio(itemObj.getFloat("precio"));
//                ir.setCantidad(itemObj.optInt("cantidad",1));
//                itemsR.add(ir);
//            }
//            
//            if (dao.modificarConItems(nuevo,itemsR)) {
//                ctx.status(201).result(buildRespuesta(201, "{\"id\":" + nuevo.getId() + "}", "Modificación exitosa"));
//            } else {
//                ctx.status(500).result(buildRespuesta(500, null, "Error al modificar el Venta"));
//            }
//            
//        } catch (Exception e) {
//            if (ctx != null) {
//                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
//            }
//            logger.error("alta()" + e.getMessage());
//        }
    }

    /**
     * Obtiene venta y sus items
     * @param ctx
     */
    public static void buscarDetallado(Context ctx){
        try {
            String id = ctx.pathParam("id");

            DAOVenta dao = new DAOVenta();
            HashMap<String, Object> data = dao.buscarDetallado(id);

            if(data == null){
                ctx.status(404).result(buildRespuesta(404, null, "No se encontro el Venta."));
                return;
            }

            Cliente c = (Cliente) data.get("cliente");
           
            Venta venta = (Venta) data.get("venta");
            List<ItemVenta> items = (List<ItemVenta>) data.get("items");

            ObjectMapper objectMapper = new ObjectMapper();
            JSONObject jsonVenta = new JSONObject(objectMapper.writeValueAsString(venta));
            JSONObject jsonCliente = new JSONObject(objectMapper.writeValueAsString(c));
            JSONArray jsonItems = new JSONArray(objectMapper.writeValueAsString(items));

            JSONObject respuesta = new JSONObject();
            respuesta.put("venta", jsonVenta);
            respuesta.put("cliente", jsonCliente);
            respuesta.put("items", jsonItems);

            ctx.status(200).json(buildRespuesta(
                    200,
                    respuesta.toString(),
                    ""
            ));

        }catch (JSONException je) {
            ctx.status(400).result(buildRespuesta(400, null, "Formulario Inválido")); //corrupto
            return;
        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("Modificar: " + e.getMessage(), e);
        }
    }

    public static void alta(Context ctx) {
        logger.debug("alta()");
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

    public static void baja(Context ctx) {
        logger.debug("VentaControlador.baja");

        String id = ctx.pathParam("id");

        if (id == null || id.isEmpty()) {
            ctx.status(400).result(buildRespuesta(400, null, "ID requerido"));
            return;
        }

        try {
            Integer id_int = Integer.parseInt(id);
            DAOVenta dao = new DAOVenta();
            boolean resultado = dao.baja(id_int);
            if (resultado) {
                ctx.status(200).result(buildRespuesta(201, null, "Baja exitosa"));
            } else {
                ctx.status(404).result(buildRespuesta(404, null, "No se encontró el registro"));
            }

        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("baja() " + e.getMessage());
        }
    }
    public static void resumenVenta(Context ctx) {
        logger.debug("resumenVenta");
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
            DAOVenta dao = new DAOVenta();
            HashMap<String, Object> resumen = dao.resumenVentasConContador(filtro, desde, hasta);

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
}
