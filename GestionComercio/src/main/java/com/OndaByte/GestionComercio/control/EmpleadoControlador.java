package com.OndaByte.GestionComercio.control;

import com.OndaByte.GestionComercio.DAO.DAOEmpleado;
import com.OndaByte.GestionComercio.modelo.Empleado;
import com.OndaByte.GestionComercio.util.Parsero;
import io.javalin.http.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.OndaByte.GestionComercio.util.Respuesta.buildRespuesta;
import com.OndaByte.config.Constantes;

public class EmpleadoControlador {
    private static Logger logger = LogManager.getLogger(EmpleadoControlador.class.getName());

    public static void listar(Context ctx) {
        logger.debug("Listar");
        try {
            DAOEmpleado dao = new DAOEmpleado();
            List<Empleado> empleados = dao.listar();
            JSONArray data = new JSONArray(empleados);
            ctx.status(200).json(buildRespuesta(200, data.toString(), ""));
            logger.debug("Listar res: "+ 200);
        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.debug("Listar res: "+ 400);
            }
            logger.error("Listar: " + e.getMessage());
        }
    }

    public static void alta(Context ctx) {
        try {
            logger.debug("Alta: "+ctx.body());
            Empleado nuevo = ctx.bodyAsClass(Empleado.class);
            if(!validarEmpleado(nuevo)){
                ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
                logger.debug("Alta res: "+400);
                return;
            }
            else {
                DAOEmpleado dao = new DAOEmpleado();
                long alta_id = dao.alta(nuevo);
                if (alta_id > -1) {
                    ctx.status(201).result(buildRespuesta(201, "{\"id\":"+alta_id+"}", "Alta exitosa"));
                    logger.debug("Alta res: "+201);
                } else {
                    ctx.status(500).result(buildRespuesta(500, null, "Error al dar de alta el cliente"));
                    logger.debug("Alta res: "+500);
                }
            }
        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.debug("Alta res: "+500);
            }
            logger.error("Alta: " + e.getMessage());
        }
    }

    public static void baja(Context ctx) {

        try {
            String id = ctx.pathParam("id");
            logger.debug("Baja: " +id);
            if (id == null || id.isEmpty()) {
                ctx.status(400).result(buildRespuesta(400, null, "ID"));
                logger.debug("Baja res: "+400);
                return;
            }
            DAOEmpleado dao = new DAOEmpleado();
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

    public static void modificar(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            logger.debug("Modificar: "+ctx.body());
            Empleado nuevo = ctx.bodyAsClass(Empleado.class);
            if (id == null || id.isEmpty()) {
                ctx.status(400).result(buildRespuesta(400, null, "ID requerido"));
                logger.debug("Modificar res: "+400);
                return;
            }

            DAOEmpleado dao = new DAOEmpleado();
            Empleado aux = dao.buscar(id);if(aux == null){
                ctx.status(404).result(buildRespuesta(404, null, "Empleado no encontrado"));
                logger.debug("Modificar res: "+404);
                return;
            }

            nuevo.setId(Integer.parseInt(id));
			
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

    public static void buscar(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            logger.debug("Buscar: "+id);
            DAOEmpleado dao = new DAOEmpleado();
            Empleado empleado = dao.buscar(id);
            JSONObject data = new JSONObject(empleado);
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

    public static void filtrar(Context ctx) {
        try {
            String filtro = ctx.queryParam("filtro");
            logger.debug("Filtrar:\n"+filtro);
            List<Empleado> empleados = filtrarBySimpleValor(filtro);
            JSONArray data = new JSONArray(empleados);
            ctx.status(200).json(buildRespuesta(200, data.toString(), ""));
        } catch (Exception e) {
            logger.error("Error en  EmpleadoControlador.filtrar" + e.getMessage());
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
        }
    }

    public static void filtrarPaginado(Context ctx) {
        logger.debug("filtrarPaginado");
        try {
            Integer pagina = Parsero.safeParse(ctx.queryParam("pagina"));
            Integer cantElementos = Parsero.safeParse(ctx.queryParam("elementos")); // cantElementos
            DAOEmpleado dao = new DAOEmpleado();
            String filtro = ctx.queryParam("filtro");
            logger.debug("Filtrar:\n"+filtro);
            ArrayList<String> campos = null;
            ArrayList<String> valores = new  ArrayList<>();
            ArrayList<Integer> condiciones = new ArrayList<>();
            ArrayList<Boolean> conectores = new ArrayList<>();
            
            if(filtro != null && !filtro.isEmpty()){
                campos = new ArrayList<>(List.of("nombre", "dni", "telefono", "direccion"));
                for(int i = 0 ; i< campos.size(); i++){
                    valores.add("%"+filtro+"%");
                    condiciones.add(Constantes.SQL_LIKE);
                    conectores.add(Constantes.SQL_OR);
                }
            }else{
                campos = new ArrayList<>();
            }


        HashMap<String,Object> resultDao = dao.filtrarOrdenadoYPaginado(campos,valores,condiciones,conectores,pagina,cantElementos);

            List<Empleado> filas = (List<Empleado>) resultDao.get("data");

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
            logger.error("Error en  PedidoControlador.listarPedidosYClientes" + e.getMessage());
        }
    }

    private static List<Empleado> filtrarBySimpleValor(String valor){
        DAOEmpleado dao = new DAOEmpleado();
        ArrayList<String> campos = new ArrayList<>(List.of("nombre", "dni", "telefono", "direccion"));
        ArrayList<String> valores = new  ArrayList<>();
        ArrayList<Integer> condiciones = new ArrayList<>();
        ArrayList<Boolean> conectores = new ArrayList<>();
        for(int i = 0 ; i<campos.size(); i++){
            valores.add("%"+valor+"%");
            condiciones.add(Constantes.SQL_LIKE);
            conectores.add(true);
        }
        return dao.filtrar(campos, valores, condiciones, conectores);
    }

    private static boolean validarEmpleado(Empleado c){
        try{
            return !(c.getNombre().isEmpty() || !c.getNombre().matches("[\\p{L} ]+") );
//|| c.getDni().isEmpty() || !c.getDni().matches("[0-9]+")
        }catch(NullPointerException e){
            return false;
        }
    }
	
}
