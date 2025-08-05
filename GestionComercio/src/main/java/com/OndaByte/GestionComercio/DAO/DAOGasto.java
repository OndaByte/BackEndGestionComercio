package com.OndaByte.GestionComercio.DAO;
import com.OndaByte.GestionComercio.modelo.GastoFijo;
import com.OndaByte.GestionComercio.modelo.Periodo;
import com.OndaByte.GestionComercio.util.Calendario;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import org.sql2o.Connection;
import org.sql2o.data.Row;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DAOGasto implements DAOInterface<GastoFijo> {
    private String clave = "id";
    GeneradorQuery<GastoFijo> generadorQuery;
    
    private static Logger logger = LogManager.getLogger(DAOPeriodo.class.getName());

    public DAOGasto() {
        generadorQuery = new GeneradorQuery<GastoFijo>(GastoFijo.class);
        generadorQuery.setClave(clave);
    }

    public Class<GastoFijo> getClase() {
        return GastoFijo.class;
    }


    public static void generarPeriodos(LocalDate fecha_actual) {
        //Gastos y ultimo periodo
        String query = "SELECT gf.id, gf.repeticion, gf.ult_pausa, gf.estado, MAX(p.periodo) as ultimoPeriodo, p.costo FROM GastoFijo gf JOIN Periodo p ON gf.id = p.gasto_id GROUP BY gf.id";
        Connection con = null;

        try {
            con = ConexionSQL2o.getSql2o().beginTransaction();
            List<Row> filas = con.createQuery(query).executeAndFetchTable().rows();
            for(Row row : filas){
                int gasto = row.getInteger("id");
                int repeticion = row.getInteger("repeticion");
                String estado = row.getString("estado");
                float costo = row.getFloat("costo");
                
                LocalDate ultimo = ((java.sql.Date) row.getDate("ultimoPeriodo")).toLocalDate();
                
                if(estado.equals("ACTIVO")){ // Si no esta pausado
                  if(row.getDate("ult_pausa")!=null){ //Pero fue pausado alguna vez
                    ultimo = ((java.sql.Date) row.getDate("ult_pausa")).toLocalDate();
                  }

                  LocalDate sigPeriodo = Calendario.sigPeriodo(ultimo, repeticion);

                  while(!sigPeriodo.isAfter(fecha_actual)) {
                    String auxQuery = "INSERT IGNORE INTO Periodo (gasto_id, periodo, costo) VALUES (:id, :periodo, :costo)";
                    con.createQuery(auxQuery)
                      .addParameter("id", gasto)
                      .addParameter("periodo", sigPeriodo)
                      .addParameter("costo", costo)
                      .executeUpdate();
                    sigPeriodo = Calendario.sigPeriodo(sigPeriodo, repeticion);
                  }
                }                
            }            
            con.commit();            
        } catch (Exception e) {
            logger.error("GenerarPeriodos: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
            }
            logger.debug("GenerarPeriodos: Conexión cerrada");
        }
    }


    
    @Override
    public List<GastoFijo> listar() {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryListar();
            logger.debug(query);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        } catch (Exception e) {
            logger.error("Listar: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
            }
            logger.debug("Listar: Conexión cerrada");
        }
        return null;
    }
    
    @Override
    public List<GastoFijo> listar(String... ids) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryListar(ids);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        } catch (Exception e) {
            logger.error("Listar :" + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
            }
            logger.debug("Listar: Conexión cerrada");
        }
        return null;
    }

    @Override
    public List<GastoFijo> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryFiltrar(campos, valores, condiciones, conectores);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        } catch (Exception e) {
            logger.error("Filtrar: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
            }
            logger.debug("Filtrar: Conexión cerrada");
        }
        return null;
    }
    
    @Override
    public Integer alta(GastoFijo t) {
        return -1;
    }

    public Integer alta(GastoFijo t, Periodo p) {
        GeneradorQuery<GastoFijo> generadorQuery = new GeneradorQuery<>(GastoFijo.class);
        String query;
        Connection con = null;
        try {
            con = ConexionSQL2o.getSql2o().beginTransaction();
            query = generadorQuery.getQueryAlta(t);
            Integer gastoId = con.createQuery(query).bind(t).executeUpdate().getKey(Integer.class);
            
            p.setGasto_id(gastoId);
            p.setPeriodo(t.getInicio());

            query = (new GeneradorQuery<Periodo>(Periodo.class)).getQueryAlta(p);
            con.createQuery(query).bind(p).executeUpdate();
            con.commit();

            generarPeriodos(LocalDate.now());
            
            return gastoId;
        } catch (Exception e) {
            logger.error("Alta: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
            }
            logger.debug("Alta: Conexión cerrada");
        }
        return -1;
    }

    
    
    @Override
    public GastoFijo buscar(String id) {
        String query;
        Connection con = null;
        logger.debug("Buscar: "+id);
        try{
            query = generadorQuery.getQueryFiltarId();
            con = ConexionSQL2o.getSql2o().open();
            return (GastoFijo) con.createQuery(query).addParameter("id", id).executeAndFetchFirst(this.getClase());
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
    
    @Override
    public boolean baja(String id, boolean borrar) {
        String query;
        Connection con = null;
        try {
            GastoFijo gf = this.buscar(id);
            String aux = (gf.getEstado()).equals("ACTIVO") ? "INACTIVO" : "ACTIVO";
            query = "UPDATE GastoFijo SET estado=:estado, ult_pausa = CURDATE() WHERE id = :id";
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query)
              .addParameter(this.clave, id)
              .addParameter("estado", aux)
              .executeUpdate().getResult() > 0;
            //       con.createQuery("CALL sum_periodo(:id)").addParameter("id", gAux.getPeriodo_id()).executeUpdate();
        } catch (Exception e) {
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
    public boolean modificar(GastoFijo t) {
        String query;
        Connection con = null;
        logger.debug("Modificar: "+t.toString());
        try {
            con = ConexionSQL2o.getSql2o().open();
            /*   query = "SELECT EXISTS (SELECT * FROM Periodo WHERE id = :periodo_id)";
            
            Query auxQuery = con.createQuery(query).addParameter("periodo_id", t.getPeriodo_id());

            if (auxQuery.executeAndFetchFirst(Integer.class) == 0) {
                return false;
                }*/
            
            query = generadorQuery.getQueryModificar();            
            
            boolean aux = con.createQuery(query).bind(t).executeUpdate().getResult() > 0;            
            //  con.createQuery("CALL sum_periodo(:id)").addParameter("id", t.getPeriodo_id()).executeUpdate();
            return aux;
            
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


    public HashMap<String, Object> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores, Integer pagina, Integer elementos) {
        condiciones.add(0);
        conectores.add(true);
        Connection con = null;
        Integer totalElementos = null;
        Integer totalPaginas = null;
        logger.debug("Filtrar");
        try {
            HashMap<String, Object> response = new HashMap<>();
            con = ConexionSQL2o.getSql2o().open();
            String count = generadorQuery.getCountQuery(campos, valores, condiciones, conectores);

            pagina = pagina == null || pagina < 1 ? 1 : pagina;
            elementos = elementos == null || elementos < 1 ? 10 : Math.min(elementos, 1000);

            totalElementos = con.createQuery(count).executeAndFetchFirst(Integer.class);
            totalPaginas = (int) Math.ceil((double) totalElementos / elementos);

            String queryDatos = generadorQuery.getQueryFiltrarOrdenadoYPaginado(campos, valores, condiciones, conectores, "DESC");
            if (logger.isDebugEnabled()) {
                logger.debug("Filtrar: " + queryDatos);
            }

            List<GastoFijo> result = con.createQuery(queryDatos)
                    .addParameter("limit", elementos)
                    .addParameter("offset", (pagina - 1) * elementos)
                    .addParameter("orden","id")
                    .executeAndFetch(this.getClase());

            
            response.put("pagina", pagina);
            response.put("elementos", elementos);
            response.put("t_elementos", totalElementos);
            response.put("t_paginas", totalPaginas);

            if (result != null) response.put("data", result);
            return response;
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
}
