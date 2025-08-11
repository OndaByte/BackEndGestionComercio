package com.OndaByte.GestionComercio.DAO;

import com.OndaByte.GestionComercio.modelo.Producto;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;

import org.sql2o.Connection;
import org.sql2o.Sql2oException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Query;
import org.sql2o.data.Row;
import org.sql2o.data.Table;

public class DAOProducto implements DAOInterface<Producto> {

    private static Logger logger = LogManager.getLogger(DAOProducto.class.getName());

    private String clave = "id";
    private GeneradorQuery<Producto> generadorQuery;

    public DAOProducto() {
        generadorQuery = new GeneradorQuery<Producto>(Producto.class);
        generadorQuery.setClave(clave);
    }

    public Class<Producto> getClase() {
        return Producto.class;
    }

    @Override
    public Integer alta(Producto p) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryAlta(p);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).bind(p).executeUpdate().getKey(Integer.class);
            // return ((BigInteger) con.createQuery(query).bind(t).executeUpdate().getKey()).longValue();
        } catch (Sql2oException e) {
            logger.error("Error SQL alta(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error alta(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a alta()");
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
            logger.error("Error SQL baja() " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en baja(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a DAOInsumo.baja()");
        }
        return false;
    }

    @Override
    public boolean modificar(Producto p) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryModificar();
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).bind(p).executeUpdate().getResult() > 0;
        } catch (Sql2oException e) {
            logger.error("Error SQL en modificar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en modificar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a modificar()");
        }
        return false;
    }

    @Override
    public List<Producto> listar() {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryListarOrder("nombre", "ASC");
            logger.debug(query);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        } catch (Sql2oException e) {
            logger.error("Error SQL en listar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a listar()");
        }
        return null;
    }

    @Override
    public List<Producto> listar(String... ids) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryListar(ids);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        } catch (Sql2oException e) {
            logger.error("Error SQL en listar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en listar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a listar()");
        }
        return null;
    }

    @Override
    public List<Producto> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryFiltrarOrdenadoYPaginado(campos, valores, condiciones, conectores, null);
            con = ConexionSQL2o.getSql2o().open();
            return con.createQuery(query).executeAndFetch(this.getClase());
        } catch (Sql2oException e) {
            logger.error("Error SQL en filtrar(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en filtrar(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
            }
            logger.debug("Conexión cerrada después de llamar a filtrar()");
        }
        return null;
    }

    public HashMap<String, Object> filtrarOrdenadoYPaginado(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores, Integer pagina, Integer elementos) {
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

            String queryDatos = generadorQuery.getQueryFiltrarOrdenadoYPaginado(campos, valores, condiciones, conectores, "ASC"); // por ahora fijo

            if (logger.isDebugEnabled()) {
                logger.debug("SELECT query filtrarOrdenadoYPaginado(): " + queryDatos);
            }

            List<Producto> result = con.createQuery(queryDatos)
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
    public Producto buscar(String id) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryFiltarId();
            con = ConexionSQL2o.getSql2o().open();
            return (Producto) con.createQuery(query).addParameter("id", id).executeAndFetchFirst(this.getClase());
        } catch (Sql2oException e) {
            logger.error("Error SQL en buscar(id): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado en buscar(id): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Conexión cerrada después de llamar a DAOInsumo.buscar(id)");
        }
        return null;
    }

    /**
     * Lista productos con su categoría. Filtra por texto (producto o categoría)
     * y/o por categoriaId.
     *
     * @param filtro Texto a buscar (en p.nombre/p.descripcion/p.codigo_barra o
     * c.nombre). Opcional.
     * @param categoriaId ID de la categoría a filtrar. Opcional.
     * @param pagina Número de página (1-based)
     * @param tamPag Tamaño de página
     * @return HashMap con data y metadatos de paginado
     */
    public HashMap<String, Object> filtrarDetalladoOP_ProductoCategoria(
            String filtro,
            Integer categoriaId,
            Integer pagina,
            Integer tamPag
    ) {
        String select = " SELECT "
                + " p.id AS pid, "
                + " p.creado AS pcreado, "
                + " p.ultMod AS pultMod, "
                + " p.estado AS pestado, "
                + " p.categoria_id AS pcategoria_id, "
                + " p.nombre AS pnombre, "
                + " p.codigo_barra AS pcodigo_barra, "
                + " p.descripcion AS pdescripcion, "
                + " p.precio_costo AS pprecio_costo, "
                + " p.porcentaje_ganancia AS pporc_ganancia, "
                + " p.porcentaje_descuento AS pporc_descuento, "
                + " p.stock AS pstock, "
                + " c.id AS cid, "
                + " c.nombre AS cnombre, "
                + " c.porcentaje_descuento AS cporc_descuento, "
                + " c.tipo AS ctipo, "
                + " c.estado AS cestado "
                + " FROM Producto p "
                + " LEFT JOIN Categoria c ON p.categoria_id = c.id AND c.tipo = 'PRODUCTO' ";

        String where = " WHERE p.estado = 'ACTIVO' "
                + " AND (c.id IS NULL OR c.estado = 'ACTIVO') ";

        if (filtro != null && !filtro.isEmpty()) {
            // Busca en producto y en categoría
            where += " AND (p.nombre LIKE :filtro "
                    + " OR p.descripcion LIKE :filtro "
                    + " OR p.codigo_barra LIKE :filtro "
                    + " OR c.nombre LIKE :filtro) ";
        }
        if (categoriaId != null) {
            where += " AND p.categoria_id = :categoriaId ";
        }

        String orden = " ORDER BY pid DESC ";
        String page = " LIMIT :limit OFFSET :offset ";

        pagina = (pagina == null || pagina < 1) ? 1 : pagina;
        tamPag = (tamPag == null || tamPag < 1) ? 10 : Math.min(tamPag, 1000);

        Connection con = null;
        try {
            con = ConexionSQL2o.getSql2o().open();

            Query cq = con.createQuery("SELECT COUNT(*) FROM Producto p "
                    + " LEFT JOIN Categoria c ON p.categoria_id = c.id AND c.tipo = 'PRODUCTO' "
                    + where);

            Query dq = con.createQuery(select + where + orden + page);

            if (filtro != null && !filtro.isEmpty()) {
                String like = "%" + filtro + "%";
                cq.addParameter("filtro", like);
                dq.addParameter("filtro", like);
            }
            if (categoriaId != null) {
                cq.addParameter("categoriaId", categoriaId);
                dq.addParameter("categoriaId", categoriaId);
            }

            Integer totalElementos = cq.executeAndFetchFirst(Integer.class);
            Integer totalPaginas = (int) Math.ceil((double) totalElementos / tamPag);

            dq.addParameter("limit", tamPag)
                    .addParameter("offset", (pagina - 1) * tamPag);

            List<HashMap<String, Object>> data = tablaToProductosConCategoria(
                    dq.executeAndFetchTable()
            );

            HashMap<String, Object> response = new HashMap<>();
            response.put("pagina", pagina);
            response.put("elementos", tamPag);
            response.put("t_elementos", totalElementos);
            response.put("t_paginas", totalPaginas);
            if (data != null) {
                response.put("data", data);
            }
            return response;

        } catch (Sql2oException e) {
            logger.error("Error SQL en filtrarDetalladoOP_ProductoCategoria(): " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error en filtrarDetalladoOP_ProductoCategoria(): " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close();
            }
            logger.debug("Conexión cerrada después de llamar a filtrarDetalladoOP_ProductoCategoria()");
        }
        return null;
    }

    private List<HashMap<String, Object>> tablaToProductosConCategoria(Table tabla) {
        List<HashMap<String, Object>> data = new ArrayList<>();
        try {
            for (Row row : tabla.rows()) {
                HashMap<String, Object> item = new HashMap<>();

                HashMap<String, Object> p = new HashMap<>();
                p.put("id", row.getInteger("pid"));
                p.put("creado", row.getString("pcreado"));
                p.put("ultMod", row.getString("pultMod"));
                p.put("estado", row.getString("pestado"));
                p.put("categoria_id", row.getInteger("pcategoria_id"));
                p.put("nombre", row.getString("pnombre"));
                p.put("codigo_barra", row.getString("pcodigo_barra"));
                p.put("descripcion", row.getString("pdescripcion"));
                p.put("precio_costo", row.getBigDecimal("pprecio_costo"));
                p.put("porcentaje_ganancia", row.getInteger("pporc_ganancia"));
                p.put("porcentaje_descuento", row.getInteger("pporc_descuento"));
                p.put("stock", row.getInteger("pstock"));
                item.put("producto", p);

                if (row.getObject("cid") != null) {
                    HashMap<String, Object> c = new HashMap<>();
                    c.put("id", row.getInteger("cid"));
                    c.put("nombre", row.getString("cnombre"));
                    c.put("porcentaje_descuento", row.getInteger("cporc_descuento"));
                    c.put("tipo", row.getString("ctipo"));
                    c.put("estado", row.getString("cestado"));
                    item.put("categoria", c);
                }

                data.add(item);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("tablaToProductosConCategoria() - correcto");
            }
        } catch (Exception ex) {
            logger.warn(getClass().getName() + " tablaToProductosConCategoria() Error: " + ex.getMessage(), ex);
        }
        return data;
    }
}
