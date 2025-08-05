    package com.OndaByte.GestionComercio.DAO;

import com.OndaByte.GestionComercio.modelo.Cliente;
import com.OndaByte.GestionComercio.modelo.Orden;
import com.OndaByte.GestionComercio.modelo.Pedido;
import com.OndaByte.GestionComercio.modelo.Presupuesto;
import com.OndaByte.GestionComercio.modelo.Turno;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;

import org.sql2o.Query;
import org.sql2o.Sql2oException;
import org.sql2o.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.data.Row;
import org.sql2o.data.Table;

public class DAOOrden implements DAOInterface<Orden> {

    private static Logger logger = LogManager.getLogger(DAOOrden.class.getName());
    
    private String clave = "id";
    private GeneradorQuery<Orden> generadorQuery;
    
    public DAOOrden() {
        generadorQuery = new GeneradorQuery<Orden>(Orden.class);
        generadorQuery.setClave(clave);
    }
    
    public Class<Orden> getClase() {
        return Orden.class;
    }

    public Integer alta(Orden t, String turno_id){
        String query;
        Connection con = null;
        try {
            con = ConexionSQL2o.getSql2o().open();
            query = generadorQuery.getQueryAlta(t);
            con.close();
            con = ConexionSQL2o.getSql2o().beginTransaction();
            t.setEstado_orden("PENDIENTE");
            Query auxQuery = con.createQuery(query).bind(t);
            Integer orden_id = auxQuery.executeUpdate().getKey(Integer.class);
            query = "INSERT INTO TurnoOrden (turno_id,orden_id) VALUES (:turno_id,:orden_id)";
            
            auxQuery = con.createQuery(query).addParameter("turno_id", turno_id).addParameter("orden_id", orden_id);
            auxQuery.executeUpdate();
            con.commit();      
            return orden_id;
        }  catch (Sql2oException e) {
            logger.error("Alta(orden,turno_id): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Alta(orden,turno_id): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Alta(orden,turno_id): Conexión cerrada");
        }
        return -1;
    }
    
    @Override
    public Orden buscar(String id) {
        String query;
        Connection con = null;
    	try {
            query = generadorQuery.getQueryFiltarId();
            con = ConexionSQL2o.getSql2o().open();
            return (Orden) con.createQuery(query).addParameter("id", id).executeAndFetchFirst(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Buscar(id): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Buscar(id): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Buscar(id): Conexión cerrada");
        }
        return null;
    }
    
    @Override
    public Integer alta(Orden t) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryAlta(t);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).bind(t).executeUpdate().getKey(Integer.class);
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOOrden.alta(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOOrden.alta(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOOrden.alta()");
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
            logger.error("Error SQL en DAOOrden.baja(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOOrden.baja(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOOrden.baja()");
        }
        return false;
    }

    public Integer cantEstado(String estado){
        String query = "SELECT count(*) FROM Orden WHERE Orden.estado_orden = :estado";
        Connection con = null;
        try {
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).addParameter("estado",estado).executeScalar(Integer.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean modificar(Orden t) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryModificar();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).bind(t).executeUpdate().getResult() > 0;
        } catch (Exception e) {
            logger.error("Modificar: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("Modificar: Conexion cerrada");
            }
        }
        return false;
    }
