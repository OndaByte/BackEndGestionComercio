package com.OndaByte.GestionComercio.DAO;

import com.OndaByte.GestionComercio.modelo.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sql2o.Query;
import org.sql2o.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Sql2oException;

public class DAOUsuario implements DAOInterface<Usuario> {

    private String clave = "id";
    private GeneradorQuery<Usuario> generadorQuery;
    
    private static Logger logger = LogManager.getLogger(DAOUsuario.class.getName());

    public DAOUsuario() {
        generadorQuery = new GeneradorQuery<Usuario>(Usuario.class);
        generadorQuery.setClave(clave);
    }

    public Class<Usuario> getClase() {
        return Usuario.class;
    }
    
    public Usuario getUsuario(String usuario) {
        Connection con = null;
        
        logger.debug("GetUsuario:\n"+ usuario.toString());
        try {
            List<String> campos = new ArrayList<String>();
            List<String> valores = new ArrayList<String>();
            List<Integer> condiciones = new ArrayList<Integer>();
            List<Boolean> conectores = new ArrayList<Boolean>();
            conectores.add(false);
            campos.add("usuario");
            valores.add(usuario);
            condiciones.add(0);
            List<Usuario> usuarios = this.filtrar(campos, valores, condiciones,conectores);
            if (!usuarios.isEmpty()) {
                logger.debug("GetUsuario:\n"+ usuarios.get(0).toString());
                return usuarios.get(0);
            }
        } catch (Exception e) {
            logger.error("GetUsuario: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("GetUsuario: Conexion cerrada");
            }
        }
        return null;
    }

    @Override
    public Integer alta(Usuario t) {
        String query;
        String query2 = "INSERT INTO UsuarioRol (usuario_id,rol_id) VALUES (:usuario_id,:rol_id)";
        Connection con = null;
        
        logger.debug("Alta: "+t.toString());
        try {
            query = generadorQuery.getQueryAlta(t);
            con = ConexionSQL2o.getSql2o().beginTransaction();
            Integer usuario_id = con.createQuery(query).bind(t).executeUpdate().getKey(Integer.class);
            con.createQuery(query2).addParameter("usuario_id", usuario_id).addParameter("rol_id", 3).executeUpdate();
            con.commit();
            return usuario_id;
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
            query = "SELECT EXISTS (SELECT * FROM Usuario WHERE id = :id)";
            con = ConexionSQL2o.getSql2o().open();
            Query auxQuery = con.createQuery(query).addParameter("id", id);
            
            if (auxQuery.executeAndFetchFirst(Integer.class) == 0) {
                return false;
            }
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
    public boolean modificar(Usuario t) {   String query;
        Connection con = null;
        logger.debug("Modificar: "+t.toString());
        try {
            query = generadorQuery.getQueryModificar();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).bind(t).executeUpdate().getResult() > 0;
        }  catch (Exception e) {
            logger.error("Modificar: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("Modificar: Conexion cerrada");
            }
        }
        return false;
	}

    public HashMap<String, Object> filtrarOrdenadoYPaginado(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores, Integer pagina, Integer elementos) {
        campos.add("estado");
        valores.add("ACTIVO");
        condiciones.add(0);
        conectores.add(true);
        Connection con = null;
        Integer totalElementos = null;
        Integer totalPaginas = null;
        try {
            HashMap<String, Object> response = new HashMap<>();
            con = ConexionSQL2o.getSql2o().open();
            String count = generadorQuery.getCountQuery(campos, valores, condiciones, conectores);

            pagina = pagina == null || pagina < 1 ? 1 : pagina;
            elementos = elementos == null || elementos < 1 ? 10 : Math.min(elementos, 1000);

            totalElementos = con.createQuery(count).executeAndFetchFirst(Integer.class); // Query extra
            totalPaginas = (int) Math.ceil((double) totalElementos / elementos);

            String queryDatos = generadorQuery.getQueryFiltrarOrdenadoYPaginado(campos, valores, condiciones, conectores, "ASC"); // por ahora fijo

            if (logger.isDebugEnabled()) {
                logger.debug("SELECT query DAOUsuario.filtrarOrdenadoYPaginado(): " + queryDatos);
            }

            List<Usuario> result = con.createQuery(queryDatos)
                    .addParameter("limit", elementos)
                    .addParameter("offset", (pagina - 1) * elementos)
                    .addParameter("orden","id") // por ahora fijo
                    .executeAndFetch(this.getClase());


            response.put("pagina", pagina);
            response.put("elementos", elementos);
            response.put("t_elementos", totalElementos);
            response.put("t_paginas", totalPaginas);

            if (result != null) response.put("data", result);
            return response;

        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOUsuario.filtrarOrdenadoYPaginado(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOUsuario.filtrarOrdenadoYPaginado(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Conexión cerrada después de llamar a DAOUsuario.filtrarOrdenadoYPaginado()");
        }
        return null;
    }

    @Override
    public Usuario buscar(String id) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryFiltarId();
            con = ConexionSQL2o.getSql2o().open();
            return (Usuario) con.createQuery(query).addParameter("id", id).executeAndFetchFirst(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOUsuario.buscar(id): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOUsuario.buscar(id): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Conexión cerrada después de llamar a DAOUsuario.buscar(id)");
        }
        return null;
    }

    @Override
    public List<Usuario> listar() {
        String query;
        Connection con = null;
        logger.debug("Listar");
        try {
            query = generadorQuery.getQueryListar();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Exception e) {
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
    public List<Usuario> listar(String... ids) {
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
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Listar: Conexion cerrada");
        }
        return null;
    }

    @Override
    public List<Usuario> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores) {
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

    /*
    @Override
    public Usuario buscar(String id) {
        String query;
        Connection con = null;
        logger.debug("Buscar: "+id);
        try{
            query = generadorQuery.getQueryFiltarId();
            con = ConexionSQL2o.getSql2o().open();
            return (Usuario) con.createQuery(query).addParameter("id", id).executeAndFetchFirst(this.getClase());
        } catch (Exception e) {
            logger.error("Buscar: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("Buscar: Conexion cerrada");
            }
        }
        return null;
    }

     */
}
