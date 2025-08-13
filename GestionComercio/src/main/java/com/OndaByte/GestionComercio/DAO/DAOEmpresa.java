package com.OndaByte.GestionComercio.DAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sql2o.Query;
import org.sql2o.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.OndaByte.GestionComercio.modelo.Empresa;
import com.OndaByte.GestionComercio.modelo.Usuario;

public class DAOEmpresa {
    private String clave = "id";
    private GeneradorQuery<Empresa> generadorQueryE = new GeneradorQuery<>(Empresa.class);
    private GeneradorQuery<Usuario> generadorQueryU = new GeneradorQuery<>(Usuario.class);

    
    private static Logger logger = LogManager.getLogger(DAOEmpresa.class.getName());

    public Boolean inicializar(Empresa empresa, Usuario u){
        Connection con=null;
        
        try{
            String queryEmpresa = generadorQueryE.getQueryAlta(empresa);
            String queryUsuario = generadorQueryU.getQueryAlta(u);
            String queryAdmin = "INSERT INTO UsuarioRol (usuario_id,rol_id) VALUES (:usuario_id,:rol_id)";
            String query = "SELECT EXISTS (SELECT * FROM Empresa)";
            con = ConexionSQL2o.getSql2o().open();
            Query auxQuery = con.createQuery(query);
            
            if (auxQuery.executeAndFetchFirst(Integer.class) >= 0) {
                con.close();
                return false;
            }            
            con.close();
            
            con = ConexionSQL2o.getSql2o().beginTransaction();
            con.createQuery(queryEmpresa).bind(empresa).executeUpdate().getKey(Integer.class);
            Integer usuario_id = con.createQuery(queryUsuario).bind(u).executeUpdate().getKey(Integer.class);
            con.createQuery(queryAdmin).addParameter("usuario_id", usuario_id).addParameter("rol_id", 1).executeUpdate();
            con.commit();
            return true;
            
        } catch (Exception e) {
            logger.error("Inicializar: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("Inicializar: Conexión cerrada");
            }
        }
        return false;
    }
	
}
