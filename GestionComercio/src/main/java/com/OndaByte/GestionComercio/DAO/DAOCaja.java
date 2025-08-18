package com.OndaByte.GestionComercio.DAO;
import com.OndaByte.GestionComercio.modelo.Caja;
import com.OndaByte.GestionComercio.modelo.Movimiento;
import com.OndaByte.config.Constantes;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Sql2oException;
import org.sql2o.Connection;
import org.sql2o.Query;

public class DAOCaja implements DAOInterface<Caja> {

    private static Logger logger = LogManager.getLogger(DAOCaja.class.getName());

    private GeneradorQuery<Caja> generadorQuery;
    private String clave = "id";

    
    public DAOCaja() {
        generadorQuery = new GeneradorQuery<Caja>(Caja.class);
        generadorQuery.setClave(clave);
    }
    
    public Class<Caja> getClase() {
        return Caja.class;
    }
    
    public Integer alta(Caja t){
        String query;
        Connection con = null;
        try {
            con = ConexionSQL2o.getSql2o().open();
            query = generadorQuery.getQueryAlta(t);
            logger.debug(query);

            return con.createQuery(query).bind(t).executeUpdate().getKey(Integer.class);
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
    
    public boolean baja(String id, boolean borrar){
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
    public Integer obtenerUltimaSesion(String cajaId) {
        String query = "SELECT id FROM SesionCaja WHERE caja_id = :id ORDER BY id DESC LIMIT 1";
        Connection con = null;
        try {
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query)
                    .addParameter("id", cajaId)
                    .executeAndFetchFirst(Integer.class);
        } catch (Exception e) {
            logger.error("ObtenerUltimaSesion: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                logger.debug("ObtenerUltimaSesion: Conexion cerrada.");
                con.close();
            }
        }
        return -1; // en caso de error o no existir sesión
    }

    public Integer abrir(String caja, String cajero, Float monto) throws SQLException {
        String query = "INSERT INTO SesionCaja (monto_inicial,caja_id,cajero_id) VALUES (:monto,:caja,:cajero)";
        Connection con = null;
        try {
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query)
                .addParameter("monto", monto)
                .addParameter("caja", caja)
                .addParameter("cajero", cajero)
                .executeUpdate().getKey(Integer.class);
        } catch (Sql2oException e) {
            Throwable cause = e.getCause();
            if (cause instanceof SQLException) {
                String sqlState = ((SQLException) cause).getSQLState();
                switch (sqlState) {
                    case "45000":
                        throw new SQLException("Error: Caja en uso");
                    case "45001":
                        throw new SQLException("Error: Cajero cuenta con sesion activa");
                    default:
                        logger.error("Error SQLState desconocido: " + sqlState, e);
                }
            }
        } catch (Exception e) {
            logger.error("Abrir: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                logger.debug("Abrir: Conexion cerrada.");
                con.close();
            }
        }
        return -1;
    }
    public Boolean cerrar(String caja, String cajero, Boolean esAdmin){
        String query = "UPDATE SesionCaja SET cierre=current_timestamp() WHERE caja_id=:caja "+(esAdmin ? "":"AND cajero_id=:cajero")+" AND cierre IS NULL";
        Connection con = null;
        try {
            con = ConexionSQL2o.getSql2o().open();
            Query queryAux = con.createQuery(query);
            queryAux.addParameter("caja", caja);
            
            if(!esAdmin) queryAux.addParameter("cajero", cajero);
            
            return queryAux.executeUpdate().getResult() > 0;
        } catch (Exception e) {
            logger.error("Cerrar: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                logger.debug("Cerrar: Conexion cerrada.");
                con.close();
            }
        }
        return false;
    }
    public boolean movimiento(Movimiento nuevo) throws SQLException{
        String query = "INSERT INTO Movimiento (tipo_mov,descripcion,total"+(nuevo.getCliente_id() != null ? ",cliente_id" : "")+",sesion_caja_id) VALUES (:tipo_mov,:descripcion,:total"+(nuevo.getCliente_id() != null ? ",:cliente_id" : "")+",:sesion_caja_id)";
        Connection con = null;
        try {
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query)
                .bind(nuevo)
                .executeUpdate().getResult() > 0;
        } catch (Sql2oException e) {
            Throwable cause = e.getCause();
            if (cause instanceof SQLException) {
                String sqlState = ((SQLException) cause).getSQLState();
                switch (sqlState) {
                    case "45002":
                        throw new SQLException("Error: sesion cerrada");
                    default:
                        logger.error("Error SQLState desconocido: " + sqlState, e);
                }
            }
        } catch (Exception e) {
            logger.error("Cerrar: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                logger.debug("Cerrar: Conexion cerrada.");
                con.close();
            }
        }
        return false;
    }
    public boolean modificar(Caja t){
        return false;
    }
    public Caja buscar(String id){
        return null;
    }
    public List<Caja> listar(){
        return null;
    }
    public List<Caja> listar(String... ids){
        return null;
    }
    public List<Caja> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones,List<Boolean> conectores){
        return null;
    }
    public HashMap<String, Object> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores, Integer pagina, Integer elementos) {
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
                
            totalElementos = con.createQuery(count).executeAndFetchFirst(Integer.class);
            totalPaginas = (int) Math.ceil((double) totalElementos / elementos);

            String queryDatos = generadorQuery.getQueryFiltrarOrdenadoYPaginado(campos, valores, condiciones, conectores, "ASC");
                
            List<Caja> result = con.createQuery(queryDatos)
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

        } catch (Exception e) {
            logger.error("Filtrar" + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("Filtrar: Conexion cerrada");
            }
        }
        return null;
    }
    
    public List<Movimiento> movimientosCaja(String id_caja){
        String query = "SELECT Movimiento.* FROM Movimiento JOIN Caja ON Movimiento.sesion_caja_id=Caja.sesion_actual WHERE Caja.id = :id";
        Connection con=null;
        try {
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).addParameter("id",id_caja).executeAndFetch(Movimiento.class);
        } catch (Exception e) {
            logger.error("MovimientosCaja" + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("movimientosCaja: Conexion cerrada");
            }
        }
        return null;
    }
       
    public HashMap<String, Object> movimientosCaja(List<String> campos, 
        List<String> valores, List<Integer> condiciones, List<Boolean> conectores, 
        Integer pagina, Integer elementos) {
        campos.add("estado");
        valores.add("ACTIVO");
        condiciones.add(Constantes.SQL_IGUAL);
        conectores.add(Constantes.SQL_OR);
        Connection con = null;
        String query;
        Integer totalElementos = null;
        Integer totalPaginas = null;        
        try {
            HashMap<String, Object> response = new HashMap<>();
            con = ConexionSQL2o.getSql2o().open();
            GeneradorQuery<Movimiento> genAux = new GeneradorQuery<Movimiento>(Movimiento.class);
            genAux.setClave(clave);

            String count = genAux.getCountQuery(campos, valores, condiciones, conectores);
                                   
            pagina = pagina == null || pagina < 1 ? 1 : pagina;
            elementos = elementos == null || elementos < 1 ? 10 : Math.min(elementos, 1000);
                
            totalElementos = con.createQuery(count).executeAndFetchFirst(Integer.class); // Query extra
            totalPaginas = (int) Math.ceil((double) totalElementos / elementos);

            
            String queryDatos = genAux.getQueryFiltrarOrdenadoYPaginado(campos, valores, condiciones, conectores, "DESC");
                
                
            List<Movimiento> result = con.createQuery(queryDatos)
                .addParameter("limit", elementos)
                .addParameter("offset", (pagina - 1) * elementos)
                .addParameter("orden","id")
                .executeAndFetch(Movimiento.class);
            

            response.put("pagina", pagina);
            response.put("elementos", elementos);
            response.put("t_elementos", totalElementos);
            response.put("t_paginas", totalPaginas);        

            if (result != null) response.put("data", result);
            return response;

        } catch (Exception e) {
            logger.error("MovimientosCaja" + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("movimientosCaja: Conexion cerrada");
            }
        }
        return null;
    }
    
    public HashMap<String, Object> resumenCaja(String filtro,String desde,String hasta) {
        Connection con = null;
        HashMap<String, Object> response = new HashMap<>();
        try {
            con = ConexionSQL2o.getSql2o().open();

            // Preparamos fechas si existen
            if (desde != null && hasta != null) {
                desde += " 00:00:00";
                hasta += " 23:59:59";
            }

            // Base WHERE común
            String filtroComun = " WHERE estado = 'ACTIVO'";
            if (filtro != null && !filtro.isEmpty()) {
                filtroComun += " AND descripcion LIKE :filtro";
            }
            if (desde != null && hasta != null) {
                filtroComun += " AND creado BETWEEN :desde AND :hasta";
            }

            // Total INGRESOS
            String sqlIngreso = "SELECT SUM(total) FROM Movimiento" + filtroComun + " AND tipo_mov = 'INGRESO'";
            // Total EGRESOS
            String sqlEgreso = "SELECT SUM(total) FROM Movimiento" + filtroComun + " AND tipo_mov = 'EGRESO'";
            // Total PERIODOS
            String sqlPeriodo = "SELECT SUM(costo) FROM Periodo WHERE estado = 'ACTIVO'";
            
            if (desde != null && hasta != null) {
                sqlPeriodo += " AND periodo BETWEEN :desde AND :hasta";
            }

            // Preparar consultas
            Query qIngreso = con.createQuery(sqlIngreso);
            Query qEgreso = con.createQuery(sqlEgreso);
            Query qPeriodo = con.createQuery(sqlPeriodo);

            // Cargar parámetros
            if (filtro != null && !filtro.isEmpty()) {
                qIngreso.addParameter("filtro", "%" + filtro + "%");
                qEgreso.addParameter("filtro", "%" + filtro + "%");
            }
            if (desde != null && hasta != null) {
                qIngreso.addParameter("desde", desde).addParameter("hasta", hasta);
                qEgreso.addParameter("desde", desde).addParameter("hasta", hasta);
                qPeriodo.addParameter("desde", desde).addParameter("hasta", hasta);
            }

            // Ejecutar
            Float totalIngreso = qIngreso.executeAndFetchFirst(Float.class);
            Float totalEgreso = qEgreso.executeAndFetchFirst(Float.class);
            Float totalPeriodos = qPeriodo.executeAndFetchFirst(Float.class);

            // Null-safe
            totalIngreso = totalIngreso == null ? 0 : totalIngreso;
            totalEgreso = totalEgreso == null ? 0 : totalEgreso;
            totalPeriodos = totalPeriodos == null ? 0 : totalPeriodos;

            // Armar respuesta
            response.put("total_ingresos", totalIngreso);
            response.put("total_egresos", totalEgreso);
            response.put("total_periodos", totalPeriodos);

            return response;

        } catch (Exception e) {
            logger.error("resumenCaja: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("resumenCaja: Conexión cerrada");
            }
        }
        return null;
    }
}
