package com.OndaByte.GestionComercio.control;

import com.OndaByte.GestionComercio.DAO.DAOOrden;
import com.OndaByte.GestionComercio.DAO.DAORemito;
import com.OndaByte.GestionComercio.modelo.Cliente;
import com.OndaByte.GestionComercio.modelo.Orden;
import com.OndaByte.GestionComercio.modelo.Pedido;
import com.OndaByte.GestionComercio.modelo.Remito;
import com.OndaByte.GestionComercio.modelo.Presupuesto;
import com.OndaByte.GestionComercio.modelo.Turno;
import com.OndaByte.GestionComercio.util.Parsero;
import static com.OndaByte.GestionComercio.util.Respuesta.buildRespuesta;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import io.javalin.http.Context;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrdenControlador {

    private static Logger logger = LogManager.getLogger(OrdenControlador.class.getName());
    
    public static void listar(Context ctx){
        logger.debug("Listar:");
        try{
            DAOOrden dao = new DAOOrden();
            List<Orden> ordenes = dao.listar();
            JSONArray data = new JSONArray(ordenes);
            ctx.status(200).json(buildRespuesta(200, data.toString(), ""));
        }
        catch (JSONException e){
            ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
            return;
        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("Listar" + e.getMessage());
        }
    }

    public static void cantEstado(Context ctx){
        logger.debug("cantEstado");
        try {
            String estado = ctx.queryParam("estado");
            DAOOrden dao = new DAOOrden();
            Integer data = dao.cantEstado(estado);
            ctx.status(200).json(buildRespuesta(200, data.toString(), ""));

        } catch (JSONException e){
            ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
            return;
        }
        catch (Exception e) {
            if(ctx!= null){
                ctx.status(500).result(buildRespuesta(500, null,"Error inesperado."));
            }
            logger.error("cantEstado: " + e.getMessage(), e);
        }
    }


    public static void actualizar(Context ctx)  {
        try {
            logger.debug("actualizar");
            String id = ctx.pathParam("id");
            JSONObject bodyJson = new JSONObject(ctx.body());
            //'PENDIENTE', 'ASIGNADA', 'PROCESANDO', 'COMPLETADA', 'ENTREGADA'
            String estadoaux = bodyJson.getString("estado_orden").toUpperCase();
            if(!(estadoaux.equals("PENDIENTE") || 
               estadoaux.equals("ASIGNADA") || 
               estadoaux.equals("PROCESANDO") || 
               estadoaux.equals("COMPLETADA") || 
               estadoaux.equals("ENTREGADA")))
                {
                    ctx.status(400).result(buildRespuesta(400, null, "Estado de Orden Incorrecto"));
                    return;
                }
            
            DAOOrden dao = new DAOOrden();
            Orden aux = dao.buscar(id);

            if(aux == null){
                ctx.status(404).result(buildRespuesta(404, null, "No se encontro el ID "+id+"."));
                return;
            }
            if(aux.getEstado_orden().equals("ENTREGADA")
               || aux.getEstado_orden().equals("COMPLETADA")){
                ctx.status(400).result(buildRespuesta(400, null, "La Orden ya ha sido archivada, no puede modificar su estado actual: "+aux.getEstado_orden()));
                return;
            }
//            if(aux.getEstado_orden().equals("RETIRAR") &&
//               !(estadoaux.equals("ENTREGADA") || estadoaux.equals("COMPLETAD"))){
//                ctx.status(400).result(buildRespuesta(400, null, "La Orden ha sido completada, debe archivarla actualizando su estado a ENTREGADA o CANCELADA."));
//                return;
//            }
            aux.setEstado_orden(estadoaux);
            if (dao.modificar(aux)) {
                if(aux.getEstado_orden().equals("COMPLETADA")){

                    DAORemito daoRemito = new DAORemito();
                    Remito remito = new Remito();                        
                    remito.setOrden_id(aux.getId());
//                    long orden_id = -1 ; //daoRemito.alta(remito);
//
//                    if(orden_id > -1){
//                        ctx.status(201).result(buildRespuesta(201, "{\"orden_id\":"+orden_id+"}", "Actualizacion exitosa, Orden dada de alta."));
//                    } else {
//                        ctx.status(500).result(buildRespuesta(500, null, "Error, no se pudo dar de alta la orden."));
//                    }
                }
                ctx.status(201).result(buildRespuesta(201, null, "Actualizacion exitosa"));
            } else {
                ctx.status(500).result(buildRespuesta(500, null, "Error al actualizar el pedido"));
            }
        }
        catch (JSONException e){
            ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
            return;
        }
        catch (Exception e) {
            if(ctx!= null){
                ctx.status(500).result(buildRespuesta(500, null,"Error inesperado."));
            }
            logger.error("Error inesperado en OrdenControlador.actualizar(): " + e.getMessage(), e);
        }
    }

    public static void filtrarDetalladoPaginado(Context ctx) {
        logger.debug("FiltrarDetalladoPaginado");
    try {
        Integer pagina = Parsero.safeParse(ctx.queryParam("pagina"));
        Integer cantElementos = Parsero.safeParse(ctx.queryParam("elementos"));
        DAOOrden dao = new DAOOrden();
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
            ctx.status(404).result(buildRespuesta(404, null, "No se encontraron datos"));
            return;
        }

        JSONArray data = new JSONArray();
        ObjectMapper objectMapper = new ObjectMapper();

        for (HashMap<String, Object> fila : filas) {
            Orden orden = (Orden) fila.get("orden");
            Pedido pedido = (Pedido) fila.get("pedido");
            Cliente cliente = (Cliente) fila.get("cliente");
            Presupuesto pre = (Presupuesto) fila.get("presupuesto");
            Turno turno = (Turno) fila.get("turno");
            JSONObject jo = new JSONObject();
            jo.put("orden", new JSONObject(objectMapper.writeValueAsString(orden)));
            jo.put("pedido", new JSONObject(objectMapper.writeValueAsString(pedido)));
            jo.put("cliente", new JSONObject(objectMapper.writeValueAsString(cliente)));
            jo.put("presupuesto", new JSONObject(objectMapper.writeValueAsString(pre)));
            jo.put("turno", new JSONObject(objectMapper.writeValueAsString(turno)));
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

}
