package com.OndaByte.GestionComercio.DAO;

import java.util.ArrayList;
import java.util.List;

import com.OndaByte.GestionComercio.modelo.Usuario;
import com.OndaByte.GestionComercio.util.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Sql2oException;

public class DAOUsuario extends ABMDAO<Usuario> {

    private String clave = "id";

    private static Logger logger = LogManager.getLogger(DAOUsuario.class.getName());

    public DAOUsuario() {
        super();
    }

    public Class<Usuario> getClase() {
        return Usuario.class;
    }

    public String getClave() {
        return this.clave;
    }

    public Usuario getUsuario(String usuario) {
        try {
            List<String> campos = new ArrayList();
            List<String> valores = new ArrayList();
            List<Integer> condiciones = new ArrayList();
            campos.add("usuario");
            valores.add(usuario);
            condiciones.add(0);
            List<Usuario> usuarios = this.filtrar(campos, valores, condiciones);
            if (usuarios.size() > 0) {
                return usuarios.get(0);
            }

        } catch (Sql2oException e) {
            logger.error("Error SQL en DAOUsuario.getUsuario(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOUsuario.getUsuario(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOUsuario.getUsuario()");
        }
        return null;
    }
    
    
    @Override
    public List<Usuario> listar() {
        try {
            logger.debug("DAOUsuario.listar");
            super.con = DAOSql2o.getSql2o().open();
            return super.listar();
        } catch (Sql2oException e) {
            logger.error("Error SQL en DAOUsuario.listar(): " + e.getMessage(), e);
            
        } catch (Exception e) {
            logger.error("Error inesperado en DAOUsuario.listar(): " + e.getMessage(), e);
            
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("DAOUsuario.close()");
        }
        return null;
    }
    
    
    
}
