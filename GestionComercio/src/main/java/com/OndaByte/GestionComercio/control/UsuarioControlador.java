
package com.OndaByte.GestionComercio.control;

import com.OndaByte.GestionComercio.DAO.DAORol;
import com.OndaByte.GestionComercio.DAO.DAOUsuario;
import com.OndaByte.GestionComercio.modelo.Rol;
import com.OndaByte.GestionComercio.modelo.Usuario;
import com.OndaByte.GestionComercio.util.Parsero;
import com.OndaByte.GestionComercio.util.Seguridad;
import static com.OndaByte.GestionComercio.util.Respuesta.buildRespuesta;
import io.javalin.http.Context;
import org.mindrot.jbcrypt.BCrypt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import io.jsonwebtoken.Claims;

public class UsuarioControlador {
    private static Logger logger = LogManager.getLogger(UsuarioControlador.class.getName());

    public static void getUsuarios(Context ctx) {
        logger.debug("Usuarios");
        DAOUsuario dao = new DAOUsuario();
        List<Usuario> usuarios = dao.listar();
        JSONArray data = new JSONArray(usuarios);
        ctx.status(200).json(buildRespuesta(200, data.toString(), ""));
    }

    public static void login(Context ctx) {
        try {
            logger.debug("Login:\n" + ctx.body());

            JSONObject bodyJson = new JSONObject(ctx.body()); 
            if(!bodyJson.has("user") || bodyJson.isNull("user") || bodyJson.getString("user") == ""  || !bodyJson.getString("user").matches("[\\p{L}0-9]+")
               || !bodyJson.has("pass") || bodyJson.isNull("pass") || bodyJson.getString("pass") == "")
                {
                    ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
                    logger.debug("Login res: "+ 400);
                    return;
                }

            DAOUsuario dao = new DAOUsuario();
            DAORol daoRol = new DAORol();
            Usuario aux = dao.getUsuario(bodyJson.getString("user"));

            if (aux != null && BCrypt.checkpw(bodyJson.getString("pass"), aux.getContra())) {
                String token = Seguridad.getToken(aux.getId()+"");
                JSONArray permiso = new JSONArray(daoRol.getPermisosUsuario(aux.getId()));
                Rol rol = daoRol.getRolUsuario(aux.getId()+"");
                JSONObject data = new JSONObject();
                data.put("token", token);
                data.put("permisos",permiso);
                data.put("rol",new JSONObject(rol).toString());
                ctx.status(200).result(buildRespuesta(200, data.toString(), "Verificado con exito"));
                logger.debug("Login res: "+ 200);
            } else {
                ctx.status(403).result(buildRespuesta(403, null,"credenciales incorrectas"));
                logger.debug("Login res: "+ 403);
            }
        } catch (Exception e) {
            logger.error("Login: " + e.getMessage(), e);
            if(ctx!= null){
                ctx.status(500).result(buildRespuesta(500, null,"Error inesperado"));
                logger.debug("Login res: " + 500);
            }
        }
    }

    public static void cambiarRol(Context ctx){
        try{
            logger.debug("CambiarRol:\n" + ctx.body());
            JSONObject bodyJson = new JSONObject(ctx.body());
            if(bodyJson.getString("rol").isEmpty()){
                ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
                logger.debug("CambiarRol res: "+ 400);
                return;
            }
            String id = ctx.pathParam("id");
            String nuevoRol = bodyJson.getString("rol");
            nuevoRol = nuevoRol.toUpperCase();
            DAORol dao = new DAORol();
            int idRol = dao.buscar(nuevoRol).getId();
            
            if(dao.cambiarRol(id, idRol+"")){
                ctx.status(201).result(buildRespuesta(201, null,"Rol actualizado"));
                logger.debug("CambiarRol res: "+ 201);
            } else {
                ctx.status(404).result(buildRespuesta(500, null, "Error inesperado"));
                logger.debug("CambiarRol res: "+ 500);
            }
        }
        catch (JSONException | NullPointerException e) {
            ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
            logger.debug("CambiarRol res: "+ 400);
            return;
        }
        catch (Exception e) {
            logger.error("CambiarRol: " + e.getMessage(), e);
            if(ctx!= null){
                ctx.status(500).result(buildRespuesta(500, null,"Error inesperado"));
                logger.debug("Login res: " + 500);
            }
        }
    }
    
    public static void cambiarContra(Context ctx) {
        try {
            logger.debug("CambiarContra:\n" + ctx.body());
            JSONObject bodyJson = new JSONObject(ctx.body());
            
            Claims claims = Seguridad.validar(ctx.header("token"));
        
            String contra = bodyJson.getString("pass");
            String nueva = bodyJson.getString("nueva");

            if(contra.isEmpty() || nueva.isEmpty()) {
                ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
                logger.debug("CambiarContra res: "+ 400);
                return;
            }
             
            DAOUsuario dao = new DAOUsuario();
            Usuario aux = dao.buscar(claims.getSubject());
            if (BCrypt.checkpw(contra, aux.getContra())) {
                aux.setContra(BCrypt.hashpw(nueva, BCrypt.gensalt()));
                if (dao.modificar(aux)) {
                    ctx.status(201).result(buildRespuesta(201, null,"Contraseña actualizada"));
                    logger.debug("CambiarContra res: "+ 201);
                } else {
                    ctx.status(404).result(buildRespuesta(404, null, "ERROR: No se pudo actualizar la contraseña"));
                    logger.debug("CambiarContra res: "+ 404);
                }
            } else {
                ctx.status(401).result(buildRespuesta(401, null,"Credenciales incorrectas"));
                logger.debug("CambiarContra res: "+ 401);
            }
        } catch (JSONException e) {
            ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
            logger.debug("CambiarContra res: "+ 400);
        } catch (Exception e) {
            logger.error("CambiarContra: " + e.getMessage(), e);
            if(ctx!= null){
                ctx.status(500).result(buildRespuesta(500, null,"Error inesperado"));
                logger.debug("CambiarContra res: " + 500);
            }
        }
    }

