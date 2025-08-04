package com.OndaByte.GestionComercio.control;
import java.util.List;
import com.OndaByte.GestionComercio.DAO.DAOInsumo;
import com.OndaByte.GestionComercio.modelo.Insumo;
import com.OndaByte.GestionComercio.util.Parsero;
import static com.OndaByte.GestionComercio.util.Respuesta.buildRespuesta;

import io.javalin.http.Context;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.JSONArray;
import org.json.JSONException;

public class InsumoControlador {

    private static Logger logger = LogManager.getLogger(InsumoControlador.class.getName());

    public static void listar(Context ctx) {
        try {
            DAOInsumo dao = new DAOInsumo();
            List<Insumo> productos = dao.listar();
            JSONArray data = new JSONArray(productos);
            ctx.status(200).json(buildRespuesta(200, data.toString(), ""));
        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("Listar: " + e.getMessage(), e);
        }
    }

    private static boolean validarInsumoAlta(Insumo i){
        boolean valido = true;
        if(i.getNombre() == null || i.getNombre().isEmpty())
            valido = false;
        if(i.getPrecio() == null || i.getPrecio()< 0)
            valido = false;
        if(i.getStock() == null || i.getStock()< 0)
            valido=false;
        return valido;
    }
    
    public static void alta(Context ctx) {
        try {
            Insumo nuevo = ctx.bodyAsClass(Insumo.class);
            if(validarInsumoAlta(nuevo)){
                DAOInsumo dao = new DAOInsumo();
                long alta_id = dao.alta(nuevo);
                if (alta_id > -1) {
                    ctx.status(201).result(buildRespuesta(201, "{\"id\":"+alta_id+"}", "Alta exitosa"));
                } else {
                    ctx.status(500).result(buildRespuesta(500, null, "Error al dar de alta el insumo"));
                }
            }else{
                ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
                return;
            }
        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("Alta: " + e.getMessage(), e);
        }
    }

    public static void modificar(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            Insumo nuevo = ctx.bodyAsClass(Insumo.class);
            nuevo.setId(Integer.parseInt(id));
            if(validarInsumoAlta(nuevo)){
                DAOInsumo dao = new DAOInsumo();
                
                if(dao.buscar(id) == null){
                    ctx.status(404).result(buildRespuesta(404, null, "No se encontro el Insumo."));
                    return;
                }
                
                if (dao.modificar(nuevo)) {
                    ctx.status(201).result(buildRespuesta(201, null, "Actualizacion exitosa"));
                } else {
                    ctx.status(500).result(buildRespuesta(500, null, "Error al actualizar el insumo"));
                }
            }else{
                ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));//mal pero no tan mal 
                return;
            }
//<<<<<<< HEAD
//
//            Insumo nuevo = new Insumo();
//            nuevo.setNombre(bodyJson.getString("nombre"));
//            nuevo.setPrecio(bodyJson.getFloat("precio_costo"));
//            nuevo.setStock(bodyJson.getInt("stock"));
//            nuevo.setId(Integer.parseInt(id));
//
//            DAOInsumo dao = new DAOInsumo();
//
//            if (dao.modificar(nuevo)) {
//                ctx.status(201).result(buildRespuesta(201, null, "Actualizacion exitosa"));
//            }
//=======
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

    public static void baja(Context ctx) {
        try {
            DAOInsumo dao = new DAOInsumo();
            String id = ctx.pathParam("id");
            if (dao.baja(id, false)) {
                ctx.status(200).result(buildRespuesta(200, null,"Baja exitosa"));
            }else {
                ctx.status(400).result(buildRespuesta(400, null, "Error al eliminar el insumo"));
            }
        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("Error inesperado en InsumoControlador.baja(): " + e.getMessage(), e);
        }
    }
    
    public static void filtrar(Context ctx) {
        try {
            String filtro = ctx.queryParam("filtro"); 
            logger.debug("Filtrar:\n"+filtro);
            List<Insumo> insumos = filtrarBySimpleValor(filtro);
            JSONArray data = new JSONArray(insumos);
            ctx.status(200).json(buildRespuesta(200, data.toString(), ""));
        } catch (Exception e) {
            logger.error("Filtrar: " + e.getMessage());
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
            DAOInsumo dao = new DAOInsumo();
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
            logger.debug("pag"+pagina+" cant"+cantElementos);
            HashMap<String,Object> resultDao = dao.filtrarOrdenadoYPaginado(campos,valores,condiciones,conectores,pagina,cantElementos);

            List<Insumo> filas = (List<Insumo>) resultDao.get("data");
            
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
    
    private static List<Insumo> filtrarBySimpleValor(String valor){
        DAOInsumo dao = new DAOInsumo();
        ArrayList<String> campos = new ArrayList<>(List.of("nombre"));
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
