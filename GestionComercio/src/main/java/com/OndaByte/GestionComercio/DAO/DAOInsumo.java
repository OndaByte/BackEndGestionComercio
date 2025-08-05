package com.OndaByte.GestionComercio.DAO;

import com.OndaByte.GestionComercio.modelo.Insumo;
import java.util.HashMap;

import java.util.List;

import org.sql2o.Connection;
import org.sql2o.Sql2oException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DAOInsumo implements DAOInterface<Insumo>{
    private static Logger logger = LogManager.getLogger(DAOInsumo.class.getName());
    
    private String clave = "id";
    private GeneradorQuery<Insumo> generadorQuery;
    
    public DAOInsumo() {
        generadorQuery = new GeneradorQuery<Insumo>(Insumo.class);
        generadorQuery.setClave(clave);
    }
    
    public Class<Insumo> getClase() {
        return Insumo.class;
    }

    @Override
    public Integer alta(Insumo t) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryAlta(t);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).bind(t).executeUpdate().getKey(Integer.class);
           // return ((BigInteger) con.createQuery(query).bind(t).executeUpdate().getKey()).longValue();
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOInsumo.alta(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOInsumo.alta(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOInsumo.alta()");
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
        } catch (Sql2oException e) {
            logger.error("Error SQL en DAOInsumo.baja(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOInsumo.baja(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOInsumo.baja()");
        }
        return false;
    }

    @Override
    public boolean modificar(Insumo t) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryModificar();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).bind(t).executeUpdate().getResult() > 0;
        } catch (Sql2oException e) {
            logger.error("Error SQL en DAOInsumo.modificar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOInsumo.modificar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOInsumo.modificar()");
        }
        return false;
    }


    @Override
    public List<Insumo> listar() {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryListarOrder("nombre","ASC");
            logger.debug(query);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        } catch (Sql2oException e) {
            logger.error("Error SQL en DAOInsumo.listar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOInsumo.listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOInsumo.listar()");
        }
        return null;
    }
    
    @Override
    public List<Insumo> listar(String... ids) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryListar(ids);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOInsumo.listar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOInsumo.listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOInsumo.listar()");
        }
        return null;
    }

    @Override
    public List<Insumo> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryFiltrarOrdenadoYPaginado(campos, valores, condiciones, conectores,null);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOInsumo.filtrar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOInsumo.filtrar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOInsumo.filtrar()");
        }
        return null;
    }

    public HashMap<String, Object> filtrarOrdenadoYPaginado(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores, Integer pagina, Integer elementos) {
        campos.add("estado");
        valores.add("ACTIVO");
        condiciones.add(0);
        conectores.add(true);
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
                logger.debug("SELECT query DAOInsumo.filtrarOrdenadoYPaginado(): " + queryDatos);
            }
                
            List<Insumo> result = con.createQuery(queryDatos)
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
            logger.error("Error SQL en DAOInsumo.filtrarOrdenadoYPaginado(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOInsumo.filtrarOrdenadoYPaginado(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOInsumo.filtrarOrdenadoYPaginado()");
        }
        return null;
    }
    @Override
    public Insumo buscar(String id) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryFiltarId();
            con = ConexionSQL2o.getSql2o().open();
            return (Insumo) con.createQuery(query).addParameter("id", id).executeAndFetchFirst(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOInsumo.buscar(id): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOInsumo.buscar(id): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Conexión cerrada después de llamar a DAOInsumo.buscar(id)");
        }
        return null;
    }
}
