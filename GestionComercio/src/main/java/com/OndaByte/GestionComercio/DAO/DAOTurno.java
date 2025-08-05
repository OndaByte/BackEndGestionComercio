
package com.OndaByte.GestionComercio.DAO;

import com.OndaByte.GestionComercio.modelo.Cliente;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Connection;
import org.sql2o.data.Row;
import org.sql2o.data.Table;
import com.OndaByte.GestionComercio.modelo.Orden;
import com.OndaByte.GestionComercio.modelo.Pedido;
import com.OndaByte.GestionComercio.modelo.Turno;
import java.util.ArrayList;
import org.sql2o.Query;
import org.sql2o.Sql2oException;

public class DAOTurno implements DAOInterface<Turno> {

    private GeneradorQuery<Turno> generadorQuery;
    private String clave = "id";
    private static Logger logger = LogManager.getLogger(DAOTurno.class.getName());

    public DAOTurno() {
        generadorQuery = new GeneradorQuery<Turno>(Turno.class);
        generadorQuery.setClave(clave);
    }

    public Class<Turno> getClase() {
        return Turno.class;
    }

    public String getClave() {
        return this.clave;
    }

    public Integer alta(Turno t, Connection con) {
        String query;
        try {
            if(con==null)
                con = ConexionSQL2o.getSql2o().open();
            query = generadorQuery.getQueryAlta(t);
            return con.createQuery(query).bind(t).executeUpdate().getKey(Integer.class);
        } catch (Exception e) {
            logger.error("Alta(): " + e.getMessage(), e);
        }
        return -1;
    }
    
