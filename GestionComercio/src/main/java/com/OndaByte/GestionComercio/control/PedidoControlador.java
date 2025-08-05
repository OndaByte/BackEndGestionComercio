package com.OndaByte.GestionComercio.control;

import com.OndaByte.GestionComercio.modelo.Orden;
import com.OndaByte.GestionComercio.modelo.Pedido;
import com.OndaByte.GestionComercio.modelo.Cliente;
import com.OndaByte.GestionComercio.DAO.DAOOrden;
import com.OndaByte.GestionComercio.DAO.DAOPedido;
import com.OndaByte.GestionComercio.modelo.Presupuesto;
import com.OndaByte.GestionComercio.modelo.Turno;
import com.OndaByte.GestionComercio.util.Parsero;

import static com.OndaByte.GestionComercio.util.Respuesta.buildRespuesta;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.http.Context;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PedidoControlador {

    private static Logger logger = LogManager.getLogger(PedidoControlador.class.getName());

    public static void listar(Context ctx) {
        logger.debug("Listar");
        try {
            DAOPedido dao = new DAOPedido();
            List<Pedido> clientes = dao.listar();
            JSONArray data = new JSONArray(clientes);

            ctx.status(200).json(buildRespuesta(200, data.toString(), ""));
            logger.debug("Listar res: "+200);
            return;

        } catch (Exception e) {
            logger.error("Listar: " + e.getMessage());
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.debug("Listar res: "+500);
                return;
            }
        }
    } 

    public static void modificar(Context ctx) {
        try {
            logger.debug("Modificar Pedido");
            String id = ctx.pathParam("id");
            JSONObject modificarJson = new JSONObject(ctx.body());

            if (id == null || id.isEmpty()) {
                ctx.status(400).result(buildRespuesta(400, null, "ID requerido"));
                logger.debug("Modificar res: 400");
                return;
            }

            // --- Buscar pedido original ---
            DAOPedido dao = new DAOPedido();
            Pedido aux = dao.buscar(id);

            if (aux == null) {
                ctx.status(404).result(buildRespuesta(404, null, "No se encontró el Pedido requerido."));
                logger.debug("Modificar res: 404");
                return;
            }

            if ("APROBADO".equals(aux.getEstado_pedido()) || "CANCELADO".equals(aux.getEstado_pedido())) {
                ctx.status(400).result(buildRespuesta(400, null, "El pedido ya ha sido archivado, no puede modificar su estado actual: " + aux.getEstado_pedido()));
                logger.debug("Modificar res: 400");
                return;
            }

            // --- Actualizar campos del pedido ---
            if (modificarJson.has("descripcion") && !modificarJson.isNull("descripcion")) {
                aux.setDescripcion(modificarJson.getString("descripcion"));
            }

            if (modificarJson.has("fecha_fin_estimada") && !modificarJson.isNull("fecha_fin_estimada")) {
                aux.setFecha_fin_estimada(modificarJson.getString("fecha_fin_estimada"));
            }

            // --- Guardar cambios ---
            if (dao.modificar(aux)) {
                ctx.status(201).result(buildRespuesta(201, null, "Actualización exitosa"));
                logger.debug("Modificar res: 201");
            } else {
                ctx.status(500).result(buildRespuesta(500, null, "Error al actualizar el pedido"));
                logger.debug("Modificar res: 500");
            }

        } catch (JSONException e) {
            logger.error("Modificar JSON error: " + e.getMessage(), e);
            ctx.status(400).result(buildRespuesta(400, null, "Formulario incorrecto"));
        } catch (Exception e) {
            logger.error("Modificar pedido error: " + e.getMessage(), e);
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
        }
    }
    
    public static void actualizar(Context ctx)  {
        try {
            logger.debug("Actualizar");
            String id = ctx.pathParam("id");
            JSONObject bodyJson = new JSONObject(ctx.body());
            //"PENDIENTE","APROBADO","PRESUPUESTADO","CANCELADO"
            String estadoaux = bodyJson.getString("estado_pedido").toUpperCase();
            if(!(estadoaux.equals("PENDIENTE") || 
                 estadoaux.equals("APROBADO") || 
                 estadoaux.equals("PRESUPUESTADO") || 
                 estadoaux.equals("CANCELADO")))
                {
                    ctx.status(400).result(buildRespuesta(400, null, "Estado de Pedido Incorrecto"));
                    return;
                }
            
            DAOPedido dao = new DAOPedido();
            Pedido aux = dao.buscar(id);

            if(aux == null){
                ctx.status(404).result(buildRespuesta(404, null, "No se encontro el Pedido requerido."));
                return;
            }
            if(aux.getEstado_pedido().equals("APROBADO") || aux.getEstado_pedido().equals("CANCELADO")){
                ctx.status(400).result(buildRespuesta(400, null, "El pedido ya ha sido archivado, no puede modificar su estado actual: "+aux.getEstado_pedido()));
                return;
            }
            aux.setEstado_pedido(estadoaux);
            
            if(estadoaux.equals("APROBADO")){
                DAOOrden daoaux = new DAOOrden();
                Orden ordenaux = new ObjectMapper().readValue(bodyJson.getJSONObject("orden").toString(),Orden.class);
                ordenaux.setPedido_id(aux.getId());
                long orden_id = daoaux.alta(ordenaux,bodyJson.getInt("turno_id")+"");
                
                if(orden_id > -1){
                    ctx.status(201).result(buildRespuesta(201, "{\"orden_id\":"+orden_id+"}", "Actualizacion exitosa, Orden dada de alta."));
                } else {
                    ctx.status(500).result(buildRespuesta(500, null, "Error al actualizar el pedido, no se pudo dar de alta la orden."));
                }
                return;
            }
            if (dao.modificar(aux)) {
                ctx.status(201).result(buildRespuesta(201, null, "Actualizacion exitosa"));
            } else {
                ctx.status(500).result(buildRespuesta(500, null, "Error al actualizar el pedido"));
            }
		}
        catch (JSONException e){
            logger.error(e.getMessage(), e);
            ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
            return;
        }
        catch (Exception e) {
            logger.error("Actualizar: " + e.getMessage(), e);
            if(ctx!= null){
                ctx.status(500).result(buildRespuesta(500, null,"Error inesperado"));
            }
        }
    }
    
    public static void alta(Context ctx){
        logger.debug("Alta:\n"+ctx.body());
        try {
            JSONObject aux = new JSONObject(ctx.body());
            JSONObject pedido = (JSONObject) aux.get("pedido");
            
//            Integer turno_id = TurnoControlador.alta((JSONObject) aux.get("turno"));
//            if(turno_id == -1){
//                ctx.status(400).result(buildRespuesta(400, null, "Error al dar de alta el turno"));
//                logger.debug("Alta res: " + 400);
//                return;
//            }
           
            
            Pedido nuevo = new Pedido();
            nuevo.setCliente_id(pedido.getInt("cliente_id"));
            nuevo.setDescripcion(pedido.getString("descripcion"));
            nuevo.setEstado_pedido("PENDIENTE");
            if (pedido.has("fecha_fin_estimada") && !pedido.isNull("fecha_fin_estimada") && !pedido.getString("fecha_fin_estimada").isEmpty()){
                nuevo.setFecha_fin_estimada(pedido.getString("fecha_fin_estimada"));
            }
            

            DAOPedido dao = new DAOPedido();
            long alta_id = dao.alta(nuevo);
            if (alta_id > -1) {
                ctx.status(201).result(buildRespuesta(201, "{\"id\":"+alta_id+"}", "Alta exitosa"));
                logger.debug("Alta res: " + 201);
                return;
            } else {
                ctx.status(500).result(buildRespuesta(500, null, "Error al dar de alta el pedido"));
                logger.debug("Alta res: " + 500);
                return;
            }
        }
        catch (JSONException e){
            logger.error("Alta :" + e.getMessage());
            ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
            logger.debug("Alta res:" + 400);
            return;
        }
        catch (Exception e) {
            logger.error("Alta :" + e.getMessage());
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.debug("Alta res: " + 500);
            }
        }
    }

    public static void baja(Context ctx) {
        logger.debug("Baja");

        String id = ctx.pathParam("id");
        logger.debug("ID de baja testeado: " + id);
        if (id == null || id.isEmpty()) {
            ctx.status(400).result(buildRespuesta(400, null, "ID requerido"));
            return;
        }

        try {
            DAOPedido dao = new DAOPedido();
            boolean resultado = dao.baja(id, false);
            if (resultado) {
                ctx.status(200).result(buildRespuesta(201, null, "Baja exitosa"));
            } else {
                ctx.status(404).result(buildRespuesta(404, null, "No se encontró el registro"));
            }

        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("Baja " + e.getMessage());
        }
    }
    
    public static void filtrar(Context ctx) {
        try {
            String filtro = ctx.queryParam("filtro"); 
            logger.debug("Filtrar:\n"+filtro);
            List<Pedido> pedidos = filtrarBySimpleValor(filtro);
            JSONArray data = new JSONArray(pedidos);
            ctx.status(200).json(buildRespuesta(200, data.toString(), ""));
        } catch (Exception e) {
            logger.error("Filtrar" + e.getMessage());
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
        }
    }
    /**
     * BASICO: solamente pedidos
     */
    public static void filtrarPaginado(Context ctx) {
        logger.debug("FiltrarPaginado");
        try {
            Integer pagina = Parsero.safeParse(ctx.queryParam("pagina"));
            Integer cantElementos = Parsero.safeParse(ctx.queryParam("elementos")); // cantElementos
            DAOPedido dao = new DAOPedido();
            String filtro = ctx.queryParam("filtro");  
            logger.debug("Filtrar:\n"+filtro);
            ArrayList<String> campos = new ArrayList<>();
            ArrayList<String> valores = new  ArrayList<>();
            ArrayList<Integer> condiciones = new ArrayList<>();  
            ArrayList<Boolean> conectores = new ArrayList<>();
            
            if(filtro != null && !filtro.isEmpty()){
                campos.add("descripcion");
                valores.add("%"+filtro+"%");
                condiciones.add(5);
                conectores.add(false);
            }
            HashMap<String,Object> resultDao = dao.filtrarOP(campos,valores,condiciones,conectores,pagina,cantElementos);

            List<Pedido> filas = (List<Pedido>) resultDao.get("data");
            
            if(filas == null ){
                ctx.status(404).result(buildRespuesta(404, null, "No se encontraron datos"));
                return;
            }
            JSONArray data = new JSONArray(filas);
            ctx.status(200).json(buildRespuesta(
                    200, 
                    data.toString(), 
                    "",//mensaje
                    resultDao.get("pagina") + "",
                    resultDao.get("elementos")+ "",
                    resultDao.get("t_elementos") + "",
                    resultDao.get("t_paginas")+ ""
                )
            );

        } catch (Exception e) {
            e.printStackTrace();
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("FiltrarPaginado: " + e.getMessage());
        }
    }
    
    public static void filtrarDetalladoPaginado(Context ctx) {
        logger.debug("FiltrarDetalladoPaginado");   
        try {
            Integer pagina = Parsero.safeParse(ctx.queryParam("pagina"));
            Integer cantElementos = Parsero.safeParse(ctx.queryParam("elementos"));
            DAOPedido dao = new DAOPedido();
            String filtro = ctx.queryParam("filtro"); 
            String desde = ctx.queryParam("desde"); 
            String hasta = ctx.queryParam("hasta"); 
            String estado = ctx.queryParam("estado"); 

            logger.debug("Filtrar:\n" + filtro);

            if (pagina == null || cantElementos == null) {
                ctx.status(400).result(buildRespuesta(400, null, "No ha especificado correctamente los parámetros"));
                return;
            }
            if (filtro != null && filtro.isEmpty()) {
                filtro = null;
            }
            if (desde != null && desde.isEmpty()) {
                desde = null;
            }
            if (hasta != null && hasta.isEmpty()) {
                hasta = null;
            }
            if (estado != null && estado.isEmpty()) {
                estado = null;
            }

            HashMap<String, Object> resultDao = dao.filtrarDetalladoOP(filtro, desde, hasta, estado, pagina, cantElementos);
            List<HashMap<String, Object>> filas = (List<HashMap<String, Object>>) resultDao.get("data");

            if (filas == null) {
                ctx.status(404).result(buildRespuesta(404, null, "No se encontraron datos"));
                return;
            }

            JSONArray data = new JSONArray();
            ObjectMapper objectMapper = new ObjectMapper();

            for (HashMap<String, Object> fila : filas) {
                Pedido pedido = (Pedido) fila.get("pedido");
                Cliente cliente = (Cliente) fila.get("cliente"); 
                Turno turno = (Turno) fila.get("turno"); 
                Presupuesto presupuesto = (Presupuesto) fila.get("presupuesto"); 
                JSONObject jo = new JSONObject();
                jo.put("pedido", new JSONObject(objectMapper.writeValueAsString(pedido)));
                jo.put("cliente", new JSONObject(objectMapper.writeValueAsString(cliente)));
                jo.put("turno", new JSONObject(objectMapper.writeValueAsString(turno)));
                jo.put("presupuesto", new JSONObject(objectMapper.writeValueAsString(presupuesto)));

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
    private static List<Pedido> filtrarBySimpleValor(String valor){
        DAOPedido dao = new DAOPedido();
        ArrayList<String> campos = new ArrayList<>(List.of("descripcion"));//faltan mas atriburos
        ArrayList<String> valores = new  ArrayList<>();
        ArrayList<Integer> condiciones = new ArrayList<>();
        ArrayList<Boolean> conectores = new ArrayList<>();
        for(int i = 0 ; i<campos.size(); i++){
            valores.add("%"+valor+"%");
            condiciones.add(5);
            conectores.add(true);
        }
        return dao.filtrar(campos, valores, condiciones, conectores);
    }
}
