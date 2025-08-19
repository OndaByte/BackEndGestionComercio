package com.OndaByte.GestionComercio.control;

import com.OndaByte.GestionComercio.DAO.DAOCategoria;
import com.OndaByte.GestionComercio.modelo.Categoria;
import com.OndaByte.GestionComercio.util.Parsero;
import com.OndaByte.config.Constantes;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.OndaByte.GestionComercio.util.Respuesta.buildRespuesta;

public class CategoriaControlador {

    private static Logger logger = LogManager.getLogger(CategoriaControlador.class.getName());

    public static void listar(Context ctx) {
        try {
            DAOCategoria dao = new DAOCategoria();
            List<Categoria> categorias = dao.listar();
            JSONArray data = new JSONArray(categorias);
            ctx.status(200).json(buildRespuesta(200, data.toString(), ""));
        } catch (Exception e) {
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("Listar: " + e.getMessage(), e);
        }
    }

    private static boolean validarCategoriaAlta(Categoria c){
        boolean valido = true;
        if(c.getNombre() == null || c.getNombre().isEmpty())
            valido = false;
        /*
        if(c.getPorcentaje_descuento() == null || c.getPorcentaje_descuento() < 0)
            valido = false;
        if(c.getTipo() == null || c.getTipo().isEmpty())
            valido=false;
         */
        return valido;
    }

    public static void alta(Context ctx) {
        try {
            Categoria nuevo = ctx.bodyAsClass(Categoria.class);
            if(validarCategoriaAlta(nuevo)){
                DAOCategoria dao = new DAOCategoria();
                long alta_id = dao.alta(nuevo);
                if (alta_id > -1) {
                    ctx.status(201).result(buildRespuesta(201, "{\"id\":"+alta_id+"}", "Alta exitosa"));
                } else {
                    ctx.status(500).result(buildRespuesta(500, null, "Error al dar de alta la Categoria"));
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
            Categoria nuevo = ctx.bodyAsClass(Categoria.class);
            nuevo.setId(Integer.parseInt(id));
            if(validarCategoriaAlta(nuevo)){
                DAOCategoria dao = new DAOCategoria();

                if(dao.buscar(id) == null){
                    ctx.status(404).result(buildRespuesta(404, null, "No se encontro la Categoria."));
                    return;
                }

                if (dao.modificar(nuevo)) {
                    ctx.status(201).result(buildRespuesta(201, null, "Actualizacion exitosa"));
                } else {
                    ctx.status(500).result(buildRespuesta(500, null, "Error al actualizar la Categoria"));
                }
            }else{
                ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));//mal pero no tan mal
                return;
            }
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
            DAOCategoria dao = new DAOCategoria();
            String id = ctx.pathParam("id");
            if (dao.baja(id, false)) {
                ctx.status(200).result(buildRespuesta(200, null,"Baja exitosa"));
            }else {
                ctx.status(400).result(buildRespuesta(400, null, "Error al eliminar la Categoria"));
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
            List<Categoria> categorias = filtrarBySimpleValor(filtro);
            JSONArray data = new JSONArray(categorias);
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
            DAOCategoria dao = new DAOCategoria();
            String filtro = ctx.queryParam("filtro");
            logger.debug("Filtrar:\n"+filtro);
            ArrayList<String> campos = new ArrayList<>();
            ArrayList<String> valores = new  ArrayList<>();
            ArrayList<Integer> condiciones = new ArrayList<>();
            ArrayList<Boolean> conectores = new ArrayList<>();

            if(filtro != null && !filtro.isEmpty()){
                campos = new ArrayList<>(List.of("nombre"));
                for(int i = 0 ; i< campos.size(); i++){
                    valores.add("%"+filtro+"%");
                    condiciones.add(Constantes.SQL_LIKE);
                    conectores.add(Constantes.SQL_AND);
                }
            }
            logger.debug("pag"+pagina+" cant"+cantElementos);
            HashMap<String,Object> resultDao = dao.filtrarOrdenadoYPaginado(campos,valores,condiciones,conectores,pagina,cantElementos);

            List<Categoria> filas = (List<Categoria>) resultDao.get("data");

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

    public static void filtrarDetallado(Context ctx) {
        logger.debug("FiltrarDetallado");
        try {
            Integer pagina = Parsero.safeParse(ctx.queryParam("pagina"));
            Integer cantElementos = Parsero.safeParse(ctx.queryParam("elementos"));
            DAOCategoria dao = new DAOCategoria();
            String filtro = ctx.queryParam("filtro");
            String cat = ctx.queryParam("categoria");
//            String desde = ctx.queryParam("desde");
//            String hasta = ctx.queryParam("hasta");
//            String estado = ctx.queryParam("estado");

            logger.debug("Filtrar:\n" + filtro);

            if (pagina == null || cantElementos == null) {
                ctx.status(400).result(buildRespuesta(400, null, "No ha especificado correctamente los parámetros"));
                return;
            }
            if (filtro != null && filtro.isEmpty()) filtro = null;
            if (cat != null && cat.isEmpty()) cat = null;
//            if (desde != null && desde.isEmpty()) desde = null;
//            if (hasta != null && hasta.isEmpty()) hasta = null;
//            if (estado != null && estado.isEmpty()) estado = null;

            /*
            //HashMap<String, Object> resultDao = dao.filtrarDetalladoOP(filtro, cat, pagina, cantElementos);
            //List<HashMap<String, Object>> filas = (List<HashMap<String, Object>>) resultDao.get("data");

            if (filas == null) {
                ctx.status(404).result(buildRespuesta(404, null, "No se encontraron presupuestos"));
                return;
            }


            JSONArray data = new JSONArray();
            ObjectMapper objectMapper = new ObjectMapper();

            for (HashMap<String, Object> fila : filas) {
                //Producto producto = (Producto) fila.get("producto");
                Categoria categoria = (Categoria) fila.get("categoria");

                JSONObject jo = new JSONObject();
                //jo.put("producto", new JSONObject(objectMapper.writeValueAsString(producto)));
                jo.put("categoria", new JSONObject(objectMapper.writeValueAsString(categoria)));

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
             */

        } catch (Exception e) {
            e.printStackTrace();
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
            }
            logger.error("FiltrarDetalladoPaginado: " + e.getMessage(), e);
        }
    }

    private static List<Categoria> filtrarBySimpleValor(String valor){
        DAOCategoria dao = new DAOCategoria();
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
