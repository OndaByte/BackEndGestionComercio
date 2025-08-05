package com.OndaByte.GestionComercio.DAO;

import com.OndaByte.GestionComercio.modelo.Permiso;
import com.OndaByte.GestionComercio.modelo.Rol;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.sql2o.Connection;

public class DAORol implements DAOInterface<Rol> {
    private static Logger logger = LogManager.getLogger(DAORol.class.getName());
    
    private String clave = "id";
    private GeneradorQuery<Rol> generadorQuery;
    
    public DAORol() {
        generadorQuery = new GeneradorQuery<Rol>(Rol.class);
        generadorQuery.setClave(clave);
    }
    
    public Class<Rol> getClase() {
        return Rol.class;
    }

    
    public Rol getRolUsuario(String id){
        String query = "SELECT Rol.* FROM UsuarioRol ur JOIN Rol ON Rol.id = ur.rol_id WHERE ur.usuario_id = :id";
        Connection con = null;
        logger.debug("RolUsuario: "+id);
        try{
            con = ConexionSQL2o.getSql2o().open();
            return (Rol) con.createQuery(query).addParameter("id",id).executeAndFetchFirst(this.getClase());
        } catch (Exception e) {
            logger.error("RolUsuario: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("RolUsuario: Conexion cerrada");
            }
        }
        return null;
    }
    
    public List<Permiso> getPermisosUsuario(int id){
        String query = "SELECT Permiso.* FROM UsuarioRol ur JOIN RolPermiso rp ON rp.rol_id = ur.rol_id JOIN Permiso ON Permiso.id = rp.permiso_id WHERE ur.usuario_id = :id";
        Connection con = null;
        logger.debug("PermisosUsuario: "+id);
        try{
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).addParameter("id",id).executeAndFetch(Permiso.class);
        } catch (Exception e) {
            logger.error("PermisosUsuario: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("PermisosUsuario: Conexion cerrada");
            }
        }
        return null;
    }


    @Override
    public Integer alta(Rol t) {
        String query;
        Connection con = null;
        logger.debug("Alta: "+t.toString());
        try {
            query = generadorQuery.getQueryAlta(t);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).bind(t).executeUpdate().getKey(Integer.class);
        }  catch (Exception e) {
            logger.error("Alta: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("Alta: Conexion cerrada");
            }
        }
        return -1;
    }

    @Override
    public boolean baja(String id, boolean borrar) {
        String query;
        Connection con = null;
        logger.debug("Baja: "+id);
        try {
            query = generadorQuery.getQueryBaja(borrar);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).addParameter(this.clave, id).executeUpdate().getResult() > 0;
        }  catch (Exception e) {
            logger.error("Baja: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("Baja: Conexion cerrada");
            }
        }
        return false;
    }

    @Override
    public boolean modificar(Rol t) {
        String query;
        Connection con = null;
        
        logger.debug("Modificar: "+t.toString());
        try {
            query = generadorQuery.getQueryModificar();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).bind(t).executeUpdate().getResult() > 0;
        } catch (Exception e) {
            logger.error("Modificar: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("Modificar: Conexion cerrada");
            }
        }
        return false;
    }


    @Override
    public List<Rol> listar() {
        String query;
        Connection con = null;
        logger.debug("Listar");
        try {
            query = generadorQuery.getQueryListar();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        } catch (Exception e) {
            logger.error("Listar: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
            logger.debug("Listar: Conexion cerrada");
            }
        }
        return null;
    }
    
    @Override
    public List<Rol> listar(String... ids) {
        String query;
        Connection con = null;
        logger.debug("Listar: "+ids.toString());
        try {
            query = generadorQuery.getQueryListar(ids);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        } catch (Exception e) {
            logger.error("Listar: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("Listar: Conexion cerrada");
            }
        }
        return null;
    }

    @Override
    public List<Rol> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones,List<Boolean> conectores) {
        String query;
        Connection con = null;
        logger.debug("Filtrar:\nCampos: "+campos.toString()+"\nValores: "+valores.toString()+"\nCondiciones: "+condiciones.toString() + "\nConectores: "+ conectores.toString());
        try {
            query = generadorQuery.getQueryFiltrar(campos, valores, condiciones, conectores);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        } catch (Exception e) {
            logger.error("Filtrar: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("Filtrar: Conexion cerrada");
            }
        }
        return null;
    }

	public boolean cambiarRol(String idUsuario, String idRol) {
        String query = "UPDATE UsuarioRol SET rol_id=:rol_id WHERE usuario_id=:usuario_id";
        Connection con = null;
        logger.debug("CambiarRol: "+idUsuario+" "+idRol);
        try{
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query)
                .addParameter("rol_id",idRol)
                .addParameter("usuario_id",idUsuario)
                .executeUpdate().getResult() > 0;
        } catch (Exception e) {
            logger.error("CambiarRol: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("CambiarRol: Conexion cerrada");
            }
        }
        return false;
	}

	@Override
	public Rol buscar(String nombre) {
        String query = "SELECT * FROM Rol WHERE nombre=:nombre";
        Connection con = null;
        try{
            con = ConexionSQL2o.getSql2o().open();
            return (Rol) con.createQuery(query).addParameter("nombre",nombre).executeAndFetchFirst(this.getClase());
       } catch (Exception e) {
            logger.error("CambiarRol: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("CambiarRol: Conexion cerrada");
            }
        }
        return null;
	}
}
