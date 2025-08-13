package com.OndaByte.GestionComercio.control;

import static com.OndaByte.GestionComercio.util.Respuesta.buildRespuesta;

import com.OndaByte.GestionComercio.modelo.Usuario;
import com.OndaByte.config.Constantes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.javalin.http.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EmpresaControlador {
    
    private static Logger logger = LogManager.getLogger(EmpresaControlador.class.getName());

    public static void inicializar(Context ctx){
        try{
            logger.debug("Inicializar:\n"+ ctx.body());            
            JSONObject bodyJson = new JSONObject(ctx.body());
            JSONObject empresa = bodyJson.getJSONObject("empresa");
            JSONObject admin = bodyJson.getJSONObject("admin");
            
            Empresa e = new Empresa();
            e.setNombre(empresa.getString("nombre"));            
            e.setNombre(empresa.getString("telefono"));
            e.setNombre(empresa.getString("email"));
            e.setNombre(empresa.getString("direccion"));

            Usuario a = new Usuario();
            a.setUsuario(admin.getString("usuario"));
            a.setContra(admin.getString("contra"));

            DAOEmpresa dao = new DAOEmpresa();
            if(dao.inicializar(e,a)){
                ctx.status(201).result(buildRespuesta(201, "", "Inicializacion exitosa"));
                logger.debug("Inicializar res: " + 201);
            } else {
                ctx.status(500).result(buildRespuesta(500, null, "Inicializar:"));
                logger.debug("Inicializar res: " + 500);
            }
            
        } catch (JSONException e) {
            ctx.status(400).result(buildRespuesta(400, null, "Formulario Incorrecto"));
            logger.debug("Inicializar res: " + 400);
        } catch (Exception e) {
            logger.error("Inicializar :" + e.getMessage());
            if (ctx != null) {
                ctx.status(500).result(buildRespuesta(500, null, "Error inesperado"));
                logger.debug("Inicializar res: " + 500);
            }
        }
    }
}