/**
     * @param ordenId Integer 
     * @param turnoId Integer 
     * @return true si ok.
     */
    public boolean asignar(Integer ordenId, Turno turno, Connection con) {
        String query;
        try {
            if(con == null)
                con = ConexionSQL2o.getSql2o().open();
            query = "UPDATE Orden o SET turno_id = :turnoId, estado_orden = 'ASIGNADA', tipo = :tipo WHERE o.id = :ordenId;";
            return con.createQuery(query)
                    .addParameter("turnoId", turno.getId())
                    .addParameter("tipo", turno.getTipo())
                    .addParameter("ordenId", ordenId)
                    .executeUpdate().getResult() > 0;
        } catch (Sql2oException e) {
            logger.error("Error SQL en DAOOrden.asignar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOOrden.asignar(): " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public List<Orden> listar() {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryListar();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOOrden.listar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOOrden.listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOOrden.listar()");
        }
        return null;
    }
    
    @Override
    public List<Orden> listar(String... ids) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryListar(ids);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOOrden.listar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOOrden.listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOOrden.listar()");
        }
        return null;
    }

    @Override
    public List<Orden> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones,List<Boolean> conectores) {
        String query;
        Connection con = null;
		try {
			query = generadorQuery.getQueryFiltrar(campos, valores, condiciones, conectores);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Error SQL en DAOOrden.filtrar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOOrden.filtrar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOOrden.filtrar()");
        }
        return null;
    }
    /**
     * Devuelve Orden, Pedido, Cliente y Turno:
     * - Presupuesto si existe.
     * - El orden está fijo por ID de Orden descendente.
     *
     * @param filtro Filtra por nombre de cliente o por descripción de pedido u orden
     * @param desde Fecha desde creado pedido
     * @param hasta Fecha hasta creado pedido
     * @param estadoOrden estado_orden
     * @param pagina Número de página
     * @param tamPag Tamaño de página
     * @return Respuesta con datos y paginado
     */
    public HashMap<String, Object> filtrarDetalladoOP(String filtro, String desde, String hasta, String estadoOrden, Integer pagina, Integer tamPag) {
        String select = " SELECT o.id AS oid, " 
                + " o.descripcion odescripcion, " 
                + " o.estado_orden oestado_orden, " 
                + " o.tipo otipo, " 
                + " o.fecha_fin ofecha_fin, " 
                + " o.creado ocreado, " 
                + " p.id AS pid, " 
                + " p.descripcion pdescripcion, " 
                + " p.estado_pedido pestado_pedido, " 
                + " p.fecha_fin_estimada pfecha_fin_estimada, " 
                + " pre.id AS preid, "
                + " pre.nombre prenombre, "
                + " pre.descripcion predescripcion, "
                + " pre.estado_presupuesto preestado_presupuesto, "
                + " pre.total pretotal, " 
                + " c.id AS cid, " 
                + " c.nombre cnombre, " 
                + " c.email cemail, " 
                + " c.telefono ctelefono, " 
                + " c.direccion cdireccion, " 
                + " c.dni cdni, " 
                + " c.cuit_cuil ccuit_cuil, " 
                + " c.localidad clocalidad, " 
                + " c.codigo_postal ccodigo_postal, " 
                + " c.provincia cprovincia, " 
                + " c.cond_iva ccond_iva, " 
                + " t.id tid, " 
                + " t.tipo ttipo, " 
                + " t.prioridad tprioridad, " 
                + " t.observaciones tobservaciones, " 
                + " t.fecha_inicio tfecha_inicio, " 
                + " t.fecha_fin_e tfecha_fin_e, "
                + " t.estado_turno testado_turno, "
                + " t.patron_repeticion tpatron_repeticion ";

        String from = " FROM Orden o " +
                " JOIN Pedido p ON o.pedido_id = p.id " +
                " JOIN Cliente c ON p.cliente_id = c.id " +
                " JOIN Presupuesto pre ON p.id = pre.pedido_id " +
                " LEFT JOIN Turno t ON o.turno_id = t.id ";

        String where = " WHERE o.estado = 'ACTIVO' " +
                " AND p.estado = 'ACTIVO' " +
                " AND c.estado = 'ACTIVO' " +
                " AND ( t.id IS NULL OR ( t.id IS NOT NULL AND t.estado='ACTIVO' ) )";


        if (filtro != null) {
            where += " AND (c.nombre LIKE :filtro OR p.descripcion LIKE :filtro OR o.descripcion LIKE :filtro)";
        }
        if (desde != null && hasta != null) {
            where += " AND (p.creado BETWEEN :desde AND :hasta)";
        }
        if (estadoOrden != null) {
            where += " AND o.estado_orden = :estadoOrden";
        }

        String orden = " ORDER BY oid DESC ";
        String page = " LIMIT :limit OFFSET :offset";

        pagina = (pagina == null || pagina < 1) ? 1 : pagina;
        tamPag = (tamPag == null || tamPag < 1) ? 10 : Math.min(tamPag, 1000);

        Connection con = null;
        Integer totalElementos = null;
        Integer totalPaginas = null;
        List<HashMap<String, Object>> data;
        try {
            HashMap<String, Object> response = new HashMap<>();
            con = ConexionSQL2o.getSql2o().open();

            Query cq = con.createQuery("SELECT COUNT(*) " + from + where);
            Query dq = con.createQuery(select + from + where + orden + page);

            if (filtro != null) {
                cq.addParameter("filtro", "%" + filtro + "%");
                dq.addParameter("filtro", "%" + filtro + "%");
            }
            if (desde != null && hasta != null) {
                desde += " 00:00:00";
                hasta += " 23:59:59";
                cq.addParameter("desde", desde);
                cq.addParameter("hasta", hasta);
                dq.addParameter("desde", desde);
                dq.addParameter("hasta", hasta);
            }
            if (estadoOrden != null) {
                cq.addParameter("estadoOrden", estadoOrden);
                dq.addParameter("estadoOrden", estadoOrden);
            }

            totalElementos = cq.executeAndFetchFirst(Integer.class);
            totalPaginas = (int) Math.ceil((double) totalElementos / tamPag);
            logger.debug("Count query ejecutada: " + cq.toString());

            data = tablaToOrdenesDetallado(dq
                    .addParameter("limit", tamPag)
                    .addParameter("offset", (pagina - 1) * tamPag)
                    .executeAndFetchTable());

              logger.debug("Datos query ejecutada: " + dq.toString());

            response.put("pagina", pagina);
            response.put("elementos", tamPag);
            response.put("t_elementos", totalElementos);
            response.put("t_paginas", totalPaginas);
            if (data != null) response.put("data", data);
            return response;

        } catch (Sql2oException e) {
            logger.error("Error SQL en DAOOrden.filtrarDetalladoOrdenes(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOOrden.filtrarDetalladoOrdenes(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
            }
            logger.debug("Conexión cerrada después de llamar a DAOOrden.filtrarDetalladoOrdenes()");
        }
        return null;
    }

