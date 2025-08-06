package com.OndaByte.GestionComercio.control;
import java.util.List;

import com.OndaByte.GestionComercio.DAO.DAOProducto;
import com.OndaByte.GestionComercio.modelo.Producto;
import com.OndaByte.GestionComercio.util.Parsero;
import static com.OndaByte.GestionComercio.util.Respuesta.buildRespuesta;

import io.javalin.http.Context;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.JSONArray;
import org.json.JSONException;

public class ProductoControlador {

    private static Logger logger = LogManager.getLogger(ProductoControlador.class.getName());

    public static void listar(Context ctx) {
        try {
            DAOProducto dao = new DAOProducto();
            List<Producto> productos = dao.listar();
            JSONArray data = new JSONArray(productos);
            ctx.status(200).json(buildRespuesta(200, data.toString(), ""));
        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("Listar: " + e.getMessage(), e);
        }
    }

    private static boolean validarProductoAlta(Producto p){
        boolean valido = true;
        if(p.getNombre() == null || p.getNombre().isEmpty())
            valido = false;
        if(p.getPrecio() == null || p.getPrecio()< 0)
            valido = false;
        if(p.getStock() == null || p.getStock()< 0)
            valido=false;
        return valido;
    }
    
    public static void alta(Context ctx) {
        try {
            Producto nuevo = ctx.bodyAsClass(Producto.class);
            if(validarProductoAlta(nuevo)){
                DAOProducto dao = new DAOProducto();
                long alta_id = dao.alta(nuevo);
                if (alta_id > -1) {
                    ctx.status(201).result(buildRespuesta(201, "{\"id\":"+alta_id+"}", "Alta exitosa"));
                } else {
                    ctx.status(500).result(buildRespuesta(500, null, "Error al dar de alta el producto"));
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
            Producto nuevo = ctx.bodyAsClass(Producto.class);
            nuevo.setId(Integer.parseInt(id));
            if(validarProductoAlta(nuevo)){
                DAOProducto dao = new DAOProducto();
                
                if(dao.buscar(id) == null){
                    ctx.status(404).result(buildRespuesta(404, null, "No se encontro el Producto."));
                    return;
                }
                
                if (dao.modificar(nuevo)) {
                    ctx.status(201).result(buildRespuesta(201, null, "Actualizacion exitosa"));
                } else {
                    ctx.status(500).result(buildRespuesta(500, null, "Error al actualizar el Producto"));
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
            DAOProducto dao = new DAOProducto();
            String id = ctx.pathParam("id");
            if (dao.baja(id, false)) {
                ctx.status(200).result(buildRespuesta(200, null,"Baja exitosa"));
            }else {
                ctx.status(400).result(buildRespuesta(400, null, "Error al eliminar el Producto"));
            }
        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("Error inesperado en baja(): " + e.getMessage(), e);
        }
    }
    
    public static void filtrar(Context ctx) {
        try {
            String filtro = ctx.queryParam("filtro"); 
            logger.debug("Filtrar:\n"+filtro);
            List<Producto> productos = filtrarBySimpleValor(filtro);
            JSONArray data = new JSONArray(productos);
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
            DAOProducto dao = new DAOProducto();
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

            List<Producto> filas = (List<Producto>) resultDao.get("data");
            
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
            logger.error("Error en  listarPedidosYClientes" + e.getMessage());
        }
    }
    
    private static List<Producto> filtrarBySimpleValor(String valor){
        DAOProducto dao = new DAOProducto();
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
