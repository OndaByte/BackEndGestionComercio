package com.OndaByte.GestionComercio.DAO;

import com.OndaByte.GestionComercio.modelo.Cliente;
import com.OndaByte.config.Constantes;

import java.util.HashMap;
import java.util.List;

import org.sql2o.Connection;
import org.sql2o.Sql2oException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DAOCliente implements DAOInterface<Cliente>{
    private static Logger logger = LogManager.getLogger(DAOCliente.class.getName());
    
    private String clave = "id";
    private GeneradorQuery<Cliente> generadorQuery;
    
    public DAOCliente() {
        generadorQuery = new GeneradorQuery<Cliente>(Cliente.class);
        generadorQuery.setClave(clave);
    }
    
    
    public Class<Cliente> getClase() {
        return Cliente.class;
    }
    
    @Override
    public Integer alta(Cliente t) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryAlta(t);
            logger.debug("Alta cliente : : " + query);
            logger.debug("Tostring  : " + t.toString());

            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).bind(t).executeUpdate().getKey(Integer.class);
        }  catch (Sql2oException e) {
            logger.error("Alta: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Alta: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("Alta: Conexión cerrada");
            }
        }
        return -1;
    }
    

    @Override
    public boolean baja(String id, boolean borrar) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryBaja(borrar);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).addParameter(this.clave, id).executeUpdate().getResult() > 0;
        } catch (Exception e) {
            logger.error("Baja: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                logger.debug("Baja: Conexion cerrada.");
                con.close();
            }
        }
        return false;
    }

    @Override
    public boolean modificar(Cliente t) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryModificar();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).bind(t).executeUpdate().getResult() > 0;
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOCliente.modificar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOCliente.modificar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOCliente.modificar()");
        }
        return false;
    }


    @Override
    public List<Cliente> listar() {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryListar();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOCliente.listar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOCliente.listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOCliente.listar()");
        }
        return null;
    }
    
	@Override
    public List<Cliente> listar(String... ids) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryListar(ids);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOCliente.listar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOCliente.listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOCliente.listar()");
        }
        return null;
    }
    
    @Override
    public List<Cliente> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores) {
        if (!conectores.isEmpty()) {
            conectores.set(conectores.size() - 1, Constantes.SQL_AND); // para cambiar el anterior a AND ...
        }
        campos.add("estado");
        valores.add("ACTIVO");
        condiciones.add(0);
        conectores.add(true);
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryFiltrar(campos, valores, condiciones, conectores);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOCliente.filtrar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOClientey.filtrar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOCliente.filtrar()");
        }
        return null;
    }


    public HashMap<String, Object> filtrarOrdenadoYPaginado(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores, Integer pagina, Integer elementos) {

        if (!conectores.isEmpty()) {
            conectores.set(conectores.size() - 1, Constantes.SQL_AND); // para cambiar el anterior a AND ...
        }
        campos.add("estado");
        valores.add("ACTIVO");
        condiciones.add(Constantes.SQL_IGUAL);
        conectores.add(Constantes.SQL_AND);
        Connection con = null;
        String query;
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
                logger.debug("SELECT query DAOCliente.filtrarOrdenadoYPaginado(): " + queryDatos);
            }

            List<Cliente> result = con.createQuery(queryDatos)
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
            logger.error("Error SQL en DAOCliente.filtrarOrdenadoYPaginado(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOCliente.filtrarOrdenadoYPaginado(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Conexión cerrada después de llamar a DAOCliente.filtrarOrdenadoYPaginado()");
        }
        return null;
    }

    @Override
    public Cliente buscar(String id) {
        String query;
        Connection con = null;
    	try {
            query = generadorQuery.getQueryFiltarId();
            con = ConexionSQL2o.getSql2o().open();
            return (Cliente) con.createQuery(query).addParameter("id", id).executeAndFetchFirst(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOCliente.buscar(id): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOCliente.buscar(id): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOCliente.buscar(id)");
        }
        return null;
    }
}
