package com.OndaByte.GestionComercio.control;
 
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;

import io.jsonwebtoken.Claims;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.OndaByte.GestionComercio.DAO.DAORol;
import com.OndaByte.GestionComercio.util.Seguridad;
import static com.OndaByte.GestionComercio.util.Respuesta.buildRespuesta;

public class FiltroAutenticador {

    private static Logger logger = LogManager.getLogger(FiltroAutenticador.class.getName());
    
    public static void filtroLogin(Context ctx) {
        try{
            String aux = ctx.header("token");
            logger.debug("FiltroLogin: "+aux);
            
            DAORol dao = new DAORol();            
            Claims claims = Seguridad.validar(aux);
            String rol;
            if(claims != null){
                rol = dao.getRolUsuario(claims.getSubject()).getNombre();
                ctx.sessionAttribute("rol", rol);
                ctx.sessionAttribute("usuario",claims.getSubject());
            }
            else {
                logger.debug("FiltroLogin res: "+403);            
                throw new HttpResponseException(403, buildRespuesta(403, null, "ERROR: Autenticación Requerida"));
            }
        }
        catch(Exception e){
            throw e;
        }
    }

    public static void filtroEmpleado(Context ctx) {
        try{
            String rol = ctx.sessionAttribute("rol");
            if(!rol.equals("ADMIN") && !rol.equals("EMPLEADO")){
                logger.debug("FiltroEmpleado res: "+401);  
                throw new HttpResponseException(401, buildRespuesta(401, null, "ERROR: No autorizado"));
            }
        }
        catch(Exception e){
            throw e;
        }
    }
    
    public static void filtroAdmin(Context ctx) {
        try{

            String rol = ctx.sessionAttribute("rol");
            if(!rol.equals("ADMIN")){
                logger.debug("FiltroEmpleado res: "+401);  
                throw new HttpResponseException(401, buildRespuesta(401, null, "ERROR: No autorizado"));
            }
        }
        catch(Exception e){
            throw e;
        }
    }

}
