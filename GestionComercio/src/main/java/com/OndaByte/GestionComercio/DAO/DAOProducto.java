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
            con = DAOSql2o.getSql2o().open();
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
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'alta'");
	}

	@Override
	public boolean baja(String id, boolean borrar) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'baja'");
	}

	@Override
	public boolean modificar(Producto t) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'modificar'");
	}

	@Override
	public List<Producto> listar() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'listar'");
	}

	@Override
	public List<Producto> listar(String... ids) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'listar'");
	}

	@Override
	public List<Producto> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'filtrar'");
	}
}
