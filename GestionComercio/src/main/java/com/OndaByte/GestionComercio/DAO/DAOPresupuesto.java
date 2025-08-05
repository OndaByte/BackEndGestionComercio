
package com.OndaByte.GestionComercio.DAO;

import com.OndaByte.GestionComercio.modelo.Cliente;
import com.OndaByte.GestionComercio.modelo.ItemPresupuesto;
import com.OndaByte.GestionComercio.modelo.Pedido;
import com.OndaByte.GestionComercio.modelo.Presupuesto;
import com.OndaByte.GestionComercio.modelo.Turno;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2oException;
import org.sql2o.data.Row;
import org.sql2o.data.Table;

public class DAOPresupuesto implements DAOInterface<Presupuesto> {

    private static Logger logger = LogManager.getLogger(DAOPresupuesto.class.getName());

    private String clave = "id";
    private GeneradorQuery<Presupuesto> generadorQuery;

    public DAOPresupuesto() {
        generadorQuery = new GeneradorQuery<Presupuesto>(Presupuesto.class);
        generadorQuery.setClave(clave);
    }

    public Class<Presupuesto> getClase() {
        return Presupuesto.class;
    }
    /**
     * Sobrecargado.
     * para no romper con la implementacion de la interfaz ..
     * @param p
     * @param con
     * @return 
     */
    public Integer alta(Presupuesto p, Connection con) {
        String query;
        try {
            if(con == null) // esto es por si queremos reutilizar el método ... o mantenemos los 2 metodos polimorfos
                con = ConexionSQL2o.getSql2o().open();
            query = "SELECT EXISTS (SELECT * FROM Pedido WHERE id = :pedido_id)";

            Query auxQuery = con.createQuery(query).addParameter("pedido_id", p.getPedido_id());

            if (auxQuery.executeAndFetchFirst(Integer.class) == 0) {
                logger.debug("DAOPresupuesto.alta() - No se encontro pedido asociado para dar de alta con id:" + p.getPedido_id());
                return -1;
            }

            query = generadorQuery.getQueryAlta(p);
            logger.debug(query);

            auxQuery = con.createQuery(query).bind(p);
            return auxQuery.executeUpdate().getKey(Integer.class);
        } catch (Exception e) {
            logger.error("Alta: " + e.getMessage(), e);
        }
        return -1;
    }
    
    @Override
    public Integer alta(Presupuesto p) {
        String query;
        Connection con = null;
        try {
            con = ConexionSQL2o.getSql2o().open();
            query = "SELECT EXISTS (SELECT * FROM Pedido WHERE id = :pedido_id)";

            Query auxQuery = con.createQuery(query).addParameter("pedido_id", p.getPedido_id());

            if (auxQuery.executeAndFetchFirst(Integer.class) == 0) {
                logger.debug("DAOPresupuesto.alta() - No se encontro pedido asociado para dar de alta con id:" + p.getPedido_id());
                return -1;
            }

            query = generadorQuery.getQueryAlta(p);
            logger.debug(query);

            auxQuery = con.createQuery(query).bind(p);
            return auxQuery.executeUpdate().getKey(Integer.class);
        } catch (Exception e) {
            logger.error("Alta: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Alta: Conexión cerrada");
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
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Baja: Conexión cerrada");
        }
        return false;
    }
    /**
     * Sobrecargado.
     * @param p
     * @param con
     * @return 
     */
    public boolean modificar(Presupuesto pre, Connection con) {
        String query;
        try {
            query = "UPDATE Presupuesto SET "
                                + " total = :total,"
                                + " descripcion = :descripcion"
                                + " WHERE id = :id;";
            return con.createQuery(query).addParameter("total", pre.getTotal())
                    .addParameter("descripcion", pre.getDescripcion())
                    .addParameter("id", pre.getId())
                    .executeUpdate()
                    .getResult() > 0;  
        } catch (Exception e) {
            logger.error("Modificar: " + e.getMessage(), e);
        }
        return false;
    }
    
