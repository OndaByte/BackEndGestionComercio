package com.OndaByte.GestionComercio.control;
import static com.OndaByte.GestionComercio.util.Respuesta.buildRespuesta;

import com.OndaByte.GestionComercio.DAO.DAOPeriodo;
import com.OndaByte.GestionComercio.modelo.GastoFijo;
import com.OndaByte.GestionComercio.modelo.Periodo;
import com.OndaByte.GestionComercio.util.Parsero;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.javalin.http.Context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PeriodoControlador {
    private static Logger logger = LogManager.getLogger(PeriodoControlador.class.getName());
    
    public static void alta(Context ctx) {
        DAOPeriodo dao = new DAOPeriodo();
        logger.debug("Alta:\n"+ctx.body());     
        try {
            JSONObject periodo = new JSONObject(ctx.body());

            DateTimeFormatter form = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate ld = LocalDate.parse(periodo.getString("periodo"),form);
            
            Periodo nuevo = new Periodo();
            nuevo.setPeriodo(ld.toString());
            nuevo.setCosto(periodo.getFloat("costo"));
            nuevo.setGasto_id(periodo.getInt("gasto_id"));

            Integer alta_id = -1;
            alta_id = dao.alta(nuevo);
            if (alta_id > -1) {
                ctx.status(201).result(buildRespuesta(201, "{\"id\":"+alta_id+"}", "Alta exitosa"));
                logger.debug("Alta res: " + 201);
                return;
            } else {
                ctx.status(500).result(buildRespuesta(500, null, "Error al dar de alta el periodo"));
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

public static void filtrarDetalladoPaginado(Context ctx) {
    logger.debug("FiltrarDetalladoPaginado");
    try {
        Integer pagina = Parsero.safeParse(ctx.queryParam("pagina"));
        Integer cantElementos = Parsero.safeParse(ctx.queryParam("elementos"));
        DAOPeriodo dao = new DAOPeriodo();
        String filtro = ctx.queryParam("filtro"); 
        String desde = ctx.queryParam("desde"); 
        String hasta = ctx.queryParam("hasta");

        logger.debug("Filtrar:\n" + filtro);

        if (pagina == null || cantElementos == null) {
            ctx.status(400).result(buildRespuesta(400, null, "No ha especificado correctamente los parámetros"));
            return;
        }
        if (filtro != null && filtro.isEmpty()) filtro = null;
        if (desde != null && desde.isEmpty()) desde = null;
        if (hasta != null && hasta.isEmpty()) hasta = null;

        HashMap<String, Object> resultDao = dao.filtrarDetalladoOP(filtro, desde, hasta, pagina, cantElementos);
        List<HashMap<String, Object>> filas = (List<HashMap<String, Object>>) resultDao.get("data");

        if (filas == null) {
            ctx.status(404).result(buildRespuesta(404, null, "No se encontraron presupuestos"));
            return;
        }

        JSONArray data = new JSONArray();
        ObjectMapper objectMapper = new ObjectMapper();

        for (HashMap<String, Object> fila : filas) {
            Periodo periodo = (Periodo) fila.get("periodo");
            GastoFijo gasto = (GastoFijo) fila.get("gasto");

            JSONObject jo = new JSONObject();
            jo.put("periodo", new JSONObject(objectMapper.writeValueAsString(periodo)));
            jo.put("gasto", new JSONObject(objectMapper.writeValueAsString(gasto)));

            data.put(jo);
        }

        ctx.status(200).json(buildRespuesta(
                200,
                data.toString(),
                "",
                resultDao.get("pagina") + "",
                resultDao.get("elementos") + "",
                resultDao.get("t_elementos") + "",
                resultDao.get("t_paginas") + "",
                resultDao.get("costo_total") + ""
        ));

    } catch (Exception e) {
        e.printStackTrace();
        if (ctx != null) {
            ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
        }
        logger.error("FiltrarDetalladoPaginado: " + e.getMessage(), e);
    }
}
    public static void filtrarPaginado(Context ctx) {
        logger.debug("FiltrarPaginado");
        try {
            Integer pagina = Parsero.safeParse(ctx.queryParam("pagina"));
            Integer cantElementos = Parsero.safeParse(ctx.queryParam("elementos"));
            DAOPeriodo dao = new DAOPeriodo();
            String filtro = ctx.queryParam("filtro");
            logger.debug("Filtrar:\n"+filtro);
            
            ArrayList<String> campos = new ArrayList<>();
            ArrayList<String> valores = new  ArrayList<>();
            ArrayList<Integer> condiciones = new ArrayList<>();
            ArrayList<Boolean> conectores = new ArrayList<>();

            if(filtro != null && !filtro.isEmpty()){
                campos.add("nombre");
                valores.add("%"+filtro+"%");
                condiciones.add(5);
                conectores.add(false);
            }
            HashMap<String,Object> resultDao = dao.filtrar(campos,valores,condiciones,conectores,pagina,cantElementos);

            List<Periodo> filas = (List<Periodo>) resultDao.get("data");

            if(filas == null ){
                ctx.status(404).result(buildRespuesta(404, null, "No se encontraron datos"));
                return;
            }
            JSONArray data = new JSONArray(filas);
            ctx.status(200).json(buildRespuesta(
                            200,
                            data.toString(),
                            "",
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
   
    
    public static void modificar(Context ctx) {
        logger.debug("Modificar");
        try {
            String id = ctx.pathParam("id");
            logger.debug("Modificar: "+ctx.body());
            Periodo nuevo = ctx.bodyAsClass(Periodo.class);
            
            if (id == null || id.isEmpty()) {
                ctx.status(400).result(buildRespuesta(400, null, "ID requerido"));
                logger.debug("Modificar res: "+400);
                return;
            }

            DAOPeriodo dao = new DAOPeriodo();
            Periodo aux = dao.buscar(id);

            if(aux == null){
                ctx.status(404).result(buildRespuesta(404, null, "Periodo no encontrado"));
                logger.debug("Modificar res: "+404);
                return;
            }
            nuevo.setId(aux.getId());
            nuevo.setGasto_id(aux.getGasto_id());
            nuevo.setPeriodo(aux.getPeriodo());
            
            if (dao.modificar(nuevo)) {
                ctx.status(201).result(buildRespuesta(201, null, "Modificación exitosa"));
                logger.debug("Modificar res: "+201);

            }
        } catch (Exception e) {
            logger.error("Modificar: " + e.getMessage());
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.debug("Modificar res:" + e.getMessage());
            }
        }
    }
    /*
    public static void ultimo(Context ctx){
        try {
            logger.debug("Ultimo");
            DAOPeriodo dao = new DAOPeriodo();
            DAOGasto daoaux = new DAOGasto();

            Periodo periodo = dao.ultimo();
            if(periodo == null){
                ctx.status(404).result(buildRespuesta(404,null,"No se encontraron periodos"));
                logger.debug("Ultimo res: "+404);
                return;
            }
            
            ArrayList<String> l1 = new ArrayList<>();
            ArrayList<String> l2 = new ArrayList<>();
            ArrayList<Integer> l3 = new ArrayList<>();
            ArrayList<Boolean> l4 = new ArrayList<>();
            l1.add("periodo_id");
            l2.add(periodo.getId()+"");
            l3.add(0);
            l4.add(false);
            
            List<Gasto> gastos = daoaux.filtrar(l1, l2, l3, l4);
            
            JSONObject data = new JSONObject();
            data.put("periodo", new JSONObject(periodo));
            data.put("gastos", gastos);
            ctx.status(200).json(buildRespuesta(200, data.toString(), ""));
            logger.debug("Buscar res: "+200);
        } catch (Exception e) {
            logger.error("Buscar: " + e.getMessage());
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.error("Buscar res: " + e.getMessage());
            }
        }
    }*/

    public static void baja(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            logger.debug("Baja: " +id);
            if (id == null || id.isEmpty()) {
                ctx.status(400).result(buildRespuesta(400, null, "ID"));
                logger.debug("Baja res: "+400);
                return;
            }
            DAOPeriodo dao = new DAOPeriodo();
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
}
