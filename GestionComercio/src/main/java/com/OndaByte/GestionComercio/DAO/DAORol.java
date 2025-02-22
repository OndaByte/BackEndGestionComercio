package com.OndaByte.GestionComercio.DAO;

import com.OndaByte.GestionComercio.modelo.Permiso;
import com.OndaByte.GestionComercio.modelo.Rol;

import java.util.List;

import org.sql2o.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Sql2oException;


/**
 * DAORol
 */
public class DAORol extends GeneradorQuery<Rol> implements DAOInterface<Rol> {
    public DAORol() {
		super();
	}

    
    private static Logger logger = LogManager.getLogger(DAORol.class.getName());
    
	private String clave = "id";

	@Override
	public Class<Rol> getClase() {
        return Rol.class;
	}

	@Override
	public String getClave() {
        return this.clave;
	}

    public List<Permiso> getPermisosUsuario(int id){
		String query = "SELECT Permiso.* FROM UsuarioRol ur JOIN RolPermiso rp ON rp.rol_id = ur.rol_id JOIN Permiso ON Permiso.id = rp.permiso_id WHERE ur.usuario_id = :id";
        Connection con = null;

        try{
            con = ConexionSQL2o.getSql2o().open();
            List<Permiso> aux = con.createQuery(query).addParameter("id",id).executeAndFetch(Permiso.class);
			return aux;
        } catch (Sql2oException e) {
            logger.error("Error SQL en DAORol.getPermisosUsuario(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAORol.getPermisosUsuario(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAORol.getPermisosUsuario()");
        }
        return null;
    }


	@Override
	public boolean alta(Rol t) {
        String query;
        Connection con = null;
		try {
			query = this.getQueryAlta();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).bind(t).executeUpdate().getResult() > 0;
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAORol.alta(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAORol.alta(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAORol.alta()");
        }
        return false;
	}

	@Override
	public boolean baja(String id, boolean borrar) {
        String query;
        Connection con = null;
		try {
			query = this.getQueryBaja(borrar);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).addParameter(this.getClave(), id).executeUpdate().getResult() > 0;
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAORol.baja(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAORol.baja(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAORol.baja()");
        }
        return false;
	}

	@Override
	public boolean modificar(Rol t) {
        String query;
        Connection con = null;
		try {
			query = this.getQueryModificar();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).bind(t).executeUpdate().getResult() > 0;
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAORol.modificar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAORol.modificar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOUsuario.modificar()");
        }
        return false;
	}


    @Override
    public List<Rol> listar() {
        String query;
        Connection con = null;
		try {
			query = this.getQueryListar();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAORol.listar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAORol.listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAORol.listar()");
        }
        return null;
    }
    
	@Override
	public List<Rol> listar(String... ids) {
        String query;
        Connection con = null;
		try {
			query = this.getQueryListar(ids);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAORol.listar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAORol.listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAORol.listar()");
        }
        return null;
	}

	@Override
	public List<Rol> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones) {
        String query;
        Connection con = null;
		try {
			query = this.getQueryFiltrar(campos, valores, condiciones);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAORol.filtrar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAORol.filtrar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOUsuario.filtrar()");
        }
        return null;
	}
    

}
