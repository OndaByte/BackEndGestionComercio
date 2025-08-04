
package com.OndaByte.GestionComercio.control;


import com.OndaByte.GestionComercio.DAO.DAOOrden;
import com.OndaByte.GestionComercio.DAO.DAORemito;
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

public class RemitoControlador {

    private static Logger logger = LogManager.getLogger(RemitoControlador.class.getName());

    public static void listar(Context ctx) {
        logger.debug("Listar");
        try {
            DAORemito dao = new DAORemito();
            List<Remito> remitos = (List<Remito>) dao.listar();
            JSONArray data = new JSONArray(remitos);

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
            DAORemito dao = new DAORemito();
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
                ctx.status(404).result(buildRespuesta(404, null, "No se encontraron remitos"));
                return;
            }

            JSONArray data = new JSONArray();
            ObjectMapper objectMapper = new ObjectMapper();

            for (HashMap<String, Object> fila : filas) {
                Remito remito = (Remito) fila.get("remito");
                Cliente cliente = (Cliente) fila.get("cliente");
                Orden orden = (Orden) fila.get("orden");
                //Presupuesto presupuesto = (Presupuesto) fila.get("presupuesto");

                JSONObject jo = new JSONObject();
                jo.put("remito", new JSONObject(objectMapper.writeValueAsString(remito)));
                jo.put("cliente", new JSONObject(objectMapper.writeValueAsString(cliente)));
                jo.put("orden", new JSONObject(objectMapper.writeValueAsString(orden)));
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
        try {
            logger.debug("actualizar");
            String id = ctx.pathParam("id");
            JSONObject bodyJson = new JSONObject(ctx.body());
            String nuevoFechaPago = bodyJson.getString("fecha_pago").toUpperCase();
            if(nuevoFechaPago == null)
            {
                ctx.status(400).result(buildRespuesta(400, null, "Estado de Remito Incorrecto"));
                return;
            }

            DAORemito dao = new DAORemito();
            Remito aux = dao.buscar(id);

            if (aux == null) {
                ctx.status(404).result(buildRespuesta(404, null, "No se encontro el remito requerido."));
                return;
            }
            aux.setFecha_pago(nuevoFechaPago);
            if (dao.modificar(aux)) {
                ctx.status(201).result(buildRespuesta(201, "{\"id\":" + aux.getId() + "}", "Actualización exitosa"));
            } else {
                ctx.status(500).result(buildRespuesta(500, null, "Error al actualizar el remito"));
            }

        } catch (JSONException e) {
            logger.debug("Formulario Incorrecto: " + e.getMessage(), e);
            ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
            return;
        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("Error inesperado en RemitoControlador.actualizar(): " + e.getMessage(), e);
        }
    }

    public static void editar(Context ctx){
        logger.debug("RemitoControlador.editar");
        try {
            //Lectura path
            String id = ctx.pathParam("id");
            //Lectura body
            JSONObject joBody = new JSONObject(ctx.body());
            JSONObject joRemito = joBody.getJSONObject("remito");
            JSONArray jiArray = joBody.getJSONArray("items");
            //Estructuras de Datos
            Remito nuevo = new Remito();
            List<ItemRemito> itemsR = new ArrayList<>();
            DAORemito dao = new DAORemito();

            nuevo.setId(Integer.valueOf(id));
            nuevo.setTotal(joRemito.getFloat("total"));
            nuevo.setObservaciones(joRemito.optString("observaciones",null));

            for (int i = 0; i < jiArray.length(); i++) {
                JSONObject itemObj = jiArray.getJSONObject(i);
                ItemRemito ir = new ItemRemito();
                ir.setDescripcion(itemObj.getString("descripcion"));
                ir.setPrecio(itemObj.getFloat("precio"));
                ir.setCantidad(itemObj.optInt("cantidad",1));
                itemsR.add(ir);
            }
            
            if (dao.modificarConItems(nuevo,itemsR)) {
                ctx.status(201).result(buildRespuesta(201, "{\"id\":" + nuevo.getId() + "}", "Modificación exitosa"));
            } else {
                ctx.status(500).result(buildRespuesta(500, null, "Error al modificar el Remito"));
            }
            
        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("alta()" + e.getMessage());
        }
    }

    /**
     * Obtiene remito y sus items
     * @param ctx
     */
    public static void buscarDetallado(Context ctx){
        try {
            String id = ctx.pathParam("id");

            DAORemito dao = new DAORemito();
            HashMap<String, Object> data = dao.buscarDetallado(id);

            if(data == null){
                ctx.status(404).result(buildRespuesta(404, null, "No se encontro el Remito."));
                return;
            }

            Cliente c = (Cliente) data.get("cliente");
            Orden o = (Orden) data.get("orden");
            Remito remito = (Remito) data.get("remito");
            List<ItemRemito> items = (List<ItemRemito>) data.get("items");

            ObjectMapper objectMapper = new ObjectMapper();
            JSONObject jsonRemito = new JSONObject(objectMapper.writeValueAsString(remito));
            JSONObject jsonCliente = new JSONObject(objectMapper.writeValueAsString(c));
            JSONObject jsonOrden = new JSONObject(objectMapper.writeValueAsString(o));
            JSONArray jsonItems = new JSONArray(objectMapper.writeValueAsString(items));

            JSONObject respuesta = new JSONObject();
            respuesta.put("remito", jsonRemito);
            respuesta.put("cliente", jsonCliente);
            respuesta.put("orden", jsonOrden);
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
        logger.debug("RemitoControlador.alta");
        try {
            //Lectura body
            JSONObject joBody = new JSONObject(ctx.body());
            JSONObject joRemito = joBody.getJSONObject("remito");
            JSONArray jiArray = joBody.getJSONArray("items");
            //Estructuras de Datos
            Remito nuevo = new Remito();
            List<ItemRemito> itemsR = new ArrayList<>();
            DAORemito dao = new DAORemito();
            DAOOrden daoOrden = new DAOOrden();
            // ...
            nuevo.setFecha_emision(joRemito.getString("fecha_emision"));
            nuevo.setFecha_pago(joRemito.optString("fecha_pago",null));
            nuevo.setPunto_venta("00006");
            nuevo.setTotal(joRemito.getFloat("total"));
            nuevo.setObservaciones(joRemito.optString("observaciones",null));
            nuevo.setOrden_id(joRemito.getInt("orden_id"));
            nuevo.setCliente_id(joRemito.getInt("cliente_id"));
            nuevo.setCliente_cuit_cuil(joRemito.getString("c_cuit_cuil"));
            nuevo.setCliente_nombre(joRemito.getString("c_nombre"));
            nuevo.setCliente_domicilio(joRemito.getString("c_domicilio"));
            nuevo.setCliente_localidad(joRemito.getString("c_localidad"));
            nuevo.setCliente_telefono(joRemito.getString("c_telefono"));

            for (int i = 0; i < jiArray.length(); i++) {
                JSONObject itemObj = jiArray.getJSONObject(i);
                ItemRemito ir = new ItemRemito();
                ir.setDescripcion(itemObj.getString("descripcion"));
                ir.setPrecio(itemObj.getFloat("precio"));
                ir.setCantidad(itemObj.optInt("cantidad",1));
                itemsR.add(ir);
            }

            Orden o = daoOrden.buscar(nuevo.getOrden_id()+"");
            if (o == null) {
                ctx.status(404).result(buildRespuesta(404, null, "No se encontro el Orden asociado al remito."));
                return;
            }
            long alta_id = dao.altaConItems(nuevo,itemsR);
            if (alta_id > -1) {
                o.setEstado_orden("ENTREGADA");
                if(daoOrden.modificar(o)){
                    ctx.status(201).result(buildRespuesta(201, "{\"id\":" + alta_id + "}", "Alta exitosa"));
                 //   ctx.status(201).result(buildRespuesta(201, "{\"orden_id\":"+orden_id+"}", "Actualizacion exitosa, Orden dada de alta."));
                } else {
                    ctx.status(500).result(buildRespuesta(500, null, "Error, no se pudo actualizar la orden."));
                }   
                //GENERO LOS ITEMS CON EL remito_id correspondiente
            } else {
                ctx.status(500).result(buildRespuesta(500, null, "Error al dar de alta el Remito"));
            }
            
            
        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("alta()" + e.getMessage());
        }
    }

    public static void baja(Context ctx) {
        logger.debug("RemitoControlador.baja");

        String id = ctx.pathParam("id");

        if (id == null || id.isEmpty()) {
            ctx.status(400).result(buildRespuesta(400, null, "ID requerido"));
            return;
        }

        try {
            Integer id_int = Integer.parseInt(id);
            DAORemito dao = new DAORemito();
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
}
