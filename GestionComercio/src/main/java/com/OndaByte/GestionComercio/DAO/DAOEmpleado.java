package com.OndaByte.GestionComercio.DAO;

import com.OndaByte.GestionComercio.modelo.Empleado;
import com.OndaByte.config.Constantes;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Connection;
import org.sql2o.Sql2oException;

public class DAOEmpleado implements DAOInterface<Empleado> {

    private static Logger logger = LogManager.getLogger(DAOEmpleado.class.getName());

    private String clave = "id";
    private GeneradorQuery<Empleado> generadorQuery;


    public DAOEmpleado() {
        generadorQuery = new GeneradorQuery<Empleado>(Empleado.class);
        generadorQuery.setClave(clave);
    }


    public Class<Empleado> getClase() {
        return Empleado.class;
    }


    @Override
    public Integer alta(Empleado t) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryAlta(t);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).bind(t).executeUpdate().getKey(Integer.class);
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOEmpleado.alta(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOEmpleado.alta(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Conexión cerrada después de llamar a DAOEmpleado.alta()");
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
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOEmpleado.baja(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOEmpleado.baja(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Conexión cerrada después de llamar a DAOEmpleado.baja()");
        }
        return false;
    }

    @Override
    public boolean modificar(Empleado t) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryModificar();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).bind(t).executeUpdate().getResult() > 0;
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOCEmpleado.modificar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOEmpleado.modificar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Conexión cerrada después de llamar a DAOEmpleado.modificar()");
        }
        return false;
    }

    public HashMap<String, Object> filtrarOrdenadoYPaginado(List<String> campos, List<String> valores, List<Integer> condiciones,  List<Boolean> conectores, Integer pagina, Integer elementos) {
        if (!conectores.isEmpty()) {
            conectores.set(conectores.size() - 1, Constantes.SQL_AND); // para cambiar el anterior a AND un ebola lo prefiero harkodeado xD
        }
        campos.add("estado");
        valores.add("ACTIVO");
        condiciones.add(Constantes.SQL_IGUAL);
        conectores.add(Constantes.SQL_AND);
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
                logger.debug("SELECT query DAOEmpleado.filtrarOrdenadoYPaginado(): " + queryDatos);
            }

            List<Empleado> result = con.createQuery(queryDatos)
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
            logger.error("Error SQL en DAOEmpleado.filtrarOrdenadoYPaginado(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOEmpleado.filtrarOrdenadoYPaginado(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Conexión cerrada después de llamar a DAOEmpleado.filtrarOrdenadoYPaginado()");
        }
        return null;
    }

    @Override
    public Empleado buscar(String id) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryFiltarId();
            con = ConexionSQL2o.getSql2o().open();
            return (Empleado) con.createQuery(query).addParameter("id", id).executeAndFetchFirst(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOEmpleado.buscar(id): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOEmpleado.buscar(id): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Conexión cerrada después de llamar a DAOEmpleado.buscar(id)");
        }
        return null;
    }

    @Override
    public List<Empleado> listar() {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryListar();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOEmpleado.listar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOEmpleado.listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Conexión cerrada después de llamar a DAOEmpleado.listar()");
        }
        return null;
    }

    @Override
    public List<Empleado> listar(String... ids) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryListar(ids);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOEmpleado.listar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOEmpleado.listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Conexión cerrada después de llamar a DAOEmpleado.listar()");
        }
        return null;
    }

    @Override
    public List<Empleado> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryFiltrar(campos, valores, condiciones, conectores);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOEmpleado.filtrar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOEmpleado.filtrar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Conexión cerrada después de llamar a DAOEmpleado.filtrar()");
        }
        return null;
    }
}
