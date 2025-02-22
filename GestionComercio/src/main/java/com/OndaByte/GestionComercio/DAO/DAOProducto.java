package com.OndaByte.GestionComercio.DAO;

import java.util.List;

import org.sql2o.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Sql2oException;

import com.OndaByte.GestionComercio.modelo.Producto;

public class DAOProducto extends  GeneradorQuery<Producto> implements DAOInterface<Producto>{

    private static Logger logger = LogManager.getLogger(DAOProducto.class.getName());
    
    public DAOProducto() {
		super();
	}

	private String clave = "id";


    public Class<Producto> getClase(){
        return Producto.class;
    }

    public String getClave(){return this.clave;}
    
    public boolean actualizarStock(String id, String cant){
        String query;
        Connection con = null;
		try {
			query = this.getQueryFiltarId();
            con = ConexionSQL2o.getSql2o().open();
            Producto aux = con.createQuery(query).addParameter(this.getClave(), id).executeAndFetchFirst(this.getClase());
			aux.sumarStock(Integer.parseInt(cant));
            if (aux != null){
            
				this.modificar(aux);
				return true;
            }
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOUsuario.getUsuario(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOUsuario.getUsuario(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOUsuario.getUsuario()");
        }
        return false;
    }


	@Override
	public boolean alta(Producto t) {
        String query;
        Connection con = null;
		try {
			query = this.getQueryAlta();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).bind(t).executeUpdate().getResult() > 0;
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOProducto.alta(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOProducto.alta(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOProducto.alta()");
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
            logger.error("Error SQL en DAOProducto.baja(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOProducto.baja(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOProducto.baja()");
        }
        return false;
	}

	@Override
	public boolean modificar(Producto t) {
        String query;
        Connection con = null;
		try {
			query = this.getQueryModificar();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).bind(t).executeUpdate().getResult() > 0;
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOProducto.modificar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOProducto.modificar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOUsuario.modificar()");
        }
        return false;
	}


    @Override
    public List<Producto> listar() {
        String query;
        Connection con = null;
		try {
			query = this.getQueryListar();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOProducto.listar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOProducto.listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOProducto.listar()");
        }
        return null;
    }
    
	@Override
	public List<Producto> listar(String... ids) {
        String query;
        Connection con = null;
		try {
			query = this.getQueryListar(ids);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOProducto.listar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOProducto.listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOProducto.listar()");
        }
        return null;
	}

	@Override
	public List<Producto> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones) {
        String query;
        Connection con = null;
		try {
			query = this.getQueryFiltrar(campos, valores, condiciones);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOProducto.filtrar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOProducto.filtrar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOUsuario.filtrar()");
        }
        return null;
	}
}
