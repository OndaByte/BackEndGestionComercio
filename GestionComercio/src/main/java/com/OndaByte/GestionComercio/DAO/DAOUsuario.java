package com.OndaByte.GestionComercio.DAO;

import java.util.ArrayList;
import java.util.List;

import com.OndaByte.GestionComercio.modelo.Usuario;
import com.OndaByte.GestionComercio.util.Log;

public class DAOUsuario extends ABMDAO<Usuario>{
    private String clave = "id";

    public DAOUsuario(){
        super();
    }

    public Class<Usuario> getClase(){
        return Usuario.class;
    }

    public String getClave(){return this.clave;}
    
	public Usuario getUsuario(String usuario){
        try{
            List<String> campos = new ArrayList();
            List<String> valores = new ArrayList();
            List<Integer> condiciones = new ArrayList();
            campos.add("usuario");
            valores.add(usuario);
            condiciones.add(0);
            List<Usuario> usuarios = this.filtrar(campos, valores, condiciones);
            if(usuarios.size()>0){
                return usuarios.get(0);
            }
            else{
                return null;
            }
        }catch (Exception e){
            Log.log(e, DAOUsuario.class);
            return null;
        }
	}
}
