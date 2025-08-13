package com.OndaByte.GestionComercio.DAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sql2o.Query;
import org.sql2o.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Sql2oException;

import com.OndaByte.GestionComercio.modelo.Usuario;

public class DAOEmpresa {
    private String clave = "id";
    private GeneradorQuery<Empresa> generadorQueryE;
    private GeneradorQuery<Usuario> generadorQueryU;

    
    private static Logger logger = LogManager.getLogger(DAOEmpresa.class.getName());

    private Boolean inicializar(Empresa e, Usuario u){
        Connection con=null;
        String queryEmpresa = generadorQueryE.getQueryAlta(e);
        String queryUsuario = generadorQueryU.getQueryAlta(u);
        String queryAdmin = "INSERT INTO UsuarioRol (usuario_id,rol_id) VALUES (:usuario_id,:rol_id)";
        try{
            con = ConexionSQL2o.getSql2o().beginTransaction();
            con.createQuery(queryEmpresa).bind(e).executeUpdate().getKey(Integer.class)
            Integer usuario_id = con.createQuery(queryUsuario).bind(u).executeUpdate().getKey(Integer.class);
            con.createQuery(queryAdmin).addParameter("usuario_id", usuario_id).addParameter("rol_id", 1).executeUpdate();
            
        } catch (Exception e) {
            logger.error("Inicializar: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("Inicializar: Conexión cerrada");
            }
        }
        return -1;
    }
	
}