private List<HashMap<String, Object>> tablaToOrdenesDetallado(Table tpc) {
    List<HashMap<String, Object>> filas = new ArrayList<>();
    try {
        for (Row r : tpc.rows()) {
            Orden o = new Orden();
            o.setId(r.getInteger("oid"));
            o.setDescripcion(r.getString("odescripcion"));
            o.setEstado_orden(r.getString("oestado_orden"));
            o.setTipo(r.getString("otipo")); 
            o.setFecha_fin(r.getString("ofecha_fin"));
            o.setCreado(r.getString("ocreado"));

            Pedido p = new Pedido();
            p.setId(r.getInteger("pid"));
            p.setDescripcion(r.getString("pdescripcion"));
            p.setFecha_fin_estimada(r.getString("pfecha_fin_estimada"));
            p.setEstado_pedido(r.getString("pestado_pedido"));

            Presupuesto pre = new Presupuesto();
            pre.setId(r.getInteger("preid"));
            pre.setPedido_id(r.getInteger("pid"));
            pre.setNombre(r.getString("prenombre"));
            pre.setDescripcion(r.getString("predescripcion"));
            pre.setEstado_presupuesto(r.getString("preestado_presupuesto"));
            pre.setTotal(r.getFloat("pretotal"));
                
            Cliente c = new Cliente();
            c.setId(r.getInteger("cid"));
            c.setNombre(r.getString("cnombre"));
            c.setEmail(r.getString("cemail"));
            c.setTelefono(r.getString("ctelefono"));
            c.setDireccion(r.getString("cdireccion"));
            c.setDni(r.getString("cdni"));
            c.setCuit_cuil(r.getString("ccuit_cuil"));
            c.setLocalidad(r.getString("clocalidad"));
            c.setCodigo_postal(r.getString("ccodigo_postal"));
            c.setProvincia(r.getString("cprovincia"));
            c.setCond_iva(r.getString("ccond_iva"));

            Turno t = new Turno();
            t.setId(r.getInteger("tid"));
            t.setTipo(r.getString("ttipo"));
            t.setObservaciones(r.getString("tobservaciones"));
            t.setPrioridad(r.getInteger("tprioridad"));
            t.setFecha_inicio(r.getString("tfecha_inicio"));
            t.setFecha_fin_e(r.getString("tfecha_fin_e"));
            t.setEstado_turno(r.getString("testado_turno"));
            t.setPatron_repeticion(r.getInteger("tpatron_repeticion"));

            HashMap<String, Object> fila = new HashMap<>();
            fila.put("orden", o);
            fila.put("pedido", p);
            fila.put("cliente", c);
            fila.put("presupuesto", pre);
            fila.put("turno", t);

            filas.add(fila);
        }
        logger.debug("SELECT query DAOOrden.tablaToOrdenesDetallado() - correcto");
    } catch (Exception ex) {
        logger.warn("DAOOrden.tablaToOrdenesDetallado() Error: " + ex.getMessage(), ex);
    }
    return filas;
}


	
}
