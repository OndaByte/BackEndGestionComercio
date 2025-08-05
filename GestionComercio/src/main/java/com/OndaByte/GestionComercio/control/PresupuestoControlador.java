
package com.OndaByte.GestionComercio.control;

import com.OndaByte.GestionComercio.DAO.ConexionSQL2o;
import com.OndaByte.GestionComercio.DAO.DAOItemPresupuesto;
import com.OndaByte.GestionComercio.DAO.DAOOrden;
import com.OndaByte.GestionComercio.DAO.DAOPedido;
import com.OndaByte.GestionComercio.DAO.DAOPresupuesto;
import com.OndaByte.GestionComercio.modelo.Cliente;
import com.OndaByte.GestionComercio.modelo.ItemPresupuesto;
import com.OndaByte.GestionComercio.modelo.Orden;
import com.OndaByte.GestionComercio.modelo.Pedido;
import com.OndaByte.GestionComercio.modelo.Presupuesto;
import com.OndaByte.GestionComercio.modelo.Turno;
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
import org.sql2o.Connection;

public class PresupuestoControlador {

    private static Logger logger = LogManager.getLogger(PresupuestoControlador.class.getName());

    public static void listar(Context ctx) {
        logger.debug("Listar");
        try {
            DAOPresupuesto dao = new DAOPresupuesto();
            List<Presupuesto> presupuestos = (List<Presupuesto>) dao.listar();
            JSONArray data = new JSONArray(presupuestos);

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
            DAOPresupuesto dao = new DAOPresupuesto();
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
                ctx.status(404).result(buildRespuesta(404, null, "No se encontraron presupuestos"));
                return;
            }

            JSONArray data = new JSONArray();
            ObjectMapper objectMapper = new ObjectMapper();

            for (HashMap<String, Object> fila : filas) {
                Presupuesto presupuesto = (Presupuesto) fila.get("presupuesto");
                Pedido pedido = (Pedido) fila.get("pedido");
                Cliente cliente = (Cliente) fila.get("cliente");
                Turno turno = (Turno) fila.get("turno");

                JSONObject jo = new JSONObject();
                jo.put("presupuesto", new JSONObject(objectMapper.writeValueAsString(presupuesto)));
                jo.put("pedido", new JSONObject(objectMapper.writeValueAsString(pedido)));
                jo.put("cliente", new JSONObject(objectMapper.writeValueAsString(cliente)));
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

    /**
     * BASICO: solamente pedidos
     */
    public static void filtrarPaginado(Context ctx) {
        logger.debug("FiltrarPaginado");
        try {
            Integer pagina = Parsero.safeParse(ctx.queryParam("pagina"));
            Integer cantElementos = Parsero.safeParse(ctx.queryParam("elementos")); // cantElementos
            DAOPresupuesto dao = new DAOPresupuesto();
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

            List<Presupuesto> filas = (List<Presupuesto>) resultDao.get("data");
            
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

    public static void actualizar(Context ctx) {
        try {  
            logger.debug("actualizar");
            String id = ctx.pathParam("id");
            JSONObject bodyJson = new JSONObject(ctx.body());
            String nuevoEstado = bodyJson.getString("estado_presupuesto").toUpperCase();
            if(!( // nuevoEstado.equals("PENDIENTE") || el pendiente es el por defecto 
               nuevoEstado.equals("APROBADO") || 
                 nuevoEstado.equals("RECHAZADO")))
                {
                    ctx.status(400).result(buildRespuesta(400, null, "Estado de Pedido Incorrecto"));
                    return;
                }

            DAOPresupuesto dao = new DAOPresupuesto();
            Presupuesto aux = dao.buscar(id);

            if (aux == null) {
                ctx.status(404).result(buildRespuesta(404, null, "No se encontro el Presupuesto requerido."));
                return;
            }
            aux.setEstado_presupuesto(nuevoEstado);
            if (dao.modificar(aux)) {
                DAOPedido daoPedido = new DAOPedido();
                Pedido p = daoPedido.buscar(aux.getPedido_id()+"");

                if (p == null) {
                    ctx.status(404).result(buildRespuesta(404, null, "No se encontro el Pedido asociado al presupuesto."));
                    return;
                }
                    //            if(aux.getEstado_pedido().equals("APROBADO") || aux.getEstado_pedido().equals("CANCELADO")){
                    //                ctx.status(400).result(buildRespuesta(400, null, "El pedido ya ha sido archivado, no puede modificar su estado actual: "+aux.getEstado_pedido()));
                    //                return;
                    //            }                
                if(aux.getEstado_presupuesto().equals("APROBADO")){
                    p.setEstado_pedido(nuevoEstado);
                    if (daoPedido.modificar(p)) {
           
                        DAOOrden daoOrden = new DAOOrden();
                        Orden orden = new Orden();                        
                        orden.setPedido_id(aux.getPedido_id());
                        orden.setDescripcion(aux.getDescripcion());
                        long orden_id = daoOrden.alta(orden);
                                                    
                        if(orden_id > -1){
                            ctx.status(201).result(buildRespuesta(201, "{\"orden_id\":"+orden_id+"}", "Actualizacion exitosa, Orden dada de alta."));
                        } else {
                            ctx.status(500).result(buildRespuesta(500, null, "Error, no se pudo dar de alta la orden."));
                        }
                    }
                }else{
                    p.setEstado_pedido("RECHAZADO");
                    if (daoPedido.modificar(p)) {
                        ctx.status(201).result(buildRespuesta(201, null, "Actualizacion exitosa, se marco el pedido y el presupuesto como rechazado."));
                    }
                }

                //todavìa no
            } else {
                ctx.status(500).result(buildRespuesta(500, null, "Error al actualizar el pedido"));
            }
            
        } catch (JSONException e) {
            logger.debug("Formulario Incorrecto: " + e.getMessage(), e);
            ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
            return;
        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("Error inesperado en PedidoControlador.actualizar(): " + e.getMessage(), e);
        }
    }
    
    public static void editar(Context ctx){
        logger.debug("PresupuestoControlador.editar");
        Connection con = null;
        try {            
            String id = ctx.pathParam("id");        
            JSONObject joBody = new JSONObject(ctx.body());
            JSONObject joPresu = joBody.getJSONObject("presupuesto");
            JSONArray jiArray = joBody.getJSONArray("items");
            Presupuesto nuevo = new Presupuesto();
            List<ItemPresupuesto> itemsP = new ArrayList<>();
            DAOPresupuesto dao = new DAOPresupuesto();
            DAOItemPresupuesto daoItemP = new DAOItemPresupuesto();
            DAOPedido daoPedido = new DAOPedido();
            
            nuevo.setId(Integer.valueOf(id));
            nuevo.setDescripcion(joPresu.getString("descripcion"));
            nuevo.setTotal(joPresu.getFloat("total"));
            
            con = ConexionSQL2o.beginTransaction(); // cerrarla siempre 

            if (dao.modificar(nuevo,con)) {
                //GENERO LOS ITEMS CON EL presupuesto_id correspondiente
                for (int i = 0; i < jiArray.length(); i++) {
                    JSONObject itemObj = jiArray.getJSONObject(i);
                    ItemPresupuesto ip = new ItemPresupuesto();
                    ip.setPresupuesto_id(Integer.parseInt(nuevo.getId() + ""));
                    ip.setCantidad(itemObj.getInt("cantidad"));
                    ip.setDescripcion(itemObj.getString("descripcion"));
                    ip.setPrecio(itemObj.getFloat("precio"));
                    itemsP.add(ip);
                }
                dao.bajaItems(con, nuevo.getId());
                logger.debug("Items dados de baja");
                
                for(ItemPresupuesto ip :itemsP){
                    long alta_id = daoItemP.alta(ip,con);
                    if (alta_id < 1) {
                        ConexionSQL2o.rollback(con);
                        ctx.status(500).result(buildRespuesta(404, null, "No se pudieron modificar los items del presupuesto correctamente."));
                        return; 
                    }
                }
                logger.debug("Items dados de alta");
                ConexionSQL2o.commit(con); //sape
                ctx.status(201).result(buildRespuesta(201, "{\"id\":" + nuevo.getId() + "}", "Modificación exitosa"));
                              
            } else {
                ConexionSQL2o.rollback(con);
                ctx.status(500).result(buildRespuesta(500, null, "Error al modificar el Presupuesto"));
            }
        } catch (JSONException e) {
            if(con!=null) { ConexionSQL2o.rollback(con); } 
            ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
            return;
        } catch (Exception e) {
            if(con!=null) { ConexionSQL2o.rollback(con); } 
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("Error en editar " + e.getMessage());
        }finally{
            if(con!=null){ 
                ConexionSQL2o.close(con); // no uso 'con' directamente pq el controller no debe saber hacerlo iHH
            }
        }
    }
    /**
     * Obtiene presupuesto y sus items 
     * @param ctx 
     */
    public static void buscarDetallado(Context ctx){
        try {
            String id = ctx.pathParam("id");
            
            DAOPresupuesto dao = new DAOPresupuesto();
            HashMap<String, Object> data = dao.buscarDetallado(id); 
            
            if(data == null){
                ctx.status(404).result(buildRespuesta(404, null, "No se encontro el Presupuesto."));
                return;
            }

            Pedido p = (Pedido) data.get("pedido");
            Cliente c = (Cliente) data.get("cliente");
            Presupuesto presupuesto = (Presupuesto) data.get("presupuesto");
            List<ItemPresupuesto> items = (List<ItemPresupuesto>) data.get("items");

            ObjectMapper objectMapper = new ObjectMapper();
            JSONObject jsonPresupuesto = new JSONObject(objectMapper.writeValueAsString(presupuesto));
            JSONObject jsonPedido = new JSONObject(objectMapper.writeValueAsString(p));
            JSONObject jsonCliente = new JSONObject(objectMapper.writeValueAsString(c));
            JSONArray jsonItems = new JSONArray(objectMapper.writeValueAsString(items));

            JSONObject respuesta = new JSONObject();
            respuesta.put("pedido", jsonPedido);
            respuesta.put("cliente", jsonCliente);
            respuesta.put("presupuesto", jsonPresupuesto);
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
        logger.debug("PresupuestoControlador.alta");
        Connection con = null;
        try {
            JSONObject joBody = new JSONObject(ctx.body());
            JSONObject joPresu = joBody.getJSONObject("presupuesto");
            JSONArray jiArray = joBody.getJSONArray("items");
            Presupuesto nuevo = new Presupuesto();
            List<ItemPresupuesto> itemsP = new ArrayList<>();
            DAOPresupuesto dao = new DAOPresupuesto();
            DAOItemPresupuesto daoItemP = new DAOItemPresupuesto();
            DAOPedido daoPedido = new DAOPedido();
            //Alta presupuesto.
            nuevo.setPedido_id(joPresu.getInt("pedido_id"));
            nuevo.setNombre(joPresu.getString("nombre"));
            nuevo.setDescripcion(joPresu.getString("descripcion"));
            nuevo.setEstado_presupuesto(joPresu.getString("estado_presupuesto"));
            nuevo.setTotal(joPresu.getFloat("total"));
            
            Pedido p = daoPedido.buscar(nuevo.getPedido_id()+"");
            if (p == null) {
                ctx.status(404).result(buildRespuesta(404, null, "No se encontro el Pedido asociado al presupuesto."));
                return;
            }
            con = ConexionSQL2o.beginTransaction(); // cerrarla siempre 
            long alta_id = dao.alta(nuevo,con);
            if (alta_id > -1) {
                //GENERO LOS ITEMS CON EL presupuesto_id correspondiente
                for (int i = 0; i < jiArray.length(); i++) {
                    JSONObject itemObj = jiArray.getJSONObject(i);
                    ItemPresupuesto ip = new ItemPresupuesto();
                    ip.setPresupuesto_id(Integer.parseInt(alta_id + ""));
                    ip.setCantidad(itemObj.getInt("cantidad"));
                    ip.setDescripcion(itemObj.getString("descripcion"));
                    ip.setPrecio(itemObj.getFloat("precio"));
                    itemsP.add(ip);
                }
                
                for(ItemPresupuesto ip :itemsP){
                    alta_id = daoItemP.alta(ip,con);
                    if (alta_id < 1) {
                        ConexionSQL2o.rollback(con);
                        ctx.status(500).result(buildRespuesta(404, null, "No se pudieron dar de alta los items del presupuesto correctamente."));
                        return; 
                    }
                }
                
                p.setEstado_pedido("PRESUPUESTADO");
                if(daoPedido.modificar(p,con)){
                    ConexionSQL2o.commit(con); //sape
                    ctx.status(201).result(buildRespuesta(201, "{\"id\":" + alta_id + "}", "Alta exitosa"));
                }else{
                    ConexionSQL2o.rollback(con);
                    logger.error("No se ha podido modificar el estado del pedido a Presupuestado");
                    ctx.status(500).result(buildRespuesta(500, null, "No se ha podido modificar el estado del pedido a Presupuestado"));
                }                    
            } else {
                ConexionSQL2o.rollback(con);
                ctx.status(500).result(buildRespuesta(500, null, "Error al dar de alta el Presupuesto"));
            }
        } catch (JSONException e) {
            if(con!=null) { ConexionSQL2o.rollback(con); } 
            ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
            return;
        } catch (Exception e) {
            if(con!=null) { ConexionSQL2o.rollback(con); } 
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("Error en  PresupuestoControlador.alta " + e.getMessage());
        }finally{
            if(con!=null){ 
                ConexionSQL2o.close(con); // no uso 'con' directamente pq el controller no debe saber hacerlo iHH
            }
        }
    }

    public static void baja(Context ctx) {
        logger.debug("PresupuestoControlador.baja");

        String id = ctx.pathParam("id");

        if (id == null || id.isEmpty()) {
            ctx.status(400).result(buildRespuesta(400, null, "ID requerido"));
            return;
        }

        try {
            DAOPresupuesto dao = new DAOPresupuesto();
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
            logger.error("Error en  PresupuestoControlador.baja " + e.getMessage());
        }
    }
}
