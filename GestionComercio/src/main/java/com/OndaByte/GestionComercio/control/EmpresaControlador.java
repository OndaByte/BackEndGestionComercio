package com.OndaByte.GestionComercio.control;

import static com.OndaByte.GestionComercio.util.Respuesta.buildRespuesta;

import com.OndaByte.GestionComercio.DAO.DAOEmpresa;
import com.OndaByte.GestionComercio.modelo.Empresa;
import com.OndaByte.GestionComercio.modelo.Usuario;

import io.javalin.http.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
            e.setTelefono(empresa.getString("telefono"));
            e.setEmail(empresa.getString("email"));
            e.setDireccion(empresa.getString("direccion"));

            Usuario a = new Usuario();
            a.setUsuario(admin.getString("usuario"));
            a.setContra(admin.getString("contra"));

            DAOEmpresa dao = new DAOEmpresa();
            if(dao.inicializar(e,a)){
                ctx.status(201).result(buildRespuesta(201, "", "Inicializacion exitosa"));
                logger.debug("Inicializar res: " + 201);
            } else {
                ctx.status(500).result(buildRespuesta(500, null, "Hubo un Error al Inicializar"));
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
