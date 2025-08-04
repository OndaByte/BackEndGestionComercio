package com.OndaByte.GestionComercio.DAO;

import com.OndaByte.GestionComercio.modelo.Cliente;
import com.OndaByte.GestionComercio.modelo.Pedido;
import com.OndaByte.GestionComercio.modelo.Presupuesto;
import com.OndaByte.GestionComercio.modelo.Turno;
import java.util.ArrayList;

import java.util.List;

import org.sql2o.Query;
import org.sql2o.Sql2oException;
import org.sql2o.data.Row;
import org.sql2o.data.Table;
import org.sql2o.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;

public class DAOPedido implements DAOInterface<Pedido> {

    private static Logger logger = LogManager.getLogger(DAOPedido.class.getName());

    private String clave = "id";
    private GeneradorQuery<Pedido> generadorQuery;

    public DAOPedido() {
        generadorQuery = new GeneradorQuery<Pedido>(Pedido.class);
        generadorQuery.setClave(clave);
    }

    public Class<Pedido> getClase() {
        return Pedido.class;
    }

    @Override
    public Integer alta(Pedido t) {
        String query;
        Connection con = null;
        try {
            con = ConexionSQL2o.getSql2o().open();
            query = "SELECT EXISTS (SELECT * FROM Cliente WHERE id = :cliente_id)";

            Query auxQuery = con.createQuery(query).addParameter("cliente_id", t.getCliente_id());
            if (auxQuery.executeAndFetchFirst(Integer.class) == 0) {
                return -1;
            }

            query = generadorQuery.getQueryAlta(t);
            logger.debug(query);

            auxQuery = con.createQuery(query).bind(t);
            // auxQuery.addParameter("fecha_fin_estimada", t.getFecha_fin_estimada() != null ? t.getFecha_fin_estimada() : null);
            return auxQuery.executeUpdate().getKey(Integer.class);
        } catch (Sql2oException e) {
            logger.error("Error SQL en DAOPedido.alta(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOPedido.alta(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOPedido.alta()");
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
            logger.error("Error SQL en DAOPedido.baja(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOPedido.baja(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOPedido.baja()");
        }
        return false;
    }
    
    public boolean modificar(Pedido p, Connection con) {
        String query;
        try {
            if(con==null)
                con = ConexionSQL2o.getSql2o().open();
            query = generadorQuery.getQueryModificar();
            
            Query auxQuery = con.createQuery(query).bind(p);
           // auxQuery.addParameter("fecha_fin_estimada", p.getFecha_fin_estimada() != null ? p.getFecha_fin_estimada() : null);
            return auxQuery.executeUpdate().getResult() > 0;
        } catch (Sql2oException e) {
            logger.error("Error SQL en DAOPedido.modificar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOPedido.modificar(): " + e.getMessage(), e);
        } 
        return false;
    }
    /**
     * @param PedidoId Integer 
     * @param turnoId Integer 
     * @return true si ok.
     */
    public boolean asignar(Integer pedidoId, Integer turnoId, Connection con) {
        String query;
        try {
            if(con == null)
                con = ConexionSQL2o.getSql2o().open();
            query = "UPDATE Pedido p SET turno_id = :turnoId, estado_pedido = 'ASIGNADO' WHERE p.id = :pedidoId;";
            return con.createQuery(query)
                    .addParameter("turnoId", turnoId)
                    .addParameter("pedidoId", pedidoId)
                    .executeUpdate().getResult() > 0;
        } catch (Sql2oException e) {
            logger.error("Error SQL en DAOPedido.asignar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOPedido.asignar(): " + e.getMessage(), e);
        }
        return false;
    }
    
    @Override
    public boolean modificar(Pedido p) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryModificar();
            con = ConexionSQL2o.getSql2o().open();

