package com.OndaByte.GestionComercio.DAO;

import com.OndaByte.GestionComercio.modelo.ItemVenta;
import com.OndaByte.GestionComercio.modelo.Movimiento;
import com.OndaByte.GestionComercio.modelo.Venta;
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

public class DAOVenta {

    private static final Logger logger = LogManager.getLogger(DAOVenta.class.getName());

    // Alta de Venta con ítems
    public Integer altaConItems(Venta venta, List<ItemVenta> items) {
        Connection con = null;

        String selectMaxNroComprobante = "SELECT IFNULL(MAX(nro_comprobante), 0) + 1 AS nuevo_nro FROM Venta";

        String insertVenta = """
                INSERT INTO Venta (
                    subtotal,
                    porcentaje_descuento,
                    total,
                    forma_pago,
                    punto_venta,
                    nro_comprobante,
                    observaciones
                ) VALUES (
                    :subtotal,
                    :porcentaje_descuento,
                    :total,
                    :forma_pago,
                    :punto_venta,
                    :nro_comprobante,
                    :observaciones
                )
                """;

        try {
            con = ConexionSQL2o.getSql2o().beginTransaction();

            // Calcular nuevo número de comprobante
            Integer nuevoNroComprobante = con.createQuery(selectMaxNroComprobante)
                    .executeScalar(Integer.class);
            venta.setNro_comprobante(nuevoNroComprobante);

            // Insertar la venta
            Integer ventaId = con.createQuery(insertVenta, true)
                    .addParameter("subtotal", venta.getSubtotal())
                    .addParameter("porcentaje_descuento", venta.getPorcentaje_descuento())
                    .addParameter("total", venta.getTotal())
                    .addParameter("forma_pago", venta.getForma_pago())
                    .addParameter("punto_venta", venta.getPunto_venta())
                    .addParameter("nro_comprobante", venta.getNro_comprobante())
                    .addParameter("observaciones", venta.getObservaciones())
                    .executeUpdate()
                    .getKey(Integer.class);

            // Alta de ítems vinculados a la venta
            this.altaItems(con, ventaId, items);

            con.commit();

            logger.debug("Venta e ítems insertados con ID: " + ventaId);
            return ventaId;

        } catch (Sql2oException e) {
            if (con != null) {
                con.rollback();
                logger.warn("Rollback ejecutado por error en altaConItems()." + e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.error("Error en altaConItems(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
            }
            logger.debug("Conexión cerrada después de altaConItems()");
        }
        return -1;
    }

    public Integer altaConMovimientoEItems(Venta venta, List<ItemVenta> items) {
        Connection con = null;

        String selectSesionAbierta
                = "SELECT id FROM SesionCaja WHERE cierre IS NULL ORDER BY apertura DESC LIMIT 1";

        String selectMaxNroComprobante
                = "SELECT IFNULL(MAX(nro_comprobante), 0) + 1 AS nuevo_nro FROM Venta";

        String insertMovimiento = """
            INSERT INTO Movimiento (
                cliente_id, sesion_caja_id, tipo_mov, descripcion, total
            ) VALUES (
                :cliente_id, :sesion_caja_id, 'VENTA', :descripcion, :total
            )
            """;

        String insertVenta = """
            INSERT INTO Venta (
                movimiento_id,
                cliente_id,
                subtotal,
                porcentaje_descuento,
                total,
                forma_pago,
                punto_venta,
                nro_comprobante,
                observaciones
            ) VALUES (
                :movimiento_id,
                :cliente_id,
                :subtotal,
                :porcentaje_descuento,
                :total,
                :forma_pago,
                :punto_venta,
                :nro_comprobante,
                :observaciones
            )
            """;

        try {
            con = ConexionSQL2o.getSql2o().beginTransaction();

            // 0) Validaciones mínimas
            if (items == null || items.isEmpty()) {
                throw new IllegalArgumentException("La venta debe tener al menos un ítem.");
            }

            // 1) Sesión de caja abierta
            Integer sesionId = con.createQuery(selectSesionAbierta)
                    .executeScalar(Integer.class);
            if (sesionId == null) {
                throw new IllegalStateException("No hay sesión de caja abierta.");
            }

            // 2) Nro de comprobante
            Integer nuevoNroComprobante = con.createQuery(selectMaxNroComprobante)
                    .executeScalar(Integer.class);
            venta.setNro_comprobante(nuevoNroComprobante);

            // 3) Insertar Movimiento (VENTA)
            String descMov = "Venta "
                    + (venta.getPunto_venta() != null ? venta.getPunto_venta() : "")
                    + "-"
                    + (venta.getNro_comprobante() != null ? venta.getNro_comprobante() : "");
            Integer movimientoId = con.createQuery(insertMovimiento, true)
                    .addParameter("cliente_id", venta.getCliente_id()) // puede ser NULL
                    .addParameter("sesion_caja_id", sesionId) // NOT NULL
                    .addParameter("descripcion", descMov)
                    .addParameter("total", venta.getTotal())
                    .executeUpdate()
                    .getKey(Integer.class);

            // 4) Insertar Venta con movimiento_id
            Integer ventaId = con.createQuery(insertVenta, true)
                    .addParameter("movimiento_id", movimientoId) // NOT NULL
                    .addParameter("cliente_id", venta.getCliente_id()) // puede ser NULL
                    .addParameter("subtotal", venta.getSubtotal())
                    .addParameter("porcentaje_descuento", venta.getPorcentaje_descuento())
                    .addParameter("total", venta.getTotal())
                    .addParameter("forma_pago", venta.getForma_pago())
                    .addParameter("punto_venta", venta.getPunto_venta())
                    .addParameter("nro_comprobante", venta.getNro_comprobante())
                    .addParameter("observaciones", venta.getObservaciones())
                    .executeUpdate()
                    .getKey(Integer.class);

            // 5) Alta de ítems vinculados a la venta
            this.altaItems(con, ventaId, items);

            // 6) Commit
            con.commit();

            logger.debug("Movimiento ID: " + movimientoId + " | Venta e ítems insertados con ID: " + ventaId);
            return ventaId;

        } catch (Exception e) {
            if (con != null) {
                con.rollback();
            }
            logger.warn("Rollback ejecutado en altaConItems(): " + e.getMessage(), e);
            return -1;
        } finally {
            if (con != null) {
                con.close();
            }
            logger.debug("Conexión cerrada después de altaConItems()");
        }
    }

    /**
     * Borra todos los ítems de una venta y vuelve a insertarlos. Usa la misma
     * Connection para mantener la transacción abierta.
     */
    private void altaItems(Connection con, Integer ventaId, List<ItemVenta> items) {

        final String deleteItems
                = "DELETE FROM ItemVenta WHERE venta_id = :venta_id";

        final String insertItem
                = "INSERT INTO ItemVenta (venta_id,producto_id, producto_precio, nombre, cantidad, porcentaje_descuento, subtotal) "
                + "VALUES (:venta_id, :producto_id, :producto_precio, :nombre, :cantidad, :porcentaje_descuento, :subtotal)";

        // Borrar
        con.createQuery(deleteItems)
                .addParameter("venta_id", ventaId)
                .executeUpdate();

        // Insertar
        for (ItemVenta item : items) {
            con.createQuery(insertItem)
                    .addParameter("venta_id", ventaId)
                    .addParameter("producto_id", item.getProducto_id())
                    .addParameter("producto_precio", item.getProducto_precio())
                    .addParameter("nombre", item.getNombre())
                    .addParameter("cantidad", item.getCantidad())
                    .addParameter("porcentaje_descuento", item.getPorcentaje_descuento())
                    .addParameter("subtotal", item.getSubtotal())
                    .executeUpdate();
        }
    }

    // Buscar un remito por ID
    public Venta buscar(String id) {
        String query = "SELECT * FROM Venta WHERE id = :id AND estado = 'ACTIVO'";
        try (Connection con = ConexionSQL2o.getSql2o().open()) {
            return con.createQuery(query)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Venta.class);
        } catch (Exception e) {
            logger.error("Error en buscar(): " + e.getMessage(), e);
            return null;
        }
    }

    // Listar todos los remitos
    public List<Venta> listar() {
        String query = "SELECT * FROM Venta WHERE estado = 'ACTIVO' ORDER BY id DESC";
        try (Connection con = ConexionSQL2o.getSql2o().open()) {
            return con.createQuery(query).executeAndFetch(Venta.class);
        } catch (Exception e) {
            logger.error("Error en listar(): " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public boolean baja(Integer id) {
        String query = "UPDATE Venta SET estado = 'INACTIVO' WHERE id = :id";
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
    public boolean modificarConItems(Venta venta, List<ItemVenta> items) {
        Connection con = null;
        String insertRemito = "UPDATE Venta SET "
                + " total = :total,"
                + " observaciones = :observaciones"
                + " WHERE id = :id;";
        try {
            con = ConexionSQL2o.getSql2o().beginTransaction();

            boolean modif = con.createQuery(insertRemito)
                    .addParameter("total", venta.getTotal())
                    .addParameter("observaciones", venta.getObservaciones())
                    .addParameter("id", venta.getId())
                    .executeUpdate()
                    .getResult() > 0;

            this.altaItems(con, venta.getId(), items);

            con.commit();

            logger.debug("Remito y items insertados con ID: " + venta.getId());
            return modif;

        } catch (Sql2oException e) {
            if (con != null) {
                con.rollback();
                logger.warn("Rollback ejecutado por error en modificarConItems()." + e.getMessage(), e);
            }
        } catch (Exception e) {
            if (con != null) {
                con.rollback();
            }
            logger.error("Error en modificarConItems(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
            }
            logger.debug("Conexión cerrada después de llamar a modificarConItems()");
        }
        return false;
    }

    public boolean modificar(Venta venta) {

        String query = "UPDATE Venta SET "
                + " subtotal = :subtotal,"
                + " porcentaje_descuento = :porcentaje_descuento,"
                + " total = :total,"
                + " forma_pago = :forma_pago,"
                + " punto_venta = :punto_venta,"
                + " nro_comprobante = :nro_comprobante,"
                + " observaciones = :observaciones"
                + " WHERE id = :id;";

        try (Connection con = ConexionSQL2o.getSql2o().open()) {
            return con.createQuery(query)
                    .addParameter("id", venta.getId())
                    .addParameter("subtotal", venta.getSubtotal())
                    .addParameter("porcentaje_descuento", venta.getPorcentaje_descuento()) // podés dejar 0 si no lo usás
                    .addParameter("total", venta.getTotal()) // si no usás descuento, poné el total directo
                    .addParameter("forma_pago", venta.getForma_pago()) // 'EFECTIVO' / 'TRANSFERENCIA'
                    .addParameter("punto_venta", venta.getPunto_venta())
                    .addParameter("nro_comprobante", venta.getNro_comprobante())
                    .addParameter("observaciones", venta.getObservaciones())
                    .executeUpdate()
                    .getResult() > 0;
        } catch (Exception e) {
            logger.error("Error en modificar(): " + e.getMessage(), e);
            return false;
        }
    }

    // Listar ítems de una venta
    public List<ItemVenta> listarItems(Integer venta_id) {
        String query = "SELECT * FROM ItemVenta WHERE venta_id = :venta_id";
        try (Connection con = ConexionSQL2o.getSql2o().open()) {
            return con.createQuery(query)
                    .addParameter("venta_id", venta_id)
                    .executeAndFetch(ItemVenta.class);
        } catch (Exception e) {
            logger.error("Error en listarItems(): " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Devuelve Venta y Cliente: Presupuesto si existe.
     *
     * @param filtro Filtra por nombre de cliente o por descripción de remito
     * @param desde Fecha desde creado remito.
     * @param hasta Fecha hasta creado remito
     * @param formaPago estado_remito
     * @param pagina Numero de página
     * @param tamPag Tamaño de página
     * @return Respuesta con data + paginado.
     */
    public HashMap<String, Object> filtrarDetalladoOP(String filtro, String desde, String hasta, String formaPago, Integer pagina, Integer tamPag) {
        String select = "SELECT "
                // Venta
                + " v.id AS vid, "
                + " v.creado AS vcreado, "
                + " v.ultMod AS vultMod, "
                + " v.estado AS vestado, "
                + " v.movimiento_id AS vmovimiento_id, "
                + " v.cliente_id AS vcliente_id, "
                + " v.subtotal AS vsubtotal, "
                + " v.porcentaje_descuento AS vporc_descuento, "
                + " v.total AS vtotal, "
                + " v.forma_pago AS vforma_pago, "
                + " v.punto_venta AS vpunto_venta, "
                + " v.nro_comprobante AS vnro_comprobante, "
                + " v.observaciones AS vobservaciones, "
                // Cliente (opcional)
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
                + " c.estado AS cestado ";

        String from = " FROM Venta v "
                + " JOIN Movimiento m ON v.movimiento_id = m.id "
                + " LEFT JOIN Cliente c ON v.cliente_id=c.id " //+ " LEFT JOIN Presupuesto pre ON r.presupuesto_id = pre.pedido_id"
                ;

        String where = " WHERE v.estado = 'ACTIVO' "
                + " AND m.estado = 'ACTIVO' "
                + " AND ( c.id IS NULL OR ( c.id IS NOT NULL AND c.estado='ACTIVO' ) )";

        if (filtro != null) {
            where += " AND (c.nombre LIKE :filtro OR v.nro_comprobante LIKE :filtro OR v.observaciones LIKE :filtro)";
        }
        if (desde != null && hasta != null) {
            where += " AND (v.creado BETWEEN :desde AND :hasta)";
        }
        if (formaPago != null) {
            where += " AND v.forma_pago = :formaPago";
        }

        String orden = " ORDER BY vid DESC ";
        String page = " LIMIT :limit OFFSET :offset";

        pagina = pagina == null || pagina < 1 ? 1 : pagina; // control unsusto
        tamPag = tamPag == null || tamPag < 1 ? 10 : Math.min(tamPag, 1000); // control unsusto
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
                dq.addParameter("hasta", hasta);
                cq.addParameter("hasta", hasta);
                dq.addParameter("desde", desde);
            }
            if (formaPago != null) {
                cq.addParameter("formaPago", formaPago);
                dq.addParameter("formaPago", formaPago);
            }

            totalElementos = cq.executeAndFetchFirst(Integer.class);
            totalPaginas = (int) Math.ceil((double) totalElementos / tamPag);
            logger.debug("Count query ejecutada: " + cq.toString());
            logger.debug("Datos query por ejecutar: " + dq.toString());

            data = tablaToVentasDetalladoOP(dq
                    .addParameter("limit", tamPag)
                    .addParameter("offset", (pagina - 1) * tamPag)
                    .executeAndFetchTable());

            logger.debug("Datos query ejecutada: " + dq.toString());

            response.put("pagina", pagina);
            response.put("elementos", tamPag);
            response.put("t_elementos", totalElementos);
            response.put("t_paginas", totalPaginas);

            if (data != null) {
                response.put("data", data);
            }
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

    private List<HashMap<String, Object>> tablaToVentasDetalladoOP(Table tabla) {
        List<HashMap<String, Object>> filas = new ArrayList<>();

        try {
            for (Row row : tabla.rows()) {
                // Pedido
                Venta v = new Venta();
                v.setId(row.getInteger("vid"));
                v.setCreado(row.getString("vcreado"));
                v.setUltMod(row.getString("vultMod"));
                v.setEstado(row.getString("vestado"));
                v.setMovimiento_id(row.getInteger("vmovimiento_id"));
                v.setCliente_id(row.getInteger("vcliente_id"));
                v.setSubtotal(row.getFloat("vsubtotal"));
                v.setPorcentaje_descuento(row.getInteger("vporc_descuento"));
                v.setTotal(row.getFloat("vtotal"));
                v.setForma_pago(row.getString("vforma_pago"));
                v.setPunto_venta(row.getString("vpunto_venta"));
                v.setNro_comprobante(row.getInteger("vnro_comprobante"));
                v.setObservaciones(row.getString("vobservaciones"));
                // Cliente
                Cliente c = new Cliente();
                if (row.getObject("cid") != null) {
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
                // Armar fila
                HashMap<String, Object> fila = new HashMap<>();
                fila.put("venta", v);
                fila.put("cliente", c);

                filas.add(fila);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("SELECT query tablaToVentasDetalladoOP() - correcto");
            }

        } catch (Exception ex) {
            logger.warn("tablaToVentasDetalladoOP() Error: " + ex.getMessage(), ex);
        }

        return filas;
    }

    /**
     * @param id
     * @return
     */
    public HashMap<String, Object> buscarDetallado(String id) {
        Connection con = null;
        String query = "SELECT "
                // Venta
                + " v.id AS vid, "
                + " v.creado AS vcreado, "
                + " v.ultMod AS vultMod, "
                + " v.estado AS vestado, "
                + " v.movimiento_id AS vmovimiento_id, "
                + " v.cliente_id AS vcliente_id, "
                + " v.subtotal AS vsubtotal, "
                + " v.porcentaje_descuento AS vporcentaje_descuento, "
                + " v.total AS vtotal, "
                + " v.forma_pago AS vforma_pago, "
                + " v.punto_venta AS vpunto_venta, "
                + " v.nro_comprobante AS vnro_comprobante, "
                + " v.observaciones AS vobservaciones, "
                // Cliente (opcional)
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
                + " c.estado AS cestado, "
                // Movimiento
                //                + " m.id AS mid, "
                //                + " m.creado AS mcreado, "
                //                + " m.ultMod AS multMod, "
                //                + " m.estado AS mestado, "
                // ItemsVenta
                + " iv.id AS ivid, "
                + " iv.creado AS ivcreado, "
                + " iv.ultMod AS ivultMod, "
                + " iv.estado AS ivestado, "
                + " iv.nombre AS ivnombre, "
                + " iv.producto_id AS ivproducto_id, "
                + " iv.producto_precio AS ivproducto_precio, "
                + " iv.porcentaje_descuento AS ivporcentaje_descuento, "
                + " iv.subtotal AS ivsubtotal, "
                + " iv.cantidad AS ivcantidad "
                + " FROM Venta v "
                + " LEFT JOIN Cliente c ON v.cliente_id = c.id "
//                + " JOIN Movimiento m ON v.movimiento_id = m.id "
                + " JOIN ItemVenta iv ON v.id = iv.venta_id "
                + " WHERE v.estado = 'ACTIVO' "
//                + " AND m.estado = 'ACTIVO' "
                + " AND iv.estado = 'ACTIVO' "
                + " AND (c.id IS NULL OR c.estado = 'ACTIVO') "
                + " AND v.id = :id;";

        try {
            con = ConexionSQL2o.getSql2o().open();
            return tablaToVentaConItems(
                    con.createQuery(query).addParameter("id", id).executeAndFetchTable()
            );
        } catch (Exception e) {
            logger.error("Error inesperado en buscarDetalladoVenta(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Conexión cerrada después de llamar a buscarDetalladoVenta()");
        }
        return null;
    }

    private HashMap<String, Object> tablaToVentaConItems(Table tabla) {
        HashMap<String, Object> resultado = new HashMap<>();
        List<ItemVenta> items = new ArrayList<>();
        Venta v = null;
        Movimiento m = null;
        Cliente c = null;

        try {
            for (Row row : tabla.rows()) {
                if (v == null) {
                    v = new Venta();
                    v.setId(row.getInteger("vid"));
                    v.setCreado(row.getString("vcreado"));
                    v.setUltMod(row.getString("vultMod"));
                    v.setEstado(row.getString("vestado"));
                    v.setMovimiento_id(row.getInteger("vmovimiento_id"));
                    v.setCliente_id(row.getInteger("vcliente_id"));
                    v.setSubtotal(row.getFloat("vsubtotal"));
                    v.setPorcentaje_descuento(row.getInteger("vporcentaje_descuento"));
                    v.setTotal(row.getFloat("vtotal"));
                    v.setForma_pago(row.getString("vforma_pago"));
                    v.setPunto_venta(row.getString("vpunto_venta"));
                    v.setNro_comprobante(row.getInteger("vnro_comprobante"));
                    v.setObservaciones(row.getString("vobservaciones"));
                }

                if (c == null) {
                    c = new Cliente();
                    if (row.getObject("cid") != null) {
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
                        c.setEstado(row.getString("cestado"));
                    }
                }

//                if (m == null) {
//                    m = new Movimiento();
//                    m.setId(row.getInteger("mid"));
//                    m.setCreado(row.getString("mcreado"));
//                    m.setUltMod(row.getString("multMod"));
//                    m.setEstado(row.getString("mestado"));
//                }

                ItemVenta iv = new ItemVenta();
                iv.setId(row.getInteger("ivid"));
                iv.setCreado(row.getString("ivcreado"));
                iv.setUltMod(row.getString("ivultMod"));
                iv.setEstado(row.getString("ivestado"));
                iv.setProducto_precio(row.getFloat("ivproducto_precio"));
                iv.setNombre(row.getString("ivnombre"));
                iv.setCantidad(row.getInteger("ivcantidad"));
                iv.setSubtotal(row.getFloat("ivsubtotal"));
                iv.setPorcentaje_descuento(row.getInteger("ivporcentaje_descuento"));
                iv.setVenta_id(row.getInteger("vid"));
                items.add(iv);
            }

            resultado.put("venta", v);
//            resultado.put("movimiento", m);
            resultado.put("cliente", c);
            resultado.put("items", items);

            if (logger.isDebugEnabled()) {
                logger.debug("tablaToVentaConItems() - correcto");
            }

        } catch (Exception ex) {
            logger.warn(DAOVenta.class.getName() + " tablaToVentaConItems() Error: " + ex.getMessage(), ex);
        }
        return resultado;
    }

    public HashMap<String, Object> resumenVentas(String filtro, String desde, String hasta) {
        Connection con = null;
        HashMap<String, Object> response = new HashMap<>();
        try {
            con = ConexionSQL2o.getSql2o().open();

            // Fechas (si vienen)
            if (desde != null && hasta != null) {
                desde += " 00:00:00";
                hasta += " 23:59:59";
            }

            // WHERE base sobre Venta
            String filtroComun = " WHERE estado = 'ACTIVO'";
            if (filtro != null && !filtro.isEmpty()) {
                // Sin JOINs: uso campos propios de Venta
                filtroComun += " AND (nro_comprobante LIKE :filtro OR observaciones LIKE :filtro)";
            }
            if (desde != null && hasta != null) {
                filtroComun += " AND creado BETWEEN :desde AND :hasta";
            }

            // Totales por forma de pago y total general
            String sqlEfectivo = "SELECT SUM(total) FROM Venta" + filtroComun + " AND forma_pago = 'EFECTIVO'";
            String sqlTransferencia = "SELECT SUM(total) FROM Venta" + filtroComun + " AND forma_pago = 'TRANSFERENCIA'";
            String sqlTotalVentas = "SELECT SUM(total) FROM Venta" + filtroComun;

            // Preparar queries
            Query qEfec = con.createQuery(sqlEfectivo);
            Query qTrans = con.createQuery(sqlTransferencia);
            Query qTotal = con.createQuery(sqlTotalVentas);

            // Parámetros
            if (filtro != null && !filtro.isEmpty()) {
                qEfec.addParameter("filtro", "%" + filtro + "%");
                qTrans.addParameter("filtro", "%" + filtro + "%");
                qTotal.addParameter("filtro", "%" + filtro + "%");
            }
            if (desde != null && hasta != null) {
                qEfec.addParameter("desde", desde).addParameter("hasta", hasta);
                qTrans.addParameter("desde", desde).addParameter("hasta", hasta);
                qTotal.addParameter("desde", desde).addParameter("hasta", hasta);
            }

            // Ejecutar
            Float totalEfectivo = qEfec.executeAndFetchFirst(Float.class);
            Float totalTransferencia = qTrans.executeAndFetchFirst(Float.class);
            Float totalVentas = qTotal.executeAndFetchFirst(Float.class);

            // Null-safe
            totalEfectivo = totalEfectivo == null ? 0f : totalEfectivo;
            totalTransferencia = totalTransferencia == null ? 0f : totalTransferencia;
            totalVentas = totalVentas == null ? 0f : totalVentas;

            // Respuesta
            response.put("total_efectivo", totalEfectivo);
            response.put("total_transferencia", totalTransferencia);
            response.put("total_ventas", totalVentas);

            return response;

        } catch (Exception e) {
            logger.error("resumenVentas: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
                logger.debug("resumenVentas: Conexión cerrada");
            }
        }
        return null;
    }

    public HashMap<String, Object> resumenVentasConContador(String filtro, String desde, String hasta) {
        Connection con = null;
        HashMap<String, Object> r = new HashMap<>();
        try {
            con = ConexionSQL2o.getSql2o().open();

            if (desde != null && hasta != null) {
                desde += " 00:00:00";
                hasta += " 23:59:59";
            }

            String where = " WHERE v.estado = 'ACTIVO' ";
            if (filtro != null && !filtro.isEmpty()) {
                where += " AND (nro_comprobante LIKE :filtro OR observaciones LIKE :filtro)";
            }
            if (desde != null && hasta != null) {
                where += " AND creado BETWEEN :desde AND :hasta";
            }

            String sql
                    = "SELECT "
                    + " COALESCE(SUM(CASE WHEN forma_pago='EFECTIVO' THEN total ELSE 0 END),0)       AS total_efectivo, "
                    + " COALESCE(SUM(CASE WHEN forma_pago='TRANSFERENCIA' THEN total ELSE 0 END),0)  AS total_transferencia, "
                    + " COALESCE(SUM(total),0)                                                      AS total_ventas, "
                    + " COALESCE(SUM(CASE WHEN forma_pago='EFECTIVO' THEN 1 ELSE 0 END),0)          AS cant_efectivo, "
                    + " COALESCE(SUM(CASE WHEN forma_pago='TRANSFERENCIA' THEN 1 ELSE 0 END),0)     AS cant_transferencia "
 //                   + " COUNT(*)                                                                     AS cant_ventas "
                    + " FROM Venta v" + where;

            Query q = con.createQuery(sql);
            if (filtro != null && !filtro.isEmpty()) {
                q.addParameter("filtro", "%" + filtro + "%");
            }
            if (desde != null && hasta != null) {
                q.addParameter("desde", desde).addParameter("hasta", hasta);
            }

            var t = q.executeAndFetchTable();
            var row = t.rows().isEmpty() ? null : t.rows().get(0);

            Float totalEfec = row == null ? 0f : (row.getFloat("total_efectivo") == null ? 0f : row.getFloat("total_efectivo"));
            Float totalTrans = row == null ? 0f : (row.getFloat("total_transferencia") == null ? 0f : row.getFloat("total_transferencia"));
            Float totalVent = row == null ? 0f : (row.getFloat("total_ventas") == null ? 0f : row.getFloat("total_ventas"));

            Integer cantEfec = row == null ? 0 : (row.getInteger("cant_efectivo") == null ? 0 : row.getInteger("cant_efectivo"));
            Integer cantTrans = row == null ? 0 : (row.getInteger("cant_transferencia") == null ? 0 : row.getInteger("cant_transferencia"));
            
            r.put("total_efectivo", totalEfec);
            r.put("total_transferencia", totalTrans);
            r.put("total_ventas", totalVent);
            r.put("cant_efectivo", cantEfec);
            r.put("cant_transferencia", cantTrans);
            
            return r;

        } catch (Exception e) {
            logger.error("resumenVentasConContador: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
            }
        }
        return null;
    }
}
