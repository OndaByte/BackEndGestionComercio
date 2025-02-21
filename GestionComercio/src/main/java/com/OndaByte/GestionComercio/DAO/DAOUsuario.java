package com.OndaByte.GestionComercio.DAO;

import java.util.ArrayList;
import java.util.List;

import org.sql2o.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Sql2oException;

import com.OndaByte.GestionComercio.modelo.Usuario;


public class DAOUsuario extends GeneradorQuery<Usuario> implements DAOInterface<Usuario> {

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
        Connection con = null;
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
    
    public List<Usuario> listar() {
        String query;
        Connection con = null;
		try {
			query = this.getQueryListar();
            con = DAOSql2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOUsuario.listar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOUsuario.listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOUsuario.listar()");
        }
        return null;
    }

	@Override
	public boolean alta(Usuario t) {
        String query;
        Connection con = null;
		try {
			query = this.getQueryAlta();
            con = DAOSql2o.getSql2o().open();
            return con.createQuery(query).bind(t).executeUpdate().getResult() > 0;
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOUsuario.alta(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOUsuario.alta(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOUsuario.alta()");
        }
        return false;
	}

	@Override
	public boolean baja(String id, boolean borrar) {
        String query;
        Connection con = null;
		try {
			query = this.getQueryBaja(borrar);
            con = DAOSql2o.getSql2o().open();
            return con.createQuery(query).addParameter(this.getClave(), id).executeUpdate().getResult() > 0;
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOUsuario.baja(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOUsuario.baja(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOUsuario.baja()");
        }
        return false;
	}

	@Override
	public boolean modificar(Usuario t) {
        
        String query;
        Connection con = null;
		try {
			query = this.getQueryModificar();
            con = DAOSql2o.getSql2o().open();
            return con.createQuery(query).bind(t).executeUpdate().getResult() > 0;
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOUsuario.modificar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOUsuario.modificar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOUsuario.modificar()");
        }
        return false;
	}

	@Override
	public List<Usuario> listar(String... ids) {
        String query;
        Connection con = null;
		try {
			query = this.getQueryListar(ids);
            con = DAOSql2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOUsuario.listar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOUsuario.listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOUsuario.listar()");
        }
        return null;
	}

	@Override
	public List<Usuario> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones) {
        String query;
        Connection con = null;
		try {
			query = this.getQueryFiltrar(campos, valores, condiciones);
            con = DAOSql2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOUsuario.filtrar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOUsuario.filtrar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOUsuario.filtrar()");
        }
        return null;
	}
}