    /**
     * Borra todos los ítems de un presupuesto
     */
    public void bajaItems(Connection con, Integer preId) {

        final String deleteItems = 
            "DELETE FROM ItemPresupuesto WHERE presupuesto_id = :presupuesto_id";

        // Borrar
        con.createQuery(deleteItems)
           .addParameter("presupuesto_id", preId)
           .executeUpdate();
        
    }
    @Override
    public boolean modificar(Presupuesto p) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryModificar();
            con = ConexionSQL2o.getSql2o().open();

            Query auxQuery = con.createQuery(query).bind(p);

            return auxQuery.executeUpdate().getResult() > 0;
        } catch (Exception e) {
            logger.error("Modificar: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Modificar: Conexión cerrada");
        }
        return false;
    }

    @Override
    public List<Presupuesto> listar() {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryListar();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        } catch (Exception e) {
            logger.error("Error inesperado en DAOPresupuesto.listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOPresupuesto.listar()");
        }
        return null;
    }

    @Override
    public List<Presupuesto> listar(String... ids) {
        String query;
        Connection con = null;
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
            logger.debug("Listar: Conexión cerrada");
        }
        return null;
    }

    public HashMap<String, Object> listarPresupuestoPedidoYClientes(Integer pagina, Integer elementos) {
        boolean paginado = pagina != null && elementos != null;
        Connection con = null;
        String select = " SELECT "
                + " pre.id AS preid, "
                + " pre.nombre prenombre, "
                + " pre.descripcion predescripcion, "
                + " pre.estado_presupuesto preestado_presupuesto, "
                + " pre.total pretotal, "
                + " p.id AS pid, "
                + " p.descripcion pdescripcion, "
                + " p.cliente_id, "
                + " p.fecha_fin_estimada, "
                + " p.estado_pedido,"
                + " c.id AS cid, "
                + " c.nombre nombre, "
                + " c.email email,"
                + " c.telefono telefono, "
                + " c.direccion direccion, "
                + " c.dni dni, "
                + " c.cuit_cuil cuit_cuil, "
                + " c.localidad localidad, "
                + " c.codigo_postal codigo_postal, "
                + " c.provincia provincia, "
                + " c.cond_iva cond_iva";
        String from = " FROM Presupuesto pre JOIN Pedido p ON pre.pedido_id=p.id JOIN Cliente c ON p.cliente_id=c.id ";
        String where = " WHERE p.estado = 'ACTIVO' AND c.estado = 'ACTIVO'";
        String page = " LIMIT :limit OFFSET :offset";

        String queryDatos = select + from + where;
        Integer totalElementos = null;
        Integer totalPaginas = null;
        List<HashMap<String, Object>> data;
        try {
            con = ConexionSQL2o.getSql2o().open();
            HashMap<String, Object> response = new HashMap<>();
            if (paginado) {
                String queryCount = "SELECT COUNT(*) " + from + where;

                pagina = pagina == null || pagina < 1 ? 1 : pagina;
                elementos = elementos == null || elementos < 1 ? 10 : Math.min(elementos, 1000);

                totalElementos = con.createQuery(queryCount).executeAndFetchFirst(Integer.class); // Query extra
                totalPaginas = (int) Math.ceil((double) totalElementos / elementos);

                queryDatos += page;

                if (logger.isDebugEnabled()) {
                    logger.debug("SELECT query DAOPresupuesto.listarPresupuestoPedidoYClientes - paginado" + queryDatos);
                }

                data = tablaToDatosPresupuesto(con.createQuery(queryDatos)
                        .addParameter("limit", elementos)
                        .addParameter("offset", (pagina - 1) * elementos)
                        .executeAndFetchTable());

                response.put("pagina", pagina);
                response.put("elementos", elementos);
                response.put("t_elementos", totalElementos);
                response.put("t_paginas", totalPaginas);

            } else {

                if (logger.isDebugEnabled()) {
                    logger.debug("SELECT query DAOPresupuesto.listarPresupuestoPedidoYClientes" + queryDatos);
                }

                data = tablaToDatosPresupuesto(con.createQuery(queryDatos).executeAndFetchTable());
            }

            if (data != null) {
                response.put("data", data);
            }
            return response;

        } catch (Exception e) {
            logger.error("ListarPedidosYClientes: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("ListarPedidosYClientes: Conexión cerrada");
        }
        return null;
    }

    /**
     * presupuesto-pedido-cliente
     */
    private List<HashMap<String, Object>> tablaToDatosPresupuesto(Table tpc) {
        List<HashMap<String, Object>> filas = new ArrayList<>();
        HashMap<String, Object> f = null;
        Presupuesto pre = null;
        Pedido p = null;
        Cliente c = null;
        try {
            for (Row r : tpc.rows()) {
                pre = new Presupuesto();
                pre.setId(r.getInteger("preid"));
                pre.setPedido_id(r.getInteger("pid"));
                pre.setNombre(r.getString("prenombre"));
                pre.setDescripcion(r.getString("predescripcion"));
                pre.setEstado_presupuesto(r.getString("preestado_presupuesto"));
                pre.setTotal(r.getFloat("pretotal"));

                p = new Pedido();
                p.setId(r.getInteger("pid"));
                p.setDescripcion(r.getString("pdescripcion"));
                p.setCliente_id(r.getInteger("cliente_id"));
                p.setFecha_fin_estimada(r.getString("fecha_fin_estimada"));
                p.setEstado_pedido(r.getString("estado_pedido"));

                c = new Cliente();
                c.setId(r.getInteger("cid"));
                c.setNombre(r.getString("nombre"));

                f = new HashMap<>();
                f.put("presupuesto", pre);
                f.put("pedido", p);
                f.put("cliente", c);
                filas.add(f);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("SELECT query DAOPresupuesto.tablaToDatosPresupuesto() - correcto");
            }

        } catch (Exception ex) {
            logger.warn(DAOPresupuesto.class.getName() + ".tablaToDatosPresupuesto() Error: " + ex.getMessage() + ", " + ex.getMessage());
        }
        return filas;
    }

    /**
     * Devuelve Presupuesto, Pedido, Cliente y Turno: Presupuesto debe estar
     * activo (si existe).
     *
     * @param filtro Filtra por nombre de cliente o descripción de pedido
     * @param desde Fecha desde creado (pedido)
     * @param hasta Fecha hasta creado (pedido)
     * @param estadoPedido estado_pedido (de pedido)
     * @param pagina Número de página
     * @param tamPag Tamaño de página
     * @return HashMap con data + info de paginación
     */
    public HashMap<String, Object> filtrarDetalladoOP(String filtro, String desde, String hasta, String estadoPedido, Integer pagina, Integer tamPag) {
        String select = "SELECT "
                + " pre.id AS preid, pre.nombre AS prenombre, pre.descripcion AS predescripcion, "
                + " pre.estado_presupuesto AS preestado_presupuesto, pre.total AS pretotal, pre.creado AS precreado, pre.ultMod AS preultMod, "
                + " p.id AS pid, p.descripcion AS pdescripcion, p.cliente_id AS pcliente_id, "
                + " p.fecha_fin_estimada AS pfecha_fin_estimada, p.estado_pedido AS pestado_pedido, p.creado AS pcreado, "
                + " c.id AS cid, c.nombre AS cnombre, c.email AS cemail, c.telefono AS ctelefono, c.direccion AS cdireccion, "
                + " c.dni AS cdni, c.cuit_cuil AS ccuit_cuil, c.localidad AS clocalidad, c.codigo_postal AS ccodigo_postal, "
                + " c.provincia AS cprovincia, c.cond_iva AS ccond_iva, "
                + " t.id AS tid, t.tipo ttipo, t.observaciones AS tobservaciones, t.prioridad AS tprioridad, "
                + " t.patron_repeticion AS tpatron_repeticion, t.fecha_inicio AS tfecha_inicio, t.fecha_fin_e AS tfecha_fin_e, t.estado_turno AS testado_turno";

        String from = " FROM Presupuesto pre "
                + " JOIN Pedido p ON pre.pedido_id = p.id "
                + " JOIN Cliente c ON p.cliente_id = c.id "
                + " LEFT JOIN Turno t ON p.turno_id = t.id ";

        String where = " WHERE pre.estado = 'ACTIVO' "
                + " AND p.estado = 'ACTIVO' "
                + " AND c.estado = 'ACTIVO' ";

        if (filtro != null) {
            where += " AND (c.nombre LIKE :filtro OR p.descripcion LIKE :filtro)";
        }
        if (desde != null && hasta != null) {
            where += " AND p.creado BETWEEN :desde AND :hasta";
        }
        if (estadoPedido != null) {
            where += " AND p.estado_pedido = :estadoPedido";
        }

        String orden = " ORDER BY preid DESC";
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
            if (estadoPedido != null) {
                cq.addParameter("estadoPedido", estadoPedido);
                dq.addParameter("estadoPedido", estadoPedido);
            }

            totalElementos = cq.executeAndFetchFirst(Integer.class);
            totalPaginas = (int) Math.ceil((double) totalElementos / tamPag);
            logger.debug("Count query ejecutada: " + cq.toString());

            data = tablaToPresupuestosDetalladoOP(
                    dq.addParameter("limit", tamPag)
                            .addParameter("offset", (pagina - 1) * tamPag)
                            .executeAndFetchTable()
            );

            logger.debug("Datos query ejecutada: " + dq.toString());

            response.put("pagina", pagina);
            response.put("elementos", tamPag);
            response.put("t_elementos", totalElementos);
            response.put("t_paginas", totalPaginas);
            response.put("data", data);

            return response;

        } catch (Sql2oException e) {
            logger.error("Error SQL en DAOPresupuesto.filtrarDetalladoOP(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOPresupuesto.filtrarDetalladoOP(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
            }
            logger.debug("Conexión cerrada después de DAOPresupuesto.filtrarDetalladoOP()");
        }

        return null;
    }

    private List<HashMap<String, Object>> tablaToPresupuestosDetalladoOP(Table tabla) {
        List<HashMap<String, Object>> filas = new ArrayList<>();

        try {
            for (Row r : tabla.rows()) {
                // Pedido
                Pedido p = new Pedido();
                p.setId(r.getInteger("pid"));
                p.setDescripcion(r.getString("pdescripcion"));
                p.setCliente_id(r.getInteger("pcliente_id"));
                p.setFecha_fin_estimada(r.getString("pfecha_fin_estimada"));
                p.setEstado_pedido(r.getString("pestado_pedido"));
                p.setCreado(r.getString("pcreado"));

                // Cliente
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

                // Turno (puede no existir)
                Turno t = null;
                if (r.getInteger("tid") != null) {
                    t = new Turno();
                    t.setId(r.getInteger("tid"));
                    t.setTipo(r.getString("ttipo"));
                    t.setObservaciones(r.getString("tobservaciones"));
                    t.setPrioridad(r.getInteger("tprioridad"));
                    t.setPatron_repeticion(r.getInteger("tpatron_repeticion"));
                    t.setFecha_inicio(r.getString("tfecha_inicio"));
                    t.setFecha_fin_e(r.getString("tfecha_fin_e"));
                    t.setEstado_turno(r.getString("testado_turno"));
                }

                // Presupuesto
                Presupuesto pre = new Presupuesto();
                pre.setId(r.getInteger("preid"));
                pre.setNombre(r.getString("prenombre"));
                pre.setDescripcion(r.getString("predescripcion"));
                pre.setEstado_presupuesto(r.getString("preestado_presupuesto"));
                pre.setTotal(r.getFloat("pretotal"));
                pre.setCreado(r.getString("precreado"));
                pre.setUltMod(r.getString("preultMod"));

                // Armar fila
                HashMap<String, Object> fila = new HashMap<>();
                fila.put("presupuesto", pre);
                fila.put("pedido", p);
                fila.put("cliente", c);
                fila.put("turno", t);

                filas.add(fila);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("SELECT query DAOPresupuesto.tablaToPresupuestosDetalladoOP() - correcto");
            }

        } catch (Exception ex) {
            logger.warn(DAOPresupuesto.class.getName() + ".tablaToPresupuestosDetalladoOP() Error: " + ex.getMessage(), ex);
        }

        return filas;
    }

    @Override
    public List<Presupuesto> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryFiltrar(campos, valores, condiciones, conectores);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        } catch (Exception e) {
            logger.error("Error inesperado en DAOPresupuesto.filtrar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOPresupuesto.filtrar()");
        }
        return null;
    }

    public HashMap<String, Object> filtrarOP(List<String> campos,
            List<String> valores, List<Integer> condiciones, List<Boolean> conectores,
            Integer pagina, Integer elementos) {
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

            String queryDatos = generadorQuery.getQueryFiltrarOrdenadoYPaginado(campos, valores, condiciones, conectores, "DESC"); // por ahora fijo

            if (logger.isDebugEnabled()) {
                logger.debug("queryDatos: " + queryDatos);
            }

            List<Presupuesto> result = con.createQuery(queryDatos)
                    .addParameter("limit", elementos)
                    .addParameter("offset", (pagina - 1) * elementos)
                    .addParameter("orden", "id")
                    .executeAndFetch(this.getClase());

            response.put("pagina", pagina);
            response.put("elementos", elementos);
            response.put("t_elementos", totalElementos);
            response.put("t_paginas", totalPaginas);

            if (result != null) {
                response.put("data", result);
            }
            return response;

        } catch (Sql2oException e) {
            logger.error("Error SQL en filtrarOrdenadoYPaginado(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en filtrarOrdenadoYPaginado(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a filtrarOrdenadoYPaginado()");
        }
        return null;
    }

    @Override
    public Presupuesto buscar(String id) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryFiltarId();
            con = ConexionSQL2o.getSql2o().open();
            return (Presupuesto) con.createQuery(query).addParameter("id", id).executeAndFetchFirst(this.getClase());
        } catch (Exception e) {
            logger.error("Error inesperado en DAOPresupuesto.buscar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOPresupuesto.buscar()");
        }
        return null;
    }
    /**
     * Deje el otro pq por ahí queres buscarlo nomas para saber si existe nomas y re sirve.. livianito 
     * @param id
     * @return 
     */
    public HashMap<String,Object> buscarDetallado(String id) {
        Connection con = null;
        String query = "SELECT "
            + " pre.id AS preid, " 
            + " pre.creado AS precreado, " 
            + " pre.ultMod AS preultMod, " 
            + " pre.estado AS preestado, " 
            + " pre.pedido_id AS prepedido_id, " 
            + " pre.nombre AS prenombre, " 
            + " pre.descripcion AS predescripcion, " 
            + " pre.estado_presupuesto AS preestado_presupuesto, " 
            + " pre.total AS pretotal, " 

            + " p.id AS pid, " 
            + " p.descripcion AS pdescripcion, " 
            + " p.cliente_id AS pcliente_id, " 
            + " p.fecha_fin_estimada AS pfecha_fin_estimada, " 
            + " p.estado_pedido AS pestado_pedido, " 
            + " p.creado AS pcreado, " 

            + " c.id AS cid, " 
            + " c.nombre AS cnombre, " 
            + " c.email AS cemail, " 
            + " c.telefono AS ctelefono, " 
            + " c.direccion AS cdireccion, " 
            + " c.dni AS cdni, " 
            + " c.cuit_cuil AS ccuit_cuil, " 
            + " c.localidad AS clocalidad, " 
            + " c.codigo_postal AS ccodigo_postal, " 
            + " c.provincia AS cprovincia, " 
            + " c.cond_iva AS ccond_iva, " 

            + " ip.id AS ipid, " 
            + " ip.creado AS ipcreado, " 
            + " ip.ultMod AS ipultMod, " 
            + " ip.estado AS ipestado, " 
            + " ip.descripcion AS ipdescripcion, "
            + " ip.precio AS ipprecio, " 
            + " ip.cantidad AS ipcantidad, "
            + " ip.presupuesto_id AS ippresupuesto_id "

            + "FROM Presupuesto pre " 
            + "JOIN Pedido p ON pre.pedido_id = p.id " 
            + "JOIN Cliente c ON p.cliente_id = c.id " 
            + "JOIN ItemPresupuesto ip ON pre.id = ip.presupuesto_id " 
            + "WHERE pre.estado = 'ACTIVO' " 
            + "AND p.estado = 'ACTIVO' " 
            + "AND c.estado = 'ACTIVO' " 
            + "AND ip.estado = 'ACTIVO' " 
            + "AND pre.id = :id;";
        
        try {
            con = ConexionSQL2o.getSql2o().open();
            return tablaToPresupuestoConItems(
            con.createQuery(query).addParameter("id", id).executeAndFetchTable()
            );
        } catch (Exception e) {
            logger.error("Error inesperado en DAOPresupuesto.buscar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOPresupuesto.buscar()");
        }
        return null;
    }
    
    private HashMap<String, Object> tablaToPresupuestoConItems(Table tabla) {
        HashMap<String, Object> resultado = new HashMap<>();
        List<ItemPresupuesto> items = new ArrayList<>();
        Presupuesto pre = null;
        Pedido p = null;
        Cliente c = null;
        try {
            for (Row r : tabla.rows()) {

                // Solo se instancia una vez el presupuesto (se repite en cada fila)
                if (pre == null) {
                    pre = new Presupuesto();
                    pre.setId(r.getInteger("preid"));
                    pre.setNombre(r.getString("prenombre"));
                    pre.setDescripcion(r.getString("predescripcion"));
                    pre.setEstado_presupuesto(r.getString("preestado_presupuesto"));
                    pre.setTotal(r.getFloat("pretotal"));
                    pre.setCreado(r.getString("precreado"));
                    pre.setUltMod(r.getString("preultMod"));
                }

               
                if (p == null) {
                    p = new Pedido();
                    p.setId(r.getInteger("pid"));
                    p.setDescripcion(r.getString("pdescripcion"));
                    p.setCliente_id(r.getInteger("pcliente_id"));
                    p.setFecha_fin_estimada(r.getString("pfecha_fin_estimada"));
                    p.setEstado_pedido(r.getString("pestado_pedido"));
                    p.setCreado(r.getString("pcreado"));
                }
                if (c  == null) {
                    c = new Cliente();
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
                }
                
                ItemPresupuesto ip = new ItemPresupuesto();
                ip.setId(r.getInteger("ipid"));
                ip.setCreado(r.getString("ipcreado"));
                ip.setUltMod(r.getString("ipultMod")); //TODO agregar estos campos
                ip.setEstado(r.getString("ipestado"));
                ip.setDescripcion(r.getString("ipdescripcion"));
                ip.setPrecio(r.getFloat("ipprecio"));
                ip.setCantidad(r.getInteger("ipcantidad"));
                ip.setPresupuesto_id(r.getInteger("ippresupuesto_id"));
                items.add(ip);
            }

            resultado.put("pedido", p);
            resultado.put("cliente", c);
            resultado.put("presupuesto", pre);
            resultado.put("items", items); 

            if (logger.isDebugEnabled()) {
                logger.debug("SELECT query DAOPresupuesto.tablaToPresupuestoConItems() - correcto");
            }

        } catch (Exception ex) {
            logger.warn(DAOPresupuesto.class.getName() + ".tablaToPresupuestoConItems() Error: " + ex.getMessage(), ex);
        }

        return resultado;
    }
}