            Query auxQuery = con.createQuery(query).bind(p);
           // auxQuery.addParameter("fecha_fin_estimada", p.getFecha_fin_estimada() != null ? p.getFecha_fin_estimada() : null);
            return auxQuery.executeUpdate().getResult() > 0;
        } catch (Sql2oException e) {
            logger.error("Error SQL en DAOPedido.modificar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOPedido.modificar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOPedido.modificar()");
        }
        return false;
    }

    @Override
    public List<Pedido> listar() {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryListar();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        } catch (Sql2oException e) {
            logger.error("Error SQL en DAOPedido.listar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOPedido.listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOPedido.listar()");
        }
        return null;
    }
    /**
     * Devuelve Pedido, Cliente y Turno:
     * Presupuesto si existe.
     * @param filtro Filtra por nombre de cliente o por descripción de pedido
     * @param desde Fecha desde creado pedido.
     * @param hasta Fecha hasta creado pedido
     * @param estadoPedido estado_pedido
     * @param pagina Numero de página
     * @param tamPag Tamaño de página
     * @return Respuesta con data + paginado.
     * - El Orden es fijo por ID decreciente.
     */
    public HashMap<String, Object> filtrarDetalladoOP(String filtro,String desde, String hasta,String estadoPedido, Integer pagina, Integer tamPag) {
        String select = " SELECT p.id AS pid, "
                + " p.descripcion pdescripcion, "
                + " p.cliente_id pcliente_id, "
                + " p.fecha_fin_estimada pfecha_fin_estimada, "
                + " p.estado_pedido pestado_pedido,"
                + " p.creado pcreado,"
                + " t.id tid,"
                + " t.tipo ttipo,"
                + " t.prioridad tprioridad,"
                + " t.observaciones tobservaciones,"
                + " t.fecha_inicio tfecha_inicio,"
                + " t.fecha_fin_e tfecha_fin_e,"
                + " t.estado_turno testado_turno,"
                + " t.patron_repeticion tpatron_repeticion,"
                + " c.id AS cid, "
                + " c.nombre cnombre, "
                + " c.email cemail,"
                + " c.telefono ctelefono, "
                + " c.direccion cdireccion, "
                + " c.dni cdni, "
                + " c.cuit_cuil ccuit_cuil, "
                + " c.localidad clocalidad, "
                + " c.codigo_postal ccodigo_postal, "
                + " c.provincia cprovincia, "
                + " c.cond_iva ccond_iva, "
                + " pre.id preid,"
                + " pre.nombre prenombre,"
                + " pre.descripcion predescripcion,"
                + " pre.estado_presupuesto preestado_presupuesto"
                ;

        String from = " FROM Pedido p JOIN Cliente c ON p.cliente_id=c.id "
                + " LEFT JOIN Turno t ON p.turno_id=t.id "
                + " LEFT JOIN Presupuesto pre ON p.presupuesto_id = pre.pedido_id";
        
        String where = " WHERE p.estado = 'ACTIVO' "
                + " AND c.estado = 'ACTIVO' "
                + " AND ( t.id IS NULL OR ( t.id IS NOT NULL AND t.estado='ACTIVO' ) )"
                + " AND ( pre.id IS NULL OR ( pre.id IS NOT NULL AND pre.estado='ACTIVO' ) )";
        
        if(filtro!=null){
            where += " AND (c.nombre LIKE :filtro OR p.descripcion LIKE :filtro OR c.telefono LIKE :filtro)";
        }
        if(desde != null && hasta !=null){
            where += " AND (p.creado BETWEEN :desde AND :hasta)";
        }
        if(estadoPedido !=null){
            where += " AND p.estado_pedido = :estadoPedido";
        }
        
        String orden = " ORDER BY pid DESC " ;
        String page = " LIMIT :limit OFFSET :offset"; 
        
        pagina = pagina == null || pagina < 1 ? 1 : pagina; // control unsusto
        tamPag = tamPag == null || tamPag < 1 ? 10 : Math.min(tamPag, 1000); // control unsusto 
        Connection con = null;
        Integer totalElementos = null;
        Integer totalPaginas = null;
        List<HashMap<String,Object>> data;
        try {
            HashMap<String, Object> response = new HashMap<>();
            con = ConexionSQL2o.getSql2o().open();
            Query cq = con.createQuery("SELECT COUNT(*) " + from + where);
            Query dq = con.createQuery(select + from + where + orden + page);
            
            if(filtro!=null){
                cq.addParameter("filtro","%"+ filtro +"%");
                dq.addParameter("filtro","%"+ filtro +"%");
            }
            if(desde != null && hasta !=null){
                desde +=" 00:00:00";
                hasta += " 23:59:59";
                cq.addParameter("desde", desde);
                dq.addParameter("hasta", hasta);
                cq.addParameter("hasta", hasta);
                dq.addParameter("desde", desde);
            }
            if(estadoPedido !=null){
                cq.addParameter("estadoPedido", estadoPedido);
                dq.addParameter("estadoPedido", estadoPedido);
            } 
            
            totalElementos = cq.executeAndFetchFirst(Integer.class); 
            totalPaginas = (int) Math.ceil((double) totalElementos / tamPag);
            logger.debug("Count query ejecutada: " + cq.toString());

            data = tablaToPedidosDetallado(dq
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
            logger.error("Error SQL en DAOPedido.listarPedidosYClientes(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOPedido.listarPedidosYClientes(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOPedido.listarPedidosYClientes()");
        }
        return null;
    }

    private List<HashMap<String, Object>> tablaToPedidosDetallado(Table tpc) {
        List<HashMap<String, Object>> filas = new ArrayList<HashMap<String, Object>>();
        try {
            for (Row r : tpc.rows()) {
                Pedido p = new Pedido();
                p.setId(r.getInteger("pid"));
                p.setDescripcion(r.getString("pdescripcion"));
                p.setCliente_id(r.getInteger("pcliente_id"));
                p.setFecha_fin_estimada(r.getString("pfecha_fin_estimada"));
                p.setEstado_pedido(r.getString("pestado_pedido"));
                p.setCreado(r.getString("pcreado"));

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
                t.setPatron_repeticion(r.getInteger("tpatron_repeticion"));
                t.setFecha_inicio(r.getString("tfecha_inicio"));
                t.setFecha_fin_e(r.getString("tfecha_fin_e"));
                t.setEstado_turno(r.getString("testado_turno"));

                Presupuesto pre = new Presupuesto();
                pre.setId(r.getInteger("preid"));
                pre.setNombre(r.getString("prenombre"));
                pre.setDescripcion(r.getString("predescripcion"));
                pre.setEstado_presupuesto(r.getString("preestado_presupuesto"));

                
                HashMap<String, Object> fila = new HashMap<String, Object>();
                fila.put("pedido", p);
                fila.put("cliente", c);
                fila.put("turno", t);
                fila.put("presupuesto", pre);

                filas.add(fila);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("SELECT query DAOPedido.tablaToPedidosDetallado() - correcto");
            }

        } catch (Exception ex) {
            logger.warn(DAOPedido.class.getName() + ".tablaToPedidosDetallado() Error: " + ex.getMessage(), ex);
        }

        return filas;
    }
    
    @Override
    public List<Pedido> listar(String... ids) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryListar(ids);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        } catch (Sql2oException e) {
            logger.error("Error SQL en DAOPedido.listar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOPedido.listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOPedido.listar()");
        }
        return null;
    }

    @Override
    public List<Pedido> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryFiltrar(campos, valores, condiciones, conectores);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        } catch (Sql2oException e) {
            logger.error("Error SQL en DAOPedido.filtrar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOPedido.filtrar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOPedido.filtrar()");
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
                logger.debug("SELECT query DAOInsumo.filtrarOrdenadoYPaginado(): " + queryDatos);
            }
                
            List<Pedido> result = con.createQuery(queryDatos)
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
    public Pedido buscar(String id) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryFiltarId();
            con = ConexionSQL2o.getSql2o().open();
            return (Pedido) con.createQuery(query).addParameter("id", id).executeAndFetchFirst(this.getClase());
        } catch (Sql2oException e) {
            logger.error("Error SQL en DAOPedido.buscar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en DAOPedido.buscar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOPedido.buscar()");
        }
        return null;
    }
}
