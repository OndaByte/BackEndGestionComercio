package com.OndaByte.GestionComercio.control;

import java.util.List;

import com.OndaByte.GestionComercio.DAO.DAORol;
import com.OndaByte.GestionComercio.DAO.DAOUsuario;
import com.OndaByte.GestionComercio.modelo.Usuario;
import com.OndaByte.GestionComercio.peticiones.LoginPost;
import com.OndaByte.GestionComercio.util.Seguridad;
import com.OndaByte.GestionComercio.util.Log;

import io.javalin.http.Context;

import org.mindrot.jbcrypt.BCrypt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UsuarioControlador {
    private static Logger logger = LogManager.getLogger(UsuarioControlador.class.getName());

    private static ObjectMapper objectMapper = new ObjectMapper();


    public static void getUsuarios(Context ctx) {
            logger.debug("UsuarioControlador.usuarios");
            DAOUsuario dao = new DAOUsuario();
            List<Usuario> usuarios = dao.listar();
            ctx.status(200).json(usuarios);
    }

    public static void login(Context ctx) {
        logger.debug("UsuarioControlador.login");
        LoginPost peticion;
        try {
            peticion = objectMapper.readValue(ctx.body(), LoginPost.class);

            //ESTO TENGO QUE MOVERLO A MANEJADOR DE EXCEPCIONES/CONTROLES
            if (peticion.getUsuario() == null || peticion.getContra() == null) {
                ctx.status(400).result("Usuario y contraseña requeridos");
                return;
            }

            DAOUsuario dao = new DAOUsuario();
            DAORol daoRol = new DAORol();
            Usuario aux = dao.getUsuario(peticion.getUsuario());

            if (aux != null && BCrypt.checkpw(peticion.getContra(), aux.getContra())) {
                ctx.status(200).result("{ \"token\" : \"" + Seguridad.getToken(aux.getUsuario()) + "\", \"permisos\" : " + daoRol.getPermisosUsuario(aux.getId()) + "}\n");
            } else {
                ctx.status(500).result("Error al loguear");
            }
        } catch (Exception e) {
            logger.error("Error en  UsuarioControlador.login " + e.getMessage());
        }
    }

    public static void cambiarContra(Context ctx) {
        logger.debug("UsuarioControlador.cambiarContra");
        String usuario = ctx.queryParam("usuario");
        String contra = ctx.queryParam("contra");
        String nueva = ctx.queryParam("nueva");

        //ESTO TENGO QUE MOVERLO A MANEJADOR DE EXCEPCIONES/CONTROLES
        if (usuario == null || contra == null) {
            ctx.status(400).result("Usuario y Contraseña requeridos");
        }

        try {
            DAOUsuario dao = new DAOUsuario();
            Usuario aux = dao.getUsuario(usuario);
            if (BCrypt.checkpw(contra, aux.getContra())) {
                aux.setContra(BCrypt.hashpw(nueva, BCrypt.gensalt()));
                if (dao.modificar(aux)) {
                    ctx.status(201).result("Contraseña actualizada");
                } else {
                    ctx.status(404).result("ERROR: No se pudo actualizar la contraseña");
                }
            } else {
                ctx.status(500).result("Error al loguear");
            }
        } catch (Exception e) {
            logger.error("Error en  UsuarioControlador.cambiarContra " + e.getMessage());
        }
    }

    public static void registrar(Context ctx) {
        LoginPost peticion;
        try {
            peticion = objectMapper.readValue(ctx.body(), LoginPost.class);
            //ESTO TENGO QUE MOVERLO A MANEJADOR DE EXCEPCIONES/CONTROLES
            if (peticion.getUsuario() == null || peticion.getContra() == null) {
                ctx.status(400).result("Usuario y Contraseña requeridos");
            }

            DAOUsuario dao = new DAOUsuario();
         //   Usuario nuevo = new Usuario();
         //   nuevo.setUsuario(peticion.getUsuario());
         //   nuevo.setContra(BCrypt.hashpw(peticion.getContra(), BCrypt.gensalt()));

            if (dao.alta(null)) {
                ctx.status(201).result("Registro exitoso");
            } else {
                ctx.status(500).result("Error al registrar");
            }
        } catch (Exception e) {
            logger.error("Error en  UsuarioControlador.registrar " + e.getMessage());
        }

    }

    public static void loginForm(Context ctx) {
      //  ctx.status(404).result("no implementado");
    }

    public static void baja(Context ctx) {
        String id = ctx.queryParam("id");
        String borrar = ctx.queryParam("borrar");

        if (id == null || borrar == null) {
            ctx.status(400).result("ID y borrar son requeridos");
            return;
        }

        try {
            DAOUsuario dao = new DAOUsuario();
            boolean resultado = dao.baja(id, Boolean.parseBoolean(borrar));
            ctx.status(200).result(String.valueOf(resultado));
        } catch (Exception e) {
            logger.error("Error en  UsuarioControlador.baja " + e.getMessage());
        }
    }
    
    public Object lecturaJSON(String msgJson){
      /*  Jackpot pozo = null;
        try { 
            //Map<String, Object> map = mapperObj.readValue(msgJson, new TypeReference<Map<String,Object>>(){});
            //String timestamp = (String) map.get("timestamp");
            //String macAddress = (String) map.get("macAddress"); 
            //String payload = mapperObj.writeValueAsString(map.get("payload")); // Obtengo payLoad
            pozo = mapperObj.readValue(msgJson, Jackpot.class);  //Creo contador
            
        } catch (JsonProcessingException ex) { 
            logger.error("Error en lectura del 'JSON' en JackpotController.parseoJSON() " + " " + ex.getMessage());       
        }*/
        return null;
    }
        
    public static String buildRespuesta(String status, String data, String mensaje) {
        return null;
    }

}
