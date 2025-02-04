package com.OndaByte.GestionComercio.DAO;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sql2o.Connection;

import com.OndaByte.GestionComercio.modelo.Permiso;
import com.OndaByte.GestionComercio.modelo.Rol;

/**
 * DAORol
 */
public class DAORol extends ABMDAO<Rol> {
    public DAORol(Connection con) {
		super(con);
		//TODO Auto-generated constructor stub
	}

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

        try(Connection con = DAOSql2o.getSql2o().open()){
            List<Permiso> aux = con.createQuery(query).addParameter("id",id).executeAndFetch(Permiso.class);
			return aux;
        }
      catch(Exception e) {
            Logger.getLogger(DAORol.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

}
