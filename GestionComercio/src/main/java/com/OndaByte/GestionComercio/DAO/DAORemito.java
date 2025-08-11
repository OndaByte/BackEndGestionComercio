
package com.OndaByte.GestionComercio.DAO;

import com.OndaByte.GestionComercio.modelo.ItemRemito;
import com.OndaByte.GestionComercio.modelo.Orden;
import com.OndaByte.GestionComercio.modelo.Remito;
import com.OndaByte.GestionComercio.modelo.Cliente;
import org.sql2o.Connection;
import org.sql2o.Sql2oException;
import org.sql2o.Query;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.data.Row;
import org.sql2o.data.Table;

public class DAORemito {

    private static final Logger logger = LogManager.getLogger(DAORemito.class.getName());

    // Alta con ítems
    public Integer altaConItems(Remito remito, List<ItemRemito> items) {
        Connection con=null;
        
        String selectMaxNroRemito = "SELECT IFNULL(MAX(nro_remito), 0) + 1 AS nuevo_nro FROM Remito";
            
        String insertRemito = """ 
                INSERT INTO Remito ( 
                fecha_emision, 
                fecha_pago, 
                nro_remito, 
                punto_venta, 
                total,
                orden_id, 
                cliente_id, 
                cliente_cuit_cuil, 
                cliente_nombre,  
                cliente_domicilio,
                cliente_localidad, 
                cliente_telefono,
                observaciones 
                ) VALUES ( 
                :fecha_emision,
                :fecha_pago,
                :nro_remito,
                :punto_venta,
                :total, 
                :orden_id,
                :cliente_id, 
                :cliente_cuit_cuil, 
                :cliente_nombre,  
                :cliente_domicilio, 
                :cliente_localidad, 
                :cliente_telefono, 
                :observaciones
                )
                """;
        try {
            con = ConexionSQL2o.getSql2o().beginTransaction();
            Integer nuevoNroRemito = con.createQuery(selectMaxNroRemito)
                                        .executeScalar(Integer.class);
            remito.setNro_remito(nuevoNroRemito);
            
            Integer remitoId = con.createQuery(insertRemito, true)
                    .addParameter("fecha_emision", remito.getFecha_emision())
                    .addParameter("fecha_pago", remito.getFecha_pago())
                    .addParameter("nro_remito", remito.getNro_remito())
                    .addParameter("punto_venta", remito.getPunto_venta())
                    .addParameter("total", remito.getTotal())
                    .addParameter("orden_id", remito.getOrden_id())
                    .addParameter("cliente_id", remito.getCliente_id())
                    .addParameter("cliente_cuit_cuil", remito.getCliente_cuit_cuil())
                    .addParameter("cliente_nombre", remito.getCliente_nombre())
                    .addParameter("cliente_domicilio", remito.getCliente_domicilio())
                    .addParameter("cliente_localidad", remito.getCliente_localidad())
                    .addParameter("cliente_telefono", remito.getCliente_telefono())
                    .addParameter("observaciones", remito.getObservaciones())
                    .executeUpdate()
                    .getKey(Integer.class);

            this.altaItems(con,remitoId,items);

            con.commit();
            
            logger.debug("Remito y items insertados con ID: " + remitoId);
            return remitoId;

        }  catch (Sql2oException e) {
            if (con != null) {
                con.rollback();
                logger.warn("Rollback ejecutado por error en altaConItems()."+ e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.error("Error en altaConItems(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Conexión cerrada después de llamar a altaConItems()");
        }
        return -1;
    }

    /**
     * Borra todos los ítems de un remito y vuelve a insertarlos.
     * Usa la misma Connection para mantener la transacción abierta.
     */
    private void altaItems(Connection con, Integer remitoId, List<ItemRemito> items) {

        final String deleteItems = 
            "DELETE FROM ItemRemito WHERE remito_id = :remito_id";

        final String insertItem = 
            "INSERT INTO ItemRemito (remito_id, descripcion, cantidad, precio) " +
            "VALUES (:remito_id, :descripcion, :cantidad, :precio)";

        // Borrar
        con.createQuery(deleteItems)
           .addParameter("remito_id", remitoId)
           .executeUpdate();

        // Insertar
        for (ItemRemito item : items) {
            con.createQuery(insertItem)
               .addParameter("remito_id",  remitoId)
               .addParameter("descripcion", item.getDescripcion())
               .addParameter("cantidad",    item.getCantidad())
               .addParameter("precio",      item.getPrecio())
               .executeUpdate();
        }
    }
    
    // Buscar un remito por ID
    public Remito buscar(String id) {
        String query = "SELECT * FROM Remito WHERE id = :id AND estado = 'ACTIVO'";
        try (Connection con = ConexionSQL2o.getSql2o().open()) {
            return con.createQuery(query)
                      .addParameter("id", id)
                      .executeAndFetchFirst(Remito.class);
        } catch (Exception e) {
            logger.error("Error en buscar(): " + e.getMessage(), e);
            return null;
        }
    }

    // Listar todos los remitos
    public List<Remito> listar() {
        String query = "SELECT * FROM Remito WHERE estado = 'ACTIVO' ORDER BY id DESC";
        try (Connection con = ConexionSQL2o.getSql2o().open()) {
            return con.createQuery(query).executeAndFetch(Remito.class);
        } catch (Exception e) {
            logger.error("Error en listar(): " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // Listar remitos por orden_id
    public List<Remito> listarPorOrden(Integer orden_id) {
        String query = "SELECT * FROM Remito WHERE orden_id = :orden_id AND estado = 'ACTIVO' ORDER BY id DESC";
        try (Connection con = ConexionSQL2o.getSql2o().open()) {
            return con.createQuery(query)
                      .addParameter("orden_id", orden_id)
                      .executeAndFetch(Remito.class);
        } catch (Exception e) {
            logger.error("Error en listarPorOrden(): " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public boolean baja(Integer id) {
        String query = "UPDATE Remito SET estado = 'INACTIVO' WHERE id = :id";
        try (Connection con = ConexionSQL2o.getSql2o().open()) {
            return con.createQuery(query)
                      .addParameter("id", id)
                      .executeUpdate()
                      .getResult() > 0;
        } catch (Exception e) {
            logger.error("Error en baja(): " + e.getMessage(), e);
            return false;
        }
    }

    
     // Alta con ítems
    public boolean modificarConItems(Remito remito, List<ItemRemito> items) {
        Connection con=null;            
        String insertRemito = "UPDATE Remito SET "
                                + " total = :total,"
                                + " observaciones = :observaciones"
                                + " WHERE id = :id;";
        try {
            con = ConexionSQL2o.getSql2o().beginTransaction();
            
            boolean modif = con.createQuery(insertRemito)
                    .addParameter("total", remito.getTotal())
                    .addParameter("observaciones", remito.getObservaciones())
                    .addParameter("id", remito.getId())
                    .executeUpdate()
                    .getResult() > 0;

            this.altaItems(con,remito.getId(),items);

            con.commit();
            
            logger.debug("Remito y items insertados con ID: " + remito.getId());
            return modif;

        }  catch (Sql2oException e) {
            if (con != null) {
                con.rollback();
                logger.warn("Rollback ejecutado por error en modificarConItems()."+ e.getMessage(), e);
            }
        } catch (Exception e) {
            if (con != null) {
                con.rollback(); 
            }            logger.error("Error en modificarConItems(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); 
            }
            logger.debug("Conexión cerrada después de llamar a modificarConItems()");
        }
        return false;
    }

    
    // Modificar observaciones o total (ejemplo)
    public boolean modificar(Remito remito) {
        String query = "UPDATE Remito SET "
                + " fecha_emision = :fecha_emision,"
                + " fecha_pago = :fecha_pago,"
                + " nro_remito = :nro_remito,"
                + " punto_venta = :punto_venta,"
                + " total = :total,"
                + " orden_id = :orden_id,"
                + " cliente_id = :cliente_id,"
                + " cliente_cuit_cuil = :cliente_cuit_cuil,"
                + " cliente_nombre = :cliente_nombre,"
                + " cliente_domicilio = :cliente_domicilio,"
                + " cliente_localidad = :cliente_localidad,"
                + " cliente_telefono = :cliente_telefono,"
                + " observaciones = :observaciones"
                + " WHERE id = :id;";
        try (Connection con = ConexionSQL2o.getSql2o().open()) {
            return con.createQuery(query)
                    .addParameter("id", remito.getId())
                    .addParameter("fecha_emision", remito.getFecha_emision())
                    .addParameter("fecha_pago", remito.getFecha_pago())
                    .addParameter("nro_remito", remito.getNro_remito())
                    .addParameter("punto_venta", remito.getPunto_venta())
                    .addParameter("total", remito.getTotal())
                    .addParameter("orden_id", remito.getOrden_id())
                    .addParameter("cliente_id", remito.getCliente_id())
                    .addParameter("cliente_cuit_cuil", remito.getCliente_cuit_cuil())
                    .addParameter("cliente_nombre", remito.getCliente_nombre())
                    .addParameter("cliente_domicilio", remito.getCliente_domicilio())
                    .addParameter("cliente_localidad", remito.getCliente_localidad())
                    .addParameter("cliente_telefono", remito.getCliente_telefono())
                    .addParameter("observaciones", remito.getObservaciones())
                      .executeUpdate()
                      .getResult() > 0;
        } catch (Exception e) {
            logger.error("Error en modificar(): " + e.getMessage(), e);
            return false;
        }
    }

    // Listar ítems de un remito
    public List<ItemRemito> listarItems(Integer remito_id) {
        String query = "SELECT * FROM ItemRemito WHERE remito_id = :remito_id";
        try (Connection con = ConexionSQL2o.getSql2o().open()) {
            return con.createQuery(query)
                      .addParameter("remito_id", remito_id)
                      .executeAndFetch(ItemRemito.class);
        } catch (Exception e) {
            logger.error("Error en listarItems(): " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Devuelve Remito, Cliente y Orden:
     * Presupuesto si existe.
     * @param filtro Filtra por nombre de cliente o por descripción de remito
     * @param desde Fecha desde creado remito.
     * @param hasta Fecha hasta creado remito
     * @param estadoRemito estado_remito
     * @param pagina Numero de página
     * @param tamPag Tamaño de página
     * @return Respuesta con data + paginado.
     *
     */
    public HashMap<String, Object> filtrarDetalladoOP(String filtro, String desde, String hasta, String estadoRemito, Integer pagina, Integer tamPag) {
        String select = " SELECT r.id AS rid, "
                + " r.fecha_emision rfecha_emision, "
                + " r.fecha_pago rfecha_pago, "
                + " r.nro_remito rnro_remito,"
                + " r.punto_venta rpunto_venta,"
                + " r.total rtotal, "
                + " r.observaciones robservaciones, "
                + " r.orden_id rorden_id, "
                + " r.cliente_id rcliente_id, "
                + " r.cliente_cuit_cuil rcliente_cuit_cuil, "
                + " r.cliente_nombre rcliente_nombre, "
                + " r.cliente_domicilio rcliente_domicilio, "
                + " r.cliente_localidad rcliente_localidad, "
                + " r.cliente_telefono rcliente_telefono, "
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
                + " o.id AS oid, "
                + " o.descripcion odescripcion, "
                + " o.estado_orden oestado_orden, "
                + " o.tipo otipo, " 
                + " o.fecha_fin ofecha_fin, "
                + " o.creado ocreado "
                /*
                + " pre.id preid,"
                + " pre.nombre prenombre,"
                + " pre.descripcion predescripcion,"
                + " pre.estado_presupuesto preestado_presupuesto"
                 */
                ;

        String from = " FROM Remito r JOIN Cliente c ON r.cliente_id=c.id "
                + " LEFT JOIN Orden o ON r.orden_id=o.id "
                //+ " LEFT JOIN Presupuesto pre ON r.presupuesto_id = pre.pedido_id"
                ;

        String where = " WHERE r.estado = 'ACTIVO' "
                + " AND c.estado = 'ACTIVO' "
                + " AND o.estado = 'ACTIVO' "
                //+ " AND ( pre.id IS NULL OR ( pre.id IS NOT NULL AND pre.estado='ACTIVO' ) )"
                ;

        if(filtro!=null){
            where += " AND (c.nombre LIKE :filtro OR r.nro_remito LIKE :filtro)";
        }
        if(desde != null && hasta !=null){
            where += " AND (r.creado BETWEEN :desde AND :hasta)";
        }
        if(estadoRemito !=null){
            where += " AND r.estado_remito = :estadoRemito";
        }

        String orden = " ORDER BY rid DESC " ;
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
            if(estadoRemito !=null){
                cq.addParameter("estadoRemito", estadoRemito);
                dq.addParameter("estadoRemito", estadoRemito);
            }

            totalElementos = cq.executeAndFetchFirst(Integer.class);
            totalPaginas = (int) Math.ceil((double) totalElementos / tamPag);
            logger.debug("Count query ejecutada: " + cq.toString());
            logger.debug("Datos query por ejecutar: " + dq.toString());

            data = tablaToRemitosDetalladoOP(dq
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
            logger.error("Error SQL en filtrarDetalladoOP(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error en filtrarDetalladoOP(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Conexión cerrada después de llamar a filtrarDetalladoOP()");
        }
        return null;
    }

    private List<HashMap<String, Object>> tablaToRemitosDetalladoOP(Table tabla) {
        List<HashMap<String, Object>> filas = new ArrayList<>();

        try {
            for (Row row : tabla.rows()) {
                // Pedido
                Remito r = new Remito();
                r.setId(row.getInteger("rid"));
                r.setFecha_emision(row.getString("rfecha_emision"));
                r.setFecha_pago(row.getString("rfecha_pago"));
                r.setNro_remito(row.getInteger("rnro_remito"));
                r.setPunto_venta(row.getString("rpunto_venta"));
                r.setOrden_id(row.getInteger("rorden_id"));
                r.setCliente_id(row.getInteger("rcliente_id"));
                r.setCliente_cuit_cuil(row.getString("rcliente_cuit_cuil"));
                r.setCliente_nombre(row.getString("rcliente_nombre"));
                r.setCliente_domicilio(row.getString("rcliente_domicilio"));
                r.setCliente_localidad(row.getString("rcliente_localidad"));
                r.setCliente_telefono(row.getString("rcliente_telefono"));
                r.setObservaciones(row.getString("robservaciones"));
                r.setTotal(row.getFloat("rtotal"));
                // Cliente
                Cliente c = new Cliente();
                c.setId(row.getInteger("cid"));
                c.setNombre(row.getString("cnombre"));
                c.setEmail(row.getString("cemail"));
                c.setTelefono(row.getString("ctelefono"));
                c.setDireccion(row.getString("cdireccion"));
                c.setDni(row.getString("cdni"));
                c.setCuit_cuil(row.getString("ccuit_cuil"));
                c.setLocalidad(row.getString("clocalidad"));
                c.setCodigo_postal(row.getString("ccodigo_postal"));
                c.setProvincia(row.getString("cprovincia"));
                c.setCond_iva(row.getString("ccond_iva"));

                // Turno (puede no existir)
                Orden o = new Orden();
                o.setId(row.getInteger("oid"));
                o.setDescripcion(row.getString("odescripcion"));
                o.setEstado_orden(row.getString("oestado_orden"));
                o.setTipo(row.getString("otipo")); 
                o.setFecha_fin(row.getString("ofecha_fin"));
                o.setCreado(row.getString("ocreado"));

                /*
                // Presupuesto
                 Presupuesto pre = new Presupuesto();
                 pre.setId(row.getInteger("preid"));
                 pre.setNombre(row.getString("prenombre"));
                 pre.setDescripcion(row.getString("predescripcion"));
                 pre.setEstado_presupuesto(row.getString("preestado_presupuesto"));
                 pre.setTotal(row.getFloat("pretotal"));
                 pre.setCreado(row.getString("precreado"));
                 pre.setUltMod(row.getString("preultMod"));
                 */

                // Armar fila
                HashMap<String, Object> fila = new HashMap<>();
                fila.put("remito", r);
                fila.put("orden", o);
                fila.put("cliente", c);

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

    /**
     * Deje el otro pq por ahí queres buscarlo nomas para saber si existe nomas y re sirve.. livianito
     * @param id
     * @return
     */
    public HashMap<String,Object> buscarDetallado(String id) {
         Connection con = null;
        String query = "SELECT "
                + " r.id AS rid, "
                + " r.fecha_emision rfecha_emision, "
                + " r.fecha_pago rfecha_pago, "
                + " r.nro_remito rnro_remito,"
                + " r.punto_venta rpunto_venta,"
                + " r.total rtotal, "
                + " r.observaciones robservaciones, "
                + " r.orden_id rorden_id, "
                + " r.cliente_id rcliente_id, "
                + " r.cliente_cuit_cuil rcliente_cuit_cuil, "
                + " r.cliente_nombre rcliente_nombre, "
                + " r.cliente_domicilio rcliente_domicilio, "
                + " r.cliente_localidad rcliente_localidad, "
                + " r.cliente_telefono rcliente_telefono, "

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

                + " ir.id AS irid, " 
                + " ir.creado AS ircreado, " 
                + " ir.ultMod AS irultMod, " 
                + " ir.estado AS irestado, " 
                + " ir.descripcion AS irdescripcion, "
                + " ir.precio AS irprecio, " 
                + " ir.cantidad AS ircantidad, "
                
                + " o.id AS oid, "
                + " o.descripcion odescripcion, "
                + " o.estado_orden oestado_orden, "
                + " o.tipo otipo, "
                + " o.fecha_fin ofecha_fin, "
                + " o.creado ocreado "

                + "FROM Remito r "
                + "JOIN Cliente c ON r.cliente_id = c.id "
                + "JOIN Orden o ON r.orden_id = o.id "
                + "JOIN ItemRemito ir ON r.id = ir.remito_id "
                + "WHERE r.estado = 'ACTIVO' "
                + "AND c.estado = 'ACTIVO' "
                + "AND o.estado = 'ACTIVO' "
                + "AND ir.estado = 'ACTIVO' "
                + "AND r.id = :id;";

        try {
            con = ConexionSQL2o.getSql2o().open();
            return tablaToRemitoConItems(
                    con.createQuery(query).addParameter("id", id).executeAndFetchTable()
            );
        } catch (Exception e) {
            logger.error("Error inesperado en DAORemito.buscarDetallado(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Conexión cerrada después de llamar a DAORemito.buscarDetallado()");
        }
        return null;
    }

    private HashMap<String, Object> tablaToRemitoConItems(Table tabla) {
        HashMap<String, Object> resultado = new HashMap<>();
        List<ItemRemito> items = new ArrayList<>();
        Remito r = null;
        Orden o = null;
        Cliente c = null;
        try {
            for (Row row : tabla.rows()) {

                // Solo se instancia una vez el presupuesto (se repite en cada fila)
                if (r == null) {
                    r = new Remito();
                    r.setId(row.getInteger("rid"));
                    r.setFecha_emision(row.getString("rfecha_emision"));
                    r.setFecha_pago(row.getString("rfecha_pago"));
                    r.setNro_remito(row.getInteger("rnro_remito"));
                    r.setPunto_venta(row.getString("rpunto_venta"));
                    r.setOrden_id(row.getInteger("rorden_id"));
                    r.setCliente_id(row.getInteger("rcliente_id"));
                    r.setCliente_cuit_cuil(row.getString("rcliente_cuit_cuil"));
                    r.setCliente_nombre(row.getString("rcliente_nombre"));
                    r.setCliente_domicilio(row.getString("rcliente_domicilio"));
                    r.setCliente_localidad(row.getString("rcliente_localidad"));
                    r.setCliente_telefono(row.getString("rcliente_telefono"));
                    r.setObservaciones(row.getString("robservaciones"));
                    r.setTotal(row.getFloat("rtotal"));
                }


                if (o == null) {
                    o = new Orden();
                    o.setId(row.getInteger("oid"));
                    o.setDescripcion(row.getString("odescripcion"));
                    o.setEstado_orden(row.getString("oestado_orden"));
                    o.setTipo(row.getString("otipo"));
                    o.setFecha_fin(row.getString("ofecha_fin"));
                    o.setCreado(row.getString("ocreado"));
                }
                
                if (c  == null) {
                    c = new Cliente();
                    c.setId(row.getInteger("cid"));
                    c.setNombre(row.getString("cnombre"));
                    c.setEmail(row.getString("cemail"));
                    c.setTelefono(row.getString("ctelefono"));
                    c.setDireccion(row.getString("cdireccion"));
                    c.setDni(row.getString("cdni"));
                    c.setCuit_cuil(row.getString("ccuit_cuil"));
                    c.setLocalidad(row.getString("clocalidad"));
                    c.setCodigo_postal(row.getString("ccodigo_postal"));
                    c.setProvincia(row.getString("cprovincia"));
                    c.setCond_iva(row.getString("ccond_iva"));
                }

                ItemRemito ir = new ItemRemito();
                ir.setId(row.getInteger("irid"));
                ir.setCreado(row.getString("ircreado"));
                ir.setUltMod(row.getString("irultMod")); 
                ir.setEstado(row.getString("irestado"));
                ir.setDescripcion(row.getString("irdescripcion"));
                ir.setPrecio(row.getFloat("irprecio"));
                ir.setCantidad(row.getInteger("ircantidad"));
                ir.setRemito_id(row.getInteger("rid"));
                items.add(ir);
            }

            resultado.put("remito", r);
            resultado.put("cliente", c);
            resultado.put("orden", o);
            resultado.put("items", items);

            if (logger.isDebugEnabled()) {
                logger.debug("tablaToRemitoConItems() - correcto");
            }

        } catch (Exception ex) {
            logger.warn(DAOPresupuesto.class.getName() + "tablaToRemitoConItems() Error: " + ex.getMessage(), ex);
        }

        return resultado;
    }
}