    @Override
    public Integer alta(Turno t) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryAlta(t);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).bind(t).executeUpdate().getKey(Integer.class);
        } catch (Exception e) {
            logger.error("Alta(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); 
            }
            logger.debug("Alta: Conexión cerrada");
        }
        return -1;
    }

    @Override
    public boolean modificar(Turno t) {
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
            }
            logger.debug("Modificar: Conexión cerrada");
        }
        return false;
    }

    @Override
    public boolean baja(String id, boolean borrar) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryBaja(borrar);
            logger.debug(query);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).addParameter(this.getClave(), id).executeUpdate().getResult() > 0;
        } catch (Exception e) {
            logger.error("Baja: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); 
            }
            logger.debug("Baja: Conexión cerrada");
        }
        return false;
    }

    @Override
    public List<Turno> listar() {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryListar();
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
    public List<Turno> listar(String... ids) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryListar(ids);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        } catch (Exception e) {
            logger.error("Listar(string[]): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); 
            }
            logger.debug("Listar(string[]): Conexión cerrada");
        }
        return null;
    }

    @Override
    public List<Turno> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryFiltrar(campos, valores, condiciones, conectores);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        } catch (Exception e) {
            logger.error("Filtrar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); 
            }
            logger.debug("Filtrar: Conexión cerrada");
        }
        return null;
    }
    
    
    
    /**
     * Devuelve Pedido, Cliente y Turno:
     * Presupuesto si existe.
     * para elarchivo, así se hacía sin la columna tipo ... mas feo mas joins y menos escalable pero posible
     * FROM
            Turno t
          -- Intentar traer Pedido que referencia el Turno
          LEFT JOIN Pedido p
            ON p.turno_id = t.id AND p.estado = 'ACTIVO'

          -- Intentar traer Orden que referencia el Turno
          LEFT JOIN Orden o
            ON o.turno_id = t.id AND o.estado = 'ACTIVO'

          -- Traer el Pedido que referencia la Orden
          LEFT JOIN Pedido po
            ON po.id = o.pedido_id AND po.estado = 'ACTIVO'

          -- Cliente del Pedido directo o del Pedido de la Orden
          LEFT JOIN Cliente c
            ON (
              (p.id IS NOT NULL AND c.id = p.cliente_id)
              OR
              (p.id IS NULL AND po.id IS NOT NULL AND c.id = po.cliente_id)
            )
     * @param filtro Filtra por nombre de cliente o por descripción de pedido
     * @param desde Fecha desde creado pedido.
     * @param hasta Fecha hasta creado pedido
     * @param tipoTurno t.tipo
     * @param pagina Numero de página
     * @param tamPag Tamaño de página
     * @return Respuesta con data + paginado.
     * - El Orden es fijo por ID decreciente.
     */
    public HashMap<String, Object> filtrarDetalladoOP(String filtro,String desde, String hasta,String tipoTurno, Integer pagina, Integer tamPag) {
        String select = " SELECT t.id tid,"
                + " t.tipo ttipo,"
                + " t.prioridad tprioridad,"
                + " t.observaciones tobservaciones,"
                + " t.fecha_inicio tfecha_inicio,"
                + " t.fecha_fin_e tfecha_fin_e,"
                + " t.patron_repeticion tpatron_repeticion,"
                + " t.estado_turno testado_turno,"

                + " o.id oid, " 
                + " o.descripcion odescripcion, " 
                + " o.estado_orden oestado_orden, " 
                + " o.tipo otipo, " 
                + " o.fecha_fin ofecha_fin, " 
                + " o.creado ocreado, " 

                // Pedido directo
                + " p.id pid,"
                + " p.descripcion pdescripcion, "
                + " p.cliente_id pcliente_id, "
                + " p.fecha_fin_estimada pfecha_fin_estimada, "
                + " p.estado_pedido pestado_pedido,"
                + " p.creado pcreado,"
                
                // Pedido de la Orden
                + " po.id poid,"
                + " po.descripcion podescripcion, "
                + " po.cliente_id pocliente_id, "
                + " po.fecha_fin_estimada pofecha_fin_estimada, "
                + " po.estado_pedido poestado_pedido,"
                + " po.creado pocreado,"
                
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
                + " c.cond_iva ccond_iva "
                ;
        //
        String from = """
                      FROM
                        Turno t
                      LEFT JOIN Pedido p
                        ON (t.tipo = 'INSPECCION' AND p.turno_id = t.id AND p.estado = 'ACTIVO')
                      
                      LEFT JOIN Orden o
                        ON (t.tipo <> 'INSPECCION' AND o.turno_id = t.id AND o.estado = 'ACTIVO')
                      
                      -- Pedido asociado a la Orden
                      LEFT JOIN Pedido po
                        ON (t.tipo <> 'INSPECCION' AND po.id = o.pedido_id AND po.estado = 'ACTIVO')
                      
                      -- Cliente (del pedido directo o de la orden)
                      LEFT JOIN Cliente c
                        ON (
                          (t.tipo = 'INSPECCION' AND c.id = p.cliente_id AND c.estado = 'ACTIVO')
                          OR
                          (t.tipo <> 'INSPECCION' AND c.id = po.cliente_id AND c.estado = 'ACTIVO')
                        )
                    """; // Java twenty chu
        
        String where = """
                        WHERE
                           t.estado = 'ACTIVO' 
                        """;
        
        if(filtro!=null){
            where += " AND (c.nombre LIKE :filtro OR p.descripcion LIKE :filtro)";
        }
        if(desde != null && hasta !=null){
            where += " AND (t.fecha_inicio BETWEEN :desde AND :hasta)";
        }
        if(tipoTurno !=null){
            where += " AND t.tipo = :tipoTurno";
        }
        
        String orden = " ORDER BY tfecha_inicio DESC " ;
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
            if(tipoTurno !=null){
                cq.addParameter("tipoTurno", tipoTurno);
                dq.addParameter("tipoTurno", tipoTurno);
            } 
                        logger.debug("Datos query ejecutada: " + dq.toString());

            totalElementos = cq.executeAndFetchFirst(Integer.class); 
            totalPaginas = (int) Math.ceil((double) totalElementos / tamPag);
            logger.debug("Count query ejecutada: " + cq.toString());

            data = tablaToTurnosDetallado(dq
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
            logger.error("Error SQL filtrarDetalladoOP(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en filtrarDetalladoOP(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOPedido.listarPedidosYClientes()");
        }
        return null;
    }
    
    private List<HashMap<String, Object>> tablaToTurnosDetallado(Table tpc) {
        List<HashMap<String, Object>> filas = new ArrayList<HashMap<String, Object>>();
        try {
            for (Row r : tpc.rows()) {
                Turno t = new Turno();
                Pedido p = null;
                Orden o = null;
                
                t.setId(r.getInteger("tid"));
                t.setTipo(r.getString("ttipo"));
                t.setObservaciones(r.getString("tobservaciones"));
                t.setPrioridad(r.getInteger("tprioridad"));
                t.setPatron_repeticion(r.getInteger("tpatron_repeticion"));
                t.setFecha_inicio(r.getString("tfecha_inicio"));
                t.setFecha_fin_e(r.getString("tfecha_fin_e"));
                t.setEstado_turno(r.getString("testado_turno"));

                if (r.getInteger("oid") != null) {
                    o = new Orden();

                    o.setId(r.getInteger("oid"));
                    o.setDescripcion(r.getString("odescripcion"));
                    o.setEstado_orden(r.getString("oestado_orden"));
                    o.setTipo(r.getString("otipo"));
//                    o.setPrecio_final(r.getFloat("oprecio_final"));
//                    o.setCosto_total(r.getFloat("ocosto_total"));
                    o.setFecha_fin(r.getString("ofecha_fin"));
                    o.setCreado(r.getString("ocreado"));
                }else{                    
                    p = new Pedido();

                    p.setId(r.getInteger("pid"));
                    p.setDescripcion(r.getString("pdescripcion"));
                    p.setCliente_id(r.getInteger("pcliente_id"));
                    p.setFecha_fin_estimada(r.getString("pfecha_fin_estimada"));
                    p.setEstado_pedido(r.getString("pestado_pedido"));
                    p.setCreado(r.getString("pcreado"));
                }
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

                
                HashMap<String, Object> fila = new HashMap<String, Object>();
                fila.put("orden", o);
                fila.put("pedido", p);
                fila.put("cliente", c);
                fila.put("turno", t);

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
    
    public HashMap<String, Object> filtrarOP(
            List<String> campos,List<String> valores, List<Integer> condiciones, List<Boolean> conectores,
            Integer pagina, Integer elementos) {
        //init
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
            //count
            String count = generadorQuery.getCountQuery(campos, valores, condiciones, conectores);

            pagina = pagina == null || pagina < 1 ? 1 : pagina;
            elementos = elementos == null || elementos < 1 ? 10 : Math.min(elementos, 1000);

            totalElementos = con.createQuery(count).executeAndFetchFirst(Integer.class); // Query extra
            totalPaginas = (int) Math.ceil((double) totalElementos / elementos);
            
            String queryDatos = generadorQuery.getQueryFiltrarOrdenadoYPaginado(campos, valores, condiciones, conectores, "DESC"); // por ahora fijo

            if (logger.isDebugEnabled()) {
                logger.debug("SELECT query DAOTurno.filtrarOP(): " + queryDatos);
            }
            //rows
            List<Turno> result = con.createQuery(queryDatos)
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
            logger.error("-0 filtrarOrdenadoYPaginado(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("-1 filtrarOrdenadoYPaginado(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); 
            }
            logger.debug("Conexión cerrada después de llamar a DAOTurno.filtrarOP()");
        }
        return null;
    }    
    
    
	@Override
	public Turno buscar(String id) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryFiltarId();
            con = ConexionSQL2o.getSql2o().open();
            return (Turno) con.createQuery(query).addParameter("id", id).executeAndFetchFirst(this.getClase());
        }  catch (Sql2oException e) {
            logger.error("Buscar(id): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Buscar(id): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); 
            }
            logger.debug("Buscar(id): Conexión cerrada");
        }
        return null;
	}

    
    public Integer cantEstado(String estado){
        String query = "SELECT count(*) FROM Turno WHERE Turno.estado_turno = :estado";
        Connection con = null;
        try {
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).addParameter("estado",estado).executeScalar(Integer.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