    public static void registrar(Context ctx) {
        try {
            logger.debug("Registrar:\n" + ctx.body());
            JSONObject bodyJson = new JSONObject(ctx.body());
            System.out.println(bodyJson);

            if(!bodyJson.has("usuario")
                    || bodyJson.isNull("usuario")
                    || bodyJson.getString("usuario").isEmpty()
                    || !bodyJson.getString("usuario").matches("[\\p{L}0-9]+")
                    || !bodyJson.has("contra")
                    || bodyJson.isNull("contra")
                    || bodyJson.getString("contra").isEmpty()
               ){
                    ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
                    logger.debug("Registrar res: "+ 400);
                    return;
                }

            DAOUsuario dao = new DAOUsuario();
            Usuario nuevo = new Usuario(bodyJson.getString("usuario"),
                    BCrypt.hashpw(bodyJson.getString("contra"),
                    BCrypt.gensalt()));
            long alta_id = dao.alta(nuevo);
            if (alta_id > -1) {
                ctx.status(201).result(buildRespuesta(201, "{\"id\":"+alta_id+"}", "Registro exitoso"));
                logger.debug("Registrar res: "+ 200);
            } else {
                ctx.status(500).result(buildRespuesta(500, null,"Error al registrar"));
                logger.debug("Registrar res: "+ 500);
            }
        } catch (Exception e) {
            logger.error("Registrar: " + e.getMessage(), e);
            if(ctx!= null){
                ctx.status(500).result(buildRespuesta(500, null,"Error inesperado"));
                logger.debug("Registrar res: " + 500);
            }
        }

    }

    public static void baja(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            logger.debug("Baja: " + ctx.pathParam("id"));
            DAOUsuario dao = new DAOUsuario();
            if (!dao.baja(id, false)){
                ctx.status(404).result(buildRespuesta(404, null,"Usuario no encontrado"));
                logger.debug("Baja res: "+ 404);
                return;
            }
            ctx.status(204).result(buildRespuesta(201, null, "Baja exitosa"));
            logger.debug("Baja res: "+ 204);
        } catch (Exception e) {
            logger.error("Baja: " + e.getMessage(), e);
            if(ctx!= null){
                ctx.status(500).result(buildRespuesta(500, null,"Error inesperado"));
                logger.debug("Baja res: " + 500);
            }
        }
    }

    public static void modificar(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            logger.debug("Modificar: "+ctx.body());
            JSONObject bodyJson = new JSONObject(ctx.body());
            //Usuario nuevo = ctx.bodyAsClass(Usuario.class);
            Usuario nuevo = new Usuario();
            if (id == null || id.isEmpty()) {
                ctx.status(400).result(buildRespuesta(400, null, "ID requerido"));
                logger.debug("Modificar res: "+400);
                return;
            }

            DAOUsuario dao = new DAOUsuario();
            Usuario aux = dao.buscar(id);
            if(aux == null){
                ctx.status(404).result(buildRespuesta(404, null, "Usuario no encontrado"));
                logger.debug("Modificar res: "+404);
                return;
            }
            String usuario = bodyJson.getString("usuario");
            String contra = bodyJson.getString("contra");

            nuevo.setId(Integer.parseInt(id));
            nuevo.setUsuario(usuario);
            if (!contra.equals("")) {
                nuevo.setContra(BCrypt.hashpw(contra, BCrypt.gensalt()));
            }
            else {
                nuevo.setContra(aux.getContra());
                System.out.println("la contra no se modifica");
            }

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
            DAOUsuario dao = new DAOUsuario();
            Usuario usuario = dao.buscar(id);
            JSONObject data = new JSONObject(usuario);
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
            List<Usuario> usuarios = filtrarBySimpleValor(filtro);
            JSONArray data = new JSONArray(usuarios);
            ctx.status(200).json(buildRespuesta(200, data.toString(), ""));
        } catch (Exception e) {
            logger.error("Error en  UsuarioControlador.filtrar" + e.getMessage());
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
            DAOUsuario dao = new DAOUsuario();
            String filtro = ctx.queryParam("filtro");
            logger.debug("Filtrar:\n"+filtro);
            ArrayList<String> campos = new ArrayList<>();
            ArrayList<String> valores = new  ArrayList<>();
            ArrayList<Integer> condiciones = new ArrayList<>();
            ArrayList<Boolean> conectores = new ArrayList<>();

            if(filtro != null && !filtro.isEmpty()){
                campos.add("usuario");
                valores.add("%"+filtro+"%");
                condiciones.add(5);
                conectores.add(false);
            }
            HashMap<String,Object> resultDao = dao.filtrarOrdenadoYPaginado(campos,valores,condiciones,conectores,pagina,cantElementos);

            List<Usuario> filas = (List<Usuario>) resultDao.get("data");

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

    private static List<Usuario> filtrarBySimpleValor(String valor){
        DAOUsuario dao = new DAOUsuario();
        ArrayList<String> campos = new ArrayList<>(List.of("usuario"));
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

    private static boolean validarUsuario(Usuario u){
        try{
            return !(u.getUsuario().isEmpty() || !u.getUsuario().matches("[\\p{L} ]+") );
			
        }catch(NullPointerException e){
            return false;
        }
    }

}
