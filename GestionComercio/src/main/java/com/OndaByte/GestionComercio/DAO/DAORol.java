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
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'alta'");
	}

	@Override
	public boolean baja(String id, boolean borrar) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'baja'");
	}

	@Override
	public boolean modificar(Rol t) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'modificar'");
	}

	@Override
	public List<Rol> listar() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'listar'");
	}

	@Override
	public List<Rol> listar(String... ids) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'listar'");
	}

	@Override
	public List<Rol> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'filtrar'");
	}

}
