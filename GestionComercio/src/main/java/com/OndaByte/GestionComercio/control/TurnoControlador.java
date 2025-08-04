
package com.OndaByte.GestionComercio.control;

import com.OndaByte.GestionComercio.DAO.ConexionSQL2o;
import com.OndaByte.GestionComercio.DAO.DAOOrden;
import com.OndaByte.GestionComercio.DAO.DAOPedido;
import com.OndaByte.GestionComercio.modelo.Orden;
import com.OndaByte.GestionComercio.modelo.Pedido;
import com.OndaByte.GestionComercio.modelo.Turno;
import com.OndaByte.GestionComercio.modelo.Cliente;
import com.OndaByte.GestionComercio.DAO.DAOTurno;
import com.OndaByte.GestionComercio.util.Parsero;
import static com.OndaByte.GestionComercio.util.Respuesta.buildRespuesta;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.sql2o.Connection;

public class TurnoControlador {

    private static Logger logger = LogManager.getLogger(TurnoControlador.class.getName());
    
    public static void listar(Context ctx){
        logger.debug("Listar");
        try{
            DAOTurno dao = new DAOTurno();
            List<Turno> turnos = dao.listar();
            JSONArray data = new JSONArray(turnos);
            ctx.status(200).json(buildRespuesta(200, data.toString(), ""));
            logger.debug("Listar res: "+200);
        } catch (Exception e) {
            logger.error("Listar" + e.getMessage(), e);
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.debug("Listar res: "+500);
            }
        }
    }

    public static void baja(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            logger.debug("Baja: " +id);
            if (id == null || id.isEmpty()) {
                ctx.status(400).result(buildRespuesta(400, null, "ID incorrecto"));
                logger.debug("Baja res: "+400);
                return;
            }
            DAOTurno dao = new DAOTurno();
            boolean resultado = dao.baja(id, false);
            if (resultado) {
                ctx.status(200).result(buildRespuesta(201, null, "Baja exitosa"));
                logger.debug("Baja res: "+201);
            } else {
                ctx.status(404).result(buildRespuesta(404, null, "No se encontró el registro"));
                logger.debug("Baja res: "+404);
            }

        } catch (Exception e) {
            logger.error("Baja: " + e.getMessage());
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.debug("Baja res: "+500);
            }
        }
    }

   
    /**
     * TODO
     */
    public static void alta(Context ctx) {
        logger.warn("Te aviso.");
    }

    /**
     * tipo: Orden o Pedido.
     * @param ctx 
     */
    public static void altaConAsignacion(Context ctx) {
        Connection con = null;
        try {
            logger.debug("Alta: "+ctx.path()+"\n"+ctx.body());
            String tipo = ctx.pathParam("tipo");
            System.out.println("TIPO:   " + tipo);
            JSONObject turnoJson = new JSONObject(ctx.body()).getJSONObject("turno");
            if( !(tipo.equals("pedido") || tipo.equals("orden")))
            {
                ctx.status(400).result(buildRespuesta(400, null, "Parametros Incorrectos"));
                logger.debug("alta: Parametros incorrectos" + tipo);
            }
            Turno nuevo = new Turno();
            //Valores por defectos
            nuevo.setFecha_inicio(turnoJson.getString("fecha_inicio"));
            nuevo.setTipo(turnoJson.getString("tipo"));
            nuevo.setFecha_fin_e(turnoJson.getString("fecha_fin_e"));
            nuevo.setEstado_turno(turnoJson.optString("estado_turno", "PEDIENTE"));
            nuevo.setObservaciones(turnoJson.optString("observaciones", null));
            nuevo.setPrioridad(turnoJson.optInt("prioridad", 1));
            nuevo.setPatron_repeticion(turnoJson.optInt("patron_repeticion", 0));

            DAOTurno dao = new DAOTurno();
            con = ConexionSQL2o.beginTransaction(); // cerrarla siempre 
            Integer id = dao.alta(nuevo,con);
            logger.debug("Alta res: " + id);
            if(id > 0){
                if(tipo.equals("pedido")){
                    int idPedido = new JSONObject(ctx.body()).getInt("pedido_id");
                    if(idPedido < 1){
                        ConexionSQL2o.rollback(con);
                        ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
                        logger.debug("alta-pedido: Formulario Incorrecto" + idPedido);
                    }       
                    DAOPedido daoPedido = new DAOPedido();
                    if(daoPedido.asignar(idPedido, id,con)){
                        ConexionSQL2o.commit(con);
                        ctx.status(201).result(buildRespuesta(201, "{\"id\":"+id+"}", "Se ha asignado el turno correctamente."));
                        logger.debug("Alta res: " + 201);
                        return;
                    } else {
                        ConexionSQL2o.rollback(con);
                        ctx.status(500).result(buildRespuesta(500, null, "Error al asignar Turno"));
                        logger.debug("Alta res: " + 500);
                        return;
                    }
                }else if(tipo.equals("orden")){
                    int idOrden = new JSONObject(ctx.body()).getInt("orden_id");
                    if(idOrden < 1){
                        ConexionSQL2o.rollback(con);
                        ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
                        logger.debug("alta-pedido: Formulario Incorrecto" + idOrden);
                        return;
                    }
                    DAOOrden daoOrden = new DAOOrden();
                    nuevo.setId(id);//importante
                    if(daoOrden.asignar(idOrden, nuevo,con)){
                        ConexionSQL2o.commit(con); //sape
                        ctx.status(201).result(buildRespuesta(201, "{\"id\":"+id+"}", "Se ha asignado el turno correctamente."));
                        logger.debug("Alta res: " + 201);
                        return;
                    } else {
                        ConexionSQL2o.rollback(con);
                        ctx.status(500).result(buildRespuesta(500, null, "Error al asignar Turno"));
                        logger.debug("Alta res: " + 500);
                        return;
                    }
                }
            } else {
                ctx.status(500).result(buildRespuesta(500, null, "Error al crear Turno"));
                logger.debug("Alta res: " + 500);
                return;
            }
        } catch (JSONException e) {
            if(con!=null) { ConexionSQL2o.rollback(con); } 
            ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
            logger.debug("TurnosPedidosEntre res: " + 400);

        } catch (Exception e) {
            if(con!=null) { ConexionSQL2o.rollback(con); } 
            logger.error("Alta: " + e.getMessage(), e);
        }finally{
            if(con!=null){ 
                ConexionSQL2o.close(con); // no uso 'con' directamente pq el controller no debe saber hacerlo iHH
            }
        }
    }

    public static void modificar(Context ctx) {
        try {
            JSONObject bodyJson = new JSONObject(ctx.body());
            logger.debug("Modificar:\n" + bodyJson.toString());

            if (!bodyJson.has("id") || bodyJson.isNull("id")) {
                logger.warn("Modificar: ID faltante");
                ctx.status(404).result(buildRespuesta(404, null, "No se encontro el ID."));
                return;
            }
            DAOTurno dao = new DAOTurno();
            int id = bodyJson.getInt("id");
            Turno actual = dao.buscar(""+id);

            if (actual == null) {
                logger.warn("Modificar: Turno no encontrado con ID " + id);
                ctx.status(404).result(buildRespuesta(404, null, "No se encontro el ID"+id+"."));
                return;
            }

            if (bodyJson.has("fechaInicio") && !bodyJson.isNull("fechaFinE")) {
                String nuevaFechaInicio = bodyJson.getString("fechaInicio");
                String nuevaFechaFin = bodyJson.getString("fechaFinE");
                actual.setFecha_inicio(nuevaFechaInicio);
                actual.setFecha_fin_e(nuevaFechaFin);
            }else{
                ctx.status(500).result(buildRespuesta(500, null, "Error de formulario al actualizar el turno."));
            }

            if (dao.modificar(actual)) {
                ctx.status(201).result(buildRespuesta(201, null, "Actualizacion exitosa"));
                return;
            } else {
                ctx.status(500).result(buildRespuesta(500, null, "Error al actualizar el turno"));
                return;
            }

            /*

            if (bodyJson.has("descripcion") && !bodyJson.isNull("descripcion")) {
            String nuevaDescripcion = bodyJson.getString("descripcion");
            if (!nuevaDescripcion.equals(actual.getDescripcion())) {
            actual.setDescripcion(nuevaDescripcion);
            modificado = true;
            }
            }
 

            //  if (!modificado) {
                //    logger.debug("Modificar: No hubo cambios");
                //    return false;
                // }

            boolean resultado = dao.modificar(actual);
            logger.debug("Modificar res: " + resultado);
            ctx.status(400).result(buildRespuesta(400, null, "Estado de Orden Incorrecto"));
            return;
             */
        } catch (Exception e) {
            logger.error("Modificar: " + e.getMessage(), e);
            ctx.status(400).result(buildRespuesta(400, null, "Estado de Orden Incorrecto"));
            return;
        }
    }
    
    /**
     * SIN USO
     */
    public static boolean fechaValida(String fecha) {
        if (fecha == null) return false;
        
        DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
        try {
            LocalDate.parse(fecha, formater);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Devuelve la la fecha del domingo y sabado de la semana, separados por un punto '.'
     * SIN USO
     */
    public static String getSemana(){
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter form = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        // Semana domingo a sabado
        LocalDate inicio = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate fin = hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
              
        return inicio.format(form)+'.'+fin.format(form);
    }
    public static void filtrarDetalladoPaginado(Context ctx) {
        logger.debug("FiltrarDetalladoPaginado");   
        try {
            Integer pagina = Parsero.safeParse(ctx.queryParam("pagina"));
            Integer cantElementos = Parsero.safeParse(ctx.queryParam("elementos"));
            DAOTurno dao = new DAOTurno();
            String filtro = ctx.queryParam("filtro"); 
            String desde = ctx.queryParam("desde"); 
            String hasta = ctx.queryParam("hasta"); 
            String tipo = ctx.queryParam("tipo"); 

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
            if (tipo != null && tipo.isEmpty()) {
                tipo = null;
            }

            HashMap<String, Object> resultDao = dao.filtrarDetalladoOP(filtro, desde, hasta, tipo, pagina, cantElementos);
            List<HashMap<String, Object>> filas = (List<HashMap<String, Object>>) resultDao.get("data");

            if (filas == null) {
                ctx.status(404).result(buildRespuesta(404, null, "No se encontraron datos"));
                return;
            }

            JSONArray data = new JSONArray();
            ObjectMapper objectMapper = new ObjectMapper();

            for (HashMap<String, Object> fila : filas) {
                Turno turno = (Turno) fila.get("turno"); 
                Orden orden = (Orden) fila.get("orden"); 
                Pedido pedido = (Pedido) fila.get("pedido");
                Cliente cliente = (Cliente) fila.get("cliente"); 

                JSONObject jo = new JSONObject();
                jo.put("turno", new JSONObject(objectMapper.writeValueAsString(turno)));
                jo.put("cliente", new JSONObject(objectMapper.writeValueAsString(cliente)));
                
                if(orden != null )
                    jo.put("orden", new JSONObject(objectMapper.writeValueAsString(orden)));
                if(pedido != null )
                    jo.put("pedido", new JSONObject(objectMapper.writeValueAsString(pedido)));
                
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
    /**
     * BASICO: solamente turnos
     * SIN USO
     */    
    public static void filtrarPaginado(Context ctx) {
        logger.debug("FiltrarPaginado");   
        try {
            Integer pagina = Parsero.safeParse(ctx.queryParam("pagina"));
            Integer cantElementos = Parsero.safeParse(ctx.queryParam("elementos"));
            String filtro = ctx.queryParam("filtro"); 
            String desde = ctx.queryParam("desde"); 
            String hasta = ctx.queryParam("hasta"); 
            String estado = ctx.queryParam("estado"); 
            logger.debug("filtrarPaginado -filtro:\n" + filtro);
            logger.debug("filtrarPaginado -desde:\n" + desde);
            logger.debug("filtrarPaginado -hasta:\n" + hasta);
            logger.debug("filtrarPaginado -estado:\n" + estado);
            DAOTurno dao = new DAOTurno();
            ArrayList<String> campos = new ArrayList<>();
            ArrayList<String> valores = new  ArrayList<>();
            ArrayList<Integer> condiciones = new ArrayList<>();  
            ArrayList<Boolean> conectores = new ArrayList<>();

            if (pagina == null || cantElementos == null) {
                ctx.status(400).result(buildRespuesta(400, null, "No ha especificado correctamente los parámetros"));
                return;
            }
            if (filtro != null && !filtro.isEmpty()) {
                campos.add("descripcion");
                valores.add("%"+filtro+"%");
                condiciones.add(5);
                conectores.add(false);
            }else{
                filtro = null;
            }
            if (desde != null && !desde.isEmpty()) {
                campos.add("fecha_inicio");
                valores.add(desde);
                condiciones.add(2);
                conectores.add(false);
            }else{
                desde = null;
            }
            if (hasta != null && !hasta.isEmpty()) {
                campos.add("fecha_inicio");
                valores.add("%"+filtro+"%");
                condiciones.add(3);
                conectores.add(false);
            }else{
                hasta = null;
            }
            //            if (estado != null && !estado.isEmpty()) {
            //                campos.add("fecha_fin");
            //                valores.add(null);
            //                condiciones.add(6);//isNOT NULL//5);
            //                conectores.add(false);
            //            }else{
            //                estado = null;
            //            }
            
            
            HashMap<String,Object> resultDao = dao.filtrarOP(campos,valores,condiciones,conectores,pagina,cantElementos);
            
            List<Turno> filas = (List<Turno>) resultDao.get("data");

            if (filas == null) {
                ctx.status(404).result(buildRespuesta(404, null, "No se encontraron datos"));
                return;
            }

            JSONArray data = new JSONArray();
            ObjectMapper objectMapper = new ObjectMapper();

            for (Turno turno : filas) {
                JSONObject jo = new JSONObject();
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
            logger.error("FiltrarPaginado: " + e.getMessage(), e);
        }
    }

    public static void cantEstado(Context ctx){
        logger.debug("cantEstado");
        try {
            String estado = ctx.queryParam("estado");
            DAOTurno dao = new DAOTurno();
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
}
