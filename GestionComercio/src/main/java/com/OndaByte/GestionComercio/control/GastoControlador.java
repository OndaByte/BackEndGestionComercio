package com.OndaByte.GestionComercio.control;
import static com.OndaByte.GestionComercio.util.Respuesta.buildRespuesta;
import com.OndaByte.GestionComercio.DAO.DAOGasto;
import com.OndaByte.GestionComercio.DAO.DAOPeriodo;
import com.OndaByte.GestionComercio.modelo.GastoFijo;
import com.OndaByte.GestionComercio.modelo.Periodo;

import java.util.ArrayList;
import java.util.List;

import io.javalin.http.Context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GastoControlador {
    private static Logger logger = LogManager.getLogger(GastoControlador.class.getName());

    public static void buscar(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            logger.debug("Buscar: "+id);
            DAOGasto dao = new DAOGasto();
            DAOPeriodo daoaux = new DAOPeriodo();
            GastoFijo gasto = dao.buscar(id);

            ArrayList<String> l1 = new ArrayList<>();
            ArrayList<String> l2 = new ArrayList<>();
            ArrayList<Integer> l3 = new ArrayList<>();
            ArrayList<Boolean> l4 = new ArrayList<>();
            l1.add("gasto_id");
            l2.add(id);
            l3.add(0);
            l4.add(false);
            
            List<Periodo> periodos = daoaux.filtrar(l1, l2, l3, l4);
            
            JSONObject data = new JSONObject();
            data.put("gasto", gasto);
            data.put("periodos", periodos);
            ctx.status(200).json(buildRespuesta(200, data.toString(), ""));
            logger.debug("Buscar res: "+200);
        } catch (Exception e) {
            logger.error("Buscar: " + e.getMessage());
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.error("Buscar res: " + e.getMessage());
            }
        }
    }
       
    public static void alta(Context ctx) {
        try {
            logger.debug("Alta:\n"+ctx.body());
            JSONObject bodyJson = new JSONObject(ctx.body()); 
            GastoFijo gfNuevo = new GastoFijo();
            Periodo pNuevo = new Periodo();

            gfNuevo.setInicio(bodyJson.getString("inicio"));
            gfNuevo.setNombre(bodyJson.getString("nombre"));
            gfNuevo.setRepeticion(bodyJson.getInt("repeticion"));

            pNuevo.setCosto(bodyJson.getFloat("costo"));
            
            if(gfNuevo.getNombre().isEmpty() || gfNuevo.getInicio().isEmpty()){
                ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
                logger.debug("Alta res: "+400);
                return;
            }            
            DAOGasto dao = new DAOGasto();
            Integer alta_id = dao.alta(gfNuevo, pNuevo);
            
            logger.debug(alta_id);
            if (alta_id > -1) {
                ctx.status(201).result(buildRespuesta(201, "{\"id\":"+alta_id+"}", "Alta exitosa"));
                logger.debug("Alta res: "+201);
            } else {
                ctx.status(500).result(buildRespuesta(500, null, "Error al dar de alta el Gasto Fijo"));
                logger.debug("Alta res: "+500);
            }
        }
        catch(JSONException e){
            ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
            logger.debug("Alta res"+400);
        }
        catch (Exception e) {
            logger.error("Alta :" + e.getMessage());
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.debug("Alta res: " + 500);
            }
        }
    }
    
    
    public static void modificar(Context ctx) {
        logger.debug("Modificar");
        try {
            logger.debug("Modificar: "+ctx.body());

            JSONObject body = new JSONObject(ctx.body());
            JSONObject periodo = body.getJSONObject("periodo");
            JSONObject gasto = body.getJSONObject("gasto");

            DAOPeriodo daoaux = new DAOPeriodo();
            Periodo pAux = daoaux.buscar(periodo.getInt("id")+"");

            if(pAux == null){
                ctx.status(404).result(buildRespuesta(404, null, "Periodo no encontrado"));
                logger.debug("Modificar res: "+404);
                return;
            }
             
            DAOGasto dao = new DAOGasto();
            GastoFijo aux = dao.buscar(gasto.getInt("id")+"");

            if(aux == null){
                ctx.status(404).result(buildRespuesta(404, null, "Gasto no encontrado"));
                logger.debug("Modificar res: "+404);
                return;
            }
            
            pAux.setCosto(periodo.getFloat("costo"));
            aux.setNombre(gasto.getString("nombre"));
            aux.setRepeticion(gasto.getInt("repeticion"));
            
            if (dao.modificar(aux) && daoaux.modificar(pAux)) {
                ctx.status(201).result(buildRespuesta(201, null, "Modificación exitosa"));
                logger.debug("Modificar res: "+201);

            }
        } catch (JSONException e){
                ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
                logger.debug("Modificar res: "+400);
                return;
            
        } catch (Exception e) {
            logger.error("Modificar: " + e.getMessage());
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.debug("Modificar res:" + e.getMessage());
            }
        }
    }

    public static void listar(Context ctx) {
        logger.debug("Listar");
        try {
            DAOGasto dao = new DAOGasto();
            List<GastoFijo> gastos = dao.listar();
            JSONArray data = new JSONArray(gastos);
            ctx.status(200).json(buildRespuesta(200, data.toString(), ""));
        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.debug("Listar res: "+ 400);
            }
            logger.error("Listar: " + e.getMessage());
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
            DAOGasto dao = new DAOGasto();
            boolean resultado = dao.baja(id, true);
            if (resultado) {
                ctx.status(200).result(buildRespuesta(201, null, "Gasto pausado con exito!"));
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
}
