
package com.OndaByte.GestionComercio.DAO;

import com.OndaByte.GestionComercio.modelo.ItemRemito;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Connection;
import org.sql2o.Query;

public class DAOItemRemito implements DAOInterface<ItemRemito> {

    private static Logger logger = LogManager.getLogger(DAOItemRemito.class.getName());

    private String clave = "id";
    private GeneradorQuery<ItemRemito> generadorQuery;

    public DAOItemRemito() {
        generadorQuery = new GeneradorQuery<ItemRemito>(ItemRemito.class);
        generadorQuery.setClave(clave);
    }


    public Class<ItemRemito> getClase() {
        return ItemRemito.class;
    }

    public Integer alta(ItemRemito t, Connection con) {
        String query;
        try {
            if(con==null)
                con = ConexionSQL2o.getSql2o().open();
            query = "SELECT EXISTS (SELECT * FROM Remito WHERE id = :remito_id)";

            Query auxQuery = con.createQuery(query).addParameter("remito_id", t.getRemito_id());
            if(auxQuery.executeAndFetchFirst(Integer.class) == 0){
                logger.debug("DAOItemRemito.alta() - No se encontro presupuesto asociado para dar de alta con id:" + t.getRemito_id());
                return -1;
            }

            query = generadorQuery.getQueryAlta(t);
            logger.debug(query);

            auxQuery = con.createQuery(query).bind(t);
            return auxQuery.executeUpdate().getKey(Integer.class);
        } catch (Exception e) {
            logger.error("Alta: " + e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public Integer alta(ItemRemito t) {
        String query;
        Connection con = null;
        try {
            con = ConexionSQL2o.getSql2o().open();
            query = "SELECT EXISTS (SELECT * FROM Remito WHERE id = :remito_id)";

            Query auxQuery = con.createQuery(query).addParameter("remito_id", t.getRemito_id());
            if(auxQuery.executeAndFetchFirst(Integer.class) == 0){
                logger.debug("DAOItemRemito.alta() - No se encontro presupuesto asociado para dar de alta con id:" + t.getRemito_id());
                return -1;
            }

            query = generadorQuery.getQueryAlta(t);
            logger.debug(query);

            auxQuery = con.createQuery(query).bind(t);
            return auxQuery.executeUpdate().getKey(Integer.class);
        } catch (Exception e) {
            logger.error("Alta: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Baja: Conexión cerrada");
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

    @Override
    public boolean modificar(ItemRemito t) {
        String query;
        Connection con = null;
        try {
            query = generadorQuery.getQueryModificar();
            con = ConexionSQL2o.getSql2o().open();

            Query auxQuery = con.createQuery(query).bind(t);
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
    public List<ItemRemito> listar() {
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
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Listar: Conexión cerrada");
        }
        return null;
    }

    @Override
    public List<ItemRemito> listar(String... ids) {
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
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Listar(string[]): Conexión cerrada");
        }
        return null;
    }

    @Override
    public List<ItemRemito> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores) {
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
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Filtrar: Conexión cerrada");
        }
        return null;
    }

    @Override
    public ItemRemito buscar(String id) {
        String query;
        Connection con = null;
        try{
            query = generadorQuery.getQueryFiltarId();
            con = ConexionSQL2o.getSql2o().open();
            return (ItemRemito) con.createQuery(query).addParameter("id", id).executeAndFetchFirst(this.getClase());
        } catch (Exception e) {
            logger.error("Buscar: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                con.close(); // Aunque Sql2o la cierra, aseguramos cierre
            }
            logger.debug("Buscar: Conexión cerrada");
        }
        return null;
    }
}