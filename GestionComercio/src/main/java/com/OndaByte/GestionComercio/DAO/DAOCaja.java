package com.OndaByte.GestionComercio.DAO;

import com.OndaByte.GestionComercio.modelo.Caja;
import com.OndaByte.GestionComercio.modelo.Movimiento;
import com.OndaByte.GestionComercio.modelo.SesionCaja;
import com.OndaByte.config.Constantes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Sql2oException;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.data.Row;
import org.sql2o.data.Table;

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

    public Integer alta(Caja t) {
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
//    
//    public Integer ultimaCaja(String caja, String cajero) throws SQLException {
//        String query = "INSERT INTO SesionCaja (monto_inicial,caja_id,cajero_id) VALUES (:monto,:caja,:cajero)";
//        Connection con = null;
//        try {
//            con = ConexionSQL2o.getSql2o().open();
//            return con.createQuery(query)
//                .addParameter("monto", monto)
//                .addParameter("caja", caja)
//                .addParameter("cajero", cajero)
//                .executeUpdate().getKey(Integer.class);
//        } catch (Sql2oException e) {
//            Throwable cause = e.getCause();
//            if (cause instanceof SQLException) {
//                String sqlState = ((SQLException) cause).getSQLState();
//                switch (sqlState) {
//                    case "45000":
//                        throw new SQLException("Error: Caja en uso");
//                    case "45001":
//                        throw new SQLException("Error: Cajero cuenta con sesion activa");
//                    default:
//                        logger.error("Error SQLState desconocido: " + sqlState, e);
//                }
//            }
//        } catch (Exception e) {
//            logger.error("Abrir: " + e.getMessage(), e);
//        } finally {
//            if (con != null) {
//                logger.debug("Abrir: Conexion cerrada.");
//                con.close();
//            }
//        }
//        return -1;
//    }

    public Boolean cerrar(String caja, String cajero, Boolean esAdmin) {
        String query = "UPDATE SesionCaja SET cierre=current_timestamp() WHERE caja_id=:caja " + (esAdmin ? "" : "AND cajero_id=:cajero") + " AND cierre IS NULL";
        Connection con = null;
        try {
            con = ConexionSQL2o.getSql2o().open();
            Query queryAux = con.createQuery(query);
            queryAux.addParameter("caja", caja);

            if (!esAdmin) {
                queryAux.addParameter("cajero", cajero);
            }

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

    public boolean movimiento(Movimiento nuevo) throws SQLException {
        String query = "INSERT INTO Movimiento (tipo_mov,descripcion,total" + (nuevo.getCliente_id() != null ? ",cliente_id" : "") + ",sesion_caja_id) VALUES (:tipo_mov,:descripcion,:total" + (nuevo.getCliente_id() != null ? ",:cliente_id" : "") + ",:sesion_caja_id)";
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

    public boolean modificar(Caja t) {
        return false;
    }

    public Caja buscar(String id) {
        return null;
    }

    public List<Caja> listar() {
        return null;
    }

    public List<Caja> listar(String... ids) {
        return null;
    }

    public List<Caja> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores) {
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
                    .addParameter("orden", "id") // por ahora fijo
                    .executeAndFetch(this.getClase());

            response.put("pagina", pagina);
            response.put("elementos", elementos);
            response.put("t_elementos", totalElementos);
            response.put("t_paginas", totalPaginas);

            if (result != null) {
                response.put("data", result);
            }
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

    /**
     * Lista sesiones de caja con filtros + totales agregados de movimientos.
     *
     * @param desde Fecha desde (apertura) en formato "YYYY-MM-DD" (opcional)
     * @param hasta Fecha hasta (apertura) en formato "YYYY-MM-DD" (opcional)
     * @param cajaId Filtra por caja_id (opcional)
     * @param cajeroId Filtra por cajero_id (opcional)
     * @param estado "ABIERTA" (cierre IS NULL), "CERRADA" (cierre IS NOT NULL)
     * o null para todas
     * @param pagina Nº de página (1..n)
     * @param tamPag Tamaño de página (1..1000)
     * @return HashMap con data + paginado
     */
    public HashMap<String, Object> filtrarSesionesCajaOP(String desde,
            String hasta,
            String estado,
            Integer pagina,
            Integer tamPag) {
        
        String select
                = "SELECT "
                + " s.id AS sid, "
                + " s.monto_inicial AS smonto_inicial, "
                + " s.monto_final AS smonto_final, "
                + " s.apertura AS sapertura, "
                + " s.cierre AS scierre, "
                + " s.cajero_id AS scajero_id, "
                + " s.caja_id AS scaja_id ";

        String from
                = " FROM SesionCaja s ";
//                + " LEFT JOIN ( "
//                + "   SELECT sesion_caja_id, "
//                + "          SUM(CASE WHEN tipo_mov IN ('VENTA','INGRESO') THEN total ELSE 0 END) AS total_ingresos, "
//                + "          SUM(CASE WHEN tipo_mov = 'EGRESO' THEN total ELSE 0 END) AS total_egresos "
//                + "   FROM Movimiento "
//                + "   WHERE estado = 'ACTIVO' "
//                + "   GROUP BY sesion_caja_id "
//                + " ) ms ON ms.sesion_caja_id = s.id ";

        String where = " WHERE 1=1 ";

        if (desde != null && hasta != null) {
            where += " AND (s.apertura BETWEEN :desde AND :hasta) ";
        }
//        if (cajaId != null) {
//            where += " AND s.caja_id = :cajaId ";
//        }
//        if (cajeroId != null) {
//            where += " AND s.cajero_id = :cajeroId ";
//        }
        if (estado != null) {
            if ("ABIERTA".equalsIgnoreCase(estado)) {
                where += " AND s.cierre IS NULL ";
            } else if ("CERRADA".equalsIgnoreCase(estado)) {
                where += " AND s.cierre IS NOT NULL ";
            }
        }

        String orden = " ORDER BY s.apertura DESC ";
        String page = " LIMIT :limit OFFSET :offset ";

        pagina = (pagina == null || pagina < 1) ? 1 : pagina;
        tamPag = (tamPag == null || tamPag < 1) ? 10 : Math.min(tamPag, 1000);

        Connection con = null;
        try {
            HashMap<String, Object> response = new HashMap<>();
            con = ConexionSQL2o.getSql2o().open();

            // Count sin joins para evitar multiplicidad
            String countSql = "SELECT COUNT(*) FROM SesionCaja s WHERE 1=1 ";
            if (desde != null && hasta != null) {
                countSql += " AND (s.apertura BETWEEN :desde AND :hasta) ";
            }
//            if (cajaId != null) {
//                countSql += " AND s.caja_id = :cajaId ";
//            }
//            if (cajeroId != null) {
//                countSql += " AND s.cajero_id = :cajeroId ";
//            }
            if (estado != null) {
                if ("ABIERTA".equalsIgnoreCase(estado)) {
                    countSql += " AND s.cierre IS NULL ";
                } else if ("CERRADA".equalsIgnoreCase(estado)) {
                    countSql += " AND s.cierre IS NOT NULL ";
                }
            }

            Query cq = con.createQuery(countSql);
            Query dq = con.createQuery(select + from + where + orden + page);

            if (desde != null && hasta != null) {
                String desdeTs = desde + " 00:00:00";
                String hastaTs = hasta + " 23:59:59";
                cq.addParameter("desde", desdeTs);
                cq.addParameter("hasta", hastaTs);
                dq.addParameter("desde", desdeTs);
                dq.addParameter("hasta", hastaTs);
            }
//            if (cajaId != null) {
//                cq.addParameter("cajaId", cajaId);
//                dq.addParameter("cajaId", cajaId);
//            }
//            if (cajeroId != null) {
//                cq.addParameter("cajeroId", cajeroId);
//                dq.addParameter("cajeroId", cajeroId);
//            }

            Integer totalElementos = cq.executeAndFetchFirst(Integer.class);
            Integer totalPaginas = (int) Math.ceil((double) totalElementos / tamPag);

            List<HashMap<String, Object>> data = tablaToSesionesCajaOP(
                    dq.addParameter("limit", tamPag)
                            .addParameter("offset", (pagina - 1) * tamPag)
                            .executeAndFetchTable()
            );

            response.put("pagina", pagina);
            response.put("elementos", tamPag);
            response.put("t_elementos", totalElementos);
            response.put("t_paginas", totalPaginas);
            if (data != null) {
                response.put("data", data);
            }

            return response;
        } catch (Sql2oException e) {
            logger.error("Error SQL en filtrarSesionesCajaOP(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error en filtrarSesionesCajaOP(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
            }
            logger.debug("Conexión cerrada después de filtrarSesionesCajaOP()");
        }
        return null;
    }

    private List<HashMap<String, Object>> tablaToSesionesCajaOP(Table tabla) {
        List<HashMap<String, Object>> filas = new ArrayList<>();
        try {
            for (Row row : tabla.rows()) {
                // Sesión
                SesionCaja s = new SesionCaja();
                s.setId(row.getInteger("sid"));
                s.setMonto_inicial(row.getFloat("smonto_inicial"));
                s.setMonto_final(row.getFloat("smonto_final")); // puede venir null
                s.setApertura(row.getString("sapertura"));
                s.setCierre(row.getString("scierre"));          // puede venir null
                s.setCajero_id(row.getInteger("scajero_id"));
                s.setCaja_id(row.getInteger("scaja_id"));

                HashMap<String, Object> fila = new HashMap<>();
                fila.put("sesion", s);

                filas.add(fila);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("SELECT tablaToSesionesCajaOP() - correcto");
            }
        } catch (Exception ex) {
            logger.warn(getClass().getName() + ".tablaToSesionesCajaOP() Error: " + ex.getMessage(), ex);
        }
        return filas;
    }

    /**
     * Devuelve una sesión con sus movimientos asociados (y cliente opcional).
     *
     * @param id ID de la sesión
     * @return HashMap con "sesion", "movimientos", "totales"
     */
    public HashMap<String, Object> buscarSesionConMovimientos(String id) {
        Connection con = null;
        String query
                = "SELECT "
                + // Sesión
                " s.id AS sid, "
                + " s.monto_inicial AS smonto_inicial, "
                + " s.monto_final AS smonto_final, "
                + " s.apertura AS sapertura, "
                + " s.cierre AS scierre, "
                + " s.cajero_id AS scajero_id, "
                + " s.caja_id AS scaja_id, "
                + // Movimiento
                " m.id AS mid, "
                + " m.creado AS mcreado, "
                + " m.ultMod AS multMod, "
                + " m.estado AS mestado, "
                + " m.cliente_id AS mcliente_id, "
                + " m.sesion_caja_id AS msesion_caja_id, "
                + " m.tipo_mov AS mtipo_mov, "
                + " m.descripcion AS mdescripcion, "
                + " m.total AS mtotal, "
                
                + " FROM SesionCaja s "
                + " LEFT JOIN Movimiento m ON m.sesion_caja_id = s.id AND m.estado = 'ACTIVO' "
                + " WHERE s.id = :id "
                + " ORDER BY m.id ASC ";

        try {
            con = ConexionSQL2o.getSql2o().open();
            return tablaToSesionConMovimientos(
                    con.createQuery(query).addParameter("id", id).executeAndFetchTable()
            );
        } catch (Exception e) {
            logger.error("Error inesperado en buscarSesionConMovimientos(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
            }
            logger.debug("Conexión cerrada después de buscarSesionConMovimientos()");
        }
        return null;
    }

    private HashMap<String, Object> tablaToSesionConMovimientos(Table tabla) {
        HashMap<String, Object> resultado = new HashMap<>();
        List<Movimiento> movimientos = new ArrayList<>();
        List<HashMap<String, Object>> movimientosConCliente = new ArrayList<>();

        SesionCaja s = null;

        float totalIngresos = 0f;
        float totalEgresos = 0f;

        try {
            for (Row row : tabla.rows()) {
                if (s == null) {
                    s = new SesionCaja();
                    s.setId(row.getInteger("sid"));
                    s.setMonto_inicial(row.getFloat("smonto_inicial"));
                    s.setMonto_final(row.getFloat("smonto_final")); // puede ser null
                    s.setApertura(row.getString("sapertura"));
                    s.setCierre(row.getString("scierre"));          // puede ser null
                    s.setCajero_id(row.getInteger("scajero_id"));
                    s.setCaja_id(row.getInteger("scaja_id"));
                }

                // Puede no haber movimientos (LEFT JOIN)
                if (row.getObject("mid") != null) {
                    Movimiento m = new Movimiento();
                    m.setId(row.getInteger("mid"));
                    m.setCreado(row.getString("mcreado"));
                    m.setUltMod(row.getString("multMod"));
                    m.setEstado(row.getString("mestado"));
                    m.setCliente_id(row.getInteger("mcliente_id"));
                    m.setSesion_caja_id(row.getInteger("msesion_caja_id"));
                    m.setTipo_mov(row.getString("mtipo_mov"));
                    m.setDescripcion(row.getString("mdescripcion"));
                    m.setTotal(row.getFloat("mtotal"));
                    movimientos.add(m);

                    HashMap<String, Object> fila = new HashMap<>();
                    fila.put("movimiento", m);

                    movimientosConCliente.add(fila);

                    // Acumular totales
                    if (m.getTotal() != null) {
                        if ("EGRESO".equalsIgnoreCase(m.getTipo_mov())) {
                            totalEgresos += m.getTotal();
                        } else { // VENTA o INGRESO
                            totalIngresos += m.getTotal();
                        }
                    }
                }
            }

            // Totales y conciliación
            Float esperadoFinal = null;
            if (s != null && s.getMonto_inicial() != null) {
                esperadoFinal = s.getMonto_inicial() + totalIngresos - totalEgresos;
            }
            Float diferencia = null;
            if (esperadoFinal != null && s != null && s.getMonto_final() != null) {
                diferencia = s.getMonto_final() - esperadoFinal;
            }

            HashMap<String, Object> totales = new HashMap<>();
            totales.put("ingresos", totalIngresos);
            totales.put("egresos", totalEgresos);
            totales.put("saldo", totalIngresos - totalEgresos);
            if (esperadoFinal != null) {
                totales.put("esperado_final", esperadoFinal);
            }
            if (diferencia != null) {
                totales.put("diferencia_vs_registrado", diferencia);
            }

            resultado.put("sesion", s);
            // Si preferís, podés devolver solo `movimientos` sin el cliente. Dejo ambos:
            resultado.put("movimientos", movimientos);
            resultado.put("movimientos_detalle", movimientosConCliente);
            resultado.put("totales", totales);

            if (logger.isDebugEnabled()) {
                logger.debug("tablaToSesionConMovimientos() - correcto");
            }

        } catch (Exception ex) {
            logger.warn(getClass().getName() + " tablaToSesionConMovimientos() Error: " + ex.getMessage(), ex);
        }
        return resultado;
    }

    public List<Movimiento> movimientosCaja(String id_caja) {
        String query = "SELECT Movimiento.* FROM Movimiento JOIN Caja ON Movimiento.sesion_caja_id=Caja.sesion_actual WHERE Caja.id = :id";
        Connection con = null;
        try {
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).addParameter("id", id_caja).executeAndFetch(Movimiento.class);
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
                    .addParameter("orden", "id")
                    .executeAndFetch(Movimiento.class);

            response.put("pagina", pagina);
            response.put("elementos", elementos);
            response.put("t_elementos", totalElementos);
            response.put("t_paginas", totalPaginas);

            if (result != null) {
                response.put("data", result);
            }
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

    public HashMap<String, Object> resumenSesionesCaja(String filtro, String desde, String hasta) {
        Connection con = null;
        HashMap<String, Object> response = new HashMap<>();
        try {
            con = ConexionSQL2o.getSql2o().open();

            boolean usarFechas = (desde != null && hasta != null);
            if (usarFechas) {
                desde += " 00:00:00";
                hasta += " 23:59:59";
            }

            String where = " WHERE 1=1 ";
            if (usarFechas) {
                where += " AND s.apertura BETWEEN :desde AND :hasta ";
            }

            // Nota: SesionCaja no tiene campos de texto para filtrar por 'filtro'.
            // Si 'filtro' viniera como un número, lo uso para filtrar por caja/cajero opcionalmente.
            boolean filtroNumerico = false;
            Integer filtroNum = null;
            if (filtro != null && !filtro.isEmpty()) {
                try {
                    filtroNum = Integer.valueOf(filtro);
                    filtroNumerico = true;
                } catch (NumberFormatException ignore) {
                    /* sin filtro de texto en SesionCaja */ }
            }
            if (filtroNumerico) {
                where += " AND (s.caja_id = :filtroNum OR s.cajero_id = :filtroNum) ";
            }

//                    = "SELECT "
//                    + "  COUNT(*)                                                    AS t_sesiones, "
//                    + "  SUM(CASE WHEN s.cierre IS NULL THEN 1 ELSE 0 END)          AS t_abiertas, "
//                    + "  SUM(CASE WHEN s.cierre IS NOT NULL THEN 1 ELSE 0 END)      AS t_cerradas, "
//                    + "  COALESCE(SUM(s.monto_inicial), 0)                          AS monto_inicial_total, "
//                    + "  COALESCE(SUM(s.monto_final),   0)                          AS monto_final_total, "
//                    + "  AVG(s.monto_inicial)                                       AS promedio_monto_inicial, "
//                    + "  AVG(s.monto_final)                                         AS promedio_monto_final, "
//                    + "  MIN(s.apertura)                                            AS primera_apertura, "
//                    + "  MAX(COALESCE(s.cierre, s.apertura))                        AS ultima_actividad "
//                    + "FROM SesionCaja s " + where;

            String sql = "SELECT " 
                + "  COUNT(*) AS t_sesiones, " 
                + "  SUM(CASE WHEN s.cierre IS NULL THEN 1 ELSE 0 END) AS t_abiertas, " 
                + "  SUM(CASE WHEN s.cierre IS NOT NULL THEN 1 ELSE 0 END) AS t_cerradas, " 
                + "  COALESCE(SUM(s.monto_inicial),0) AS monto_inicial_total, " 
                + "  COALESCE(SUM(s.monto_final),0)  AS monto_final_total, " 
                + "  AVG(s.monto_inicial) AS promedio_monto_inicial, " 
                + "  AVG(s.monto_final)   AS promedio_monto_final, " 
                + "  MIN(s.apertura) AS primera_apertura, " 
                + "  MAX(COALESCE(s.cierre, s.apertura)) AS ultima_actividad, " 
                + "  COALESCE(SUM(CASE " 
                + "     WHEN s.cierre IS NOT NULL AND s.monto_final IS NOT NULL " 
                + "     THEN (s.monto_final - s.monto_inicial) ELSE 0 END), 0) "
                + "  AS diferencia_total_cerradas, " 
                + "  COALESCE(AVG(CASE " 
                + "     WHEN s.cierre IS NOT NULL AND s.monto_final IS NOT NULL " 
                + "     THEN (s.monto_final - s.monto_inicial) END), 0) "
                + "  AS diferencia_promedio_cerradas, " 
                + "  COALESCE(SUM(CASE WHEN s.cierre IS NOT NULL THEN s.monto_inicial ELSE 0 END),0) AS monto_inicial_total_cerradas, " 
                + "  COALESCE(SUM(CASE WHEN s.cierre IS NOT NULL THEN s.monto_final   ELSE 0 END),0) AS monto_final_total_cerradas " 
                + " FROM SesionCaja s " + where;

            Query q = con.createQuery(sql);

            if (usarFechas) {
                q.addParameter("desde", desde).addParameter("hasta", hasta);
            }
            if (filtroNumerico) {
                q.addParameter("filtroNum", filtroNum);
            }

            org.sql2o.data.Table t = q.executeAndFetchTable();
            org.sql2o.data.Row r = t.rows().isEmpty() ? null : t.rows().get(0);

            // Si no hay filas, devolver ceros
            if (r == null) {
                response.put("total_sesiones", 0);
                response.put("sesiones_abiertas", 0);
                response.put("sesiones_cerradas", 0);
                response.put("monto_inicial_total", 0f);
                response.put("monto_final_total", 0f);
                response.put("promedio_monto_inicial", 0f);
                response.put("promedio_monto_final", 0f);
                response.put("primera_apertura", null);
                response.put("ultima_actividad", null);
                return response;
            }

            Integer totalSes = r.getInteger("t_sesiones");
            Integer abiertas = r.getInteger("t_abiertas");
            Integer cerradas = r.getInteger("t_cerradas");

            Float montoInicialTotal = r.getFloat("monto_inicial_total");
            Float montoFinalTotal = r.getFloat("monto_final_total");
            Float promMontoInicial = r.getFloat("promedio_monto_inicial");
            Float promMontoFinal = r.getFloat("promedio_monto_final");

            Float difTotCerr = r.getFloat("diferencia_total_cerradas");
            Float difAvgCerr = r.getFloat("diferencia_promedio_cerradas");
            Float iniTotCerr = r.getFloat("monto_inicial_total_cerradas");
            Float finTotCerr = r.getFloat("monto_final_total_cerradas");

            String primeraApertura = r.getString("primera_apertura");
            String ultimaActividad = r.getString("ultima_actividad");

            // Null-safe
            response.put("total_sesiones", totalSes == null ? 0 : totalSes);
            response.put("sesiones_abiertas", abiertas == null ? 0 : abiertas);
            response.put("sesiones_cerradas", cerradas == null ? 0 : cerradas);
            response.put("monto_inicial_total", montoInicialTotal == null ? 0f : montoInicialTotal);
            response.put("monto_final_total", montoFinalTotal == null ? 0f : montoFinalTotal);
            response.put("promedio_monto_inicial", promMontoInicial == null ? 0f : promMontoInicial);
            response.put("promedio_monto_final", promMontoFinal == null ? 0f : promMontoFinal);
            response.put("primera_apertura", primeraApertura);
            response.put("ultima_actividad", ultimaActividad);
            response.put("diferencia_total_cerradas", difTotCerr == null ? 0f : difTotCerr);
            response.put("diferencia_promedio_cerradas", difAvgCerr == null ? 0f : difAvgCerr);
            response.put("monto_inicial_total_cerradas", iniTotCerr == null ? 0f : iniTotCerr);
            response.put("monto_final_total_cerradas",   finTotCerr == null ? 0f : finTotCerr);
            
            return response;

        } catch (Exception e) {
            logger.error("resumenSesionesCaja (solo SesionCaja): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("resumenSesionesCaja: Conexión cerrada");
            }
        }
        return null;
    }

    public HashMap<String, Object> resumenMovimientosCaja(String filtro, String desde, String hasta) {
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
