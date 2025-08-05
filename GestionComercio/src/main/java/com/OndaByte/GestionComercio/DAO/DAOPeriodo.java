package com.OndaByte.GestionComercio.DAO;
import com.OndaByte.GestionComercio.modelo.GastoFijo;
import com.OndaByte.GestionComercio.modelo.Periodo;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

import org.sql2o.Connection;
import org.sql2o.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.data.Row;
import org.sql2o.data.Table;

public class DAOPeriodo implements DAOInterface<Periodo> {
  private String clave = "id";
  GeneradorQuery<Periodo> generadorQuery;
    
  private static Logger logger = LogManager.getLogger(DAOPeriodo.class.getName());

  public DAOPeriodo() {
    generadorQuery = new GeneradorQuery<Periodo>(Periodo.class);
    generadorQuery.setClave(clave);
  }

  public Class<Periodo> getClase() {
    return Periodo.class;
  }

  @Override
  public List<Periodo> listar() {
    String query;
    Connection con = null;
    try {
      query = generadorQuery.getQueryListar();
      logger.debug(query);
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
  public List<Periodo> listar(String... ids) {
    String query;
    Connection con = null;
    try {
      query = generadorQuery.getQueryListar(ids);
      con = ConexionSQL2o.getSql2o().open();
      return con.createQuery(query).executeAndFetch(this.getClase());
    } catch (Exception e) {
      logger.error("Listar :" + e.getMessage(), e);
    } finally {
      if (con != null) {
        con.close();
      }
      logger.debug("Listar: Conexión cerrada");
    }
    return null;
  }

  @Override
  public List<Periodo> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores) {
    String query;
    Connection con = null;
    try {
      query = generadorQuery.getQueryFiltrarOrdenadoYPaginado(campos, valores, condiciones, conectores,null);
      con = ConexionSQL2o.getSql2o().open();
      return con.createQuery(query).executeAndFetch(this.getClase());
    } catch (Exception e) {
      logger.error("Filtrar: " + e.getMessage(), e);
    } finally {
      if (con != null) {
        con.close();
      }
      logger.debug("Filtrar: Conexión cerrada");
    }
    return null;
  }
    
  @Override
  public Integer alta(Periodo t) {/*
                                    GeneradorQuery<Periodo> generadorQuery = new GeneradorQuery<>(Periodo.class);
                                    String query;
                                    Connection con = null;
                                    try {
                                    query = "SELECT EXISTS (SELECT * FROM GastoFijo WHERE id = :gasto_id)";
                                    con = ConexionSQL2o.getSql2o().open();
            
                                    Query auxQuery = con.createQuery(query).addParameter("gasto_id", t.getGasto_id());
            
                                    if (auxQuery.executeAndFetchFirst(Integer.class) == 0) {
                                    return -1;
                                    }
            
                                    query = generadorQuery.getQueryAlta(t);
                                    return con.createQuery(query).bind(t).executeUpdate().getKey(Integer.class);
                                    } catch (Exception e) {
                                    logger.error("Alta: " + e.getMessage(), e);
                                    } finally {
                                    if (con != null) {
                                    con.close();
                                    }
                                    logger.debug("Alta: Conexión cerrada");
                                    }*/
    return -1;
  }

  @Override
  public Periodo buscar(String id) {
    String query;
    Connection con = null;
    logger.debug("Buscar: "+id);
    try{
      query = generadorQuery.getQueryFiltarId();
      con = ConexionSQL2o.getSql2o().open();
      return (Periodo) con.createQuery(query).addParameter("id", id).executeAndFetchFirst(this.getClase());
    } catch (Exception e) {
      logger.error("Buscar: " + e.getMessage(), e);
    } finally {
      if (con != null) {
        con.close();
        logger.debug("Buscar: Conexion cerrada");
      }
    }
    return null;
  }
  /*
    public Integer alta(Periodo p, List<Gasto> gastos){
    Connection con = null;
    GeneradorQuery<Gasto> gquery = new GeneradorQuery<>(Gasto.class);
        
    try{ 
    con = ConexionSQL2o.getSql2o().beginTransaction();

    Query queryPeriodo = con.createQuery(generadorQuery.getQueryAlta(p));
    gastos.get(0).setPeriodo_id(0);
    Query queryBatch = con.createQuery(gquery.getQueryAlta(gastos.get(0)));
            
    Integer res = queryPeriodo.bind(p).executeUpdate().getKey(Integer.class);
    for (Gasto g : gastos){
    g.setPeriodo_id(res);
    queryBatch.bind(g).addToBatch();
    }
    queryBatch.executeBatch();
    con.createQuery("CALL sum_periodo(:id)").addParameter("id", res).executeUpdate();
            
    con.commit();
    return res;                       
    }        
    catch (Exception e) {
    logger.error("Baja: " + e.getMessage(), e);
    } finally {
    if (con != null) {
    con.close();
    logger.debug("Baja: Conexion cerrada");
    }
    }
    return -1;
    }*/
    
  @Override
  public boolean baja(String id, boolean borrar) {
    String query;
    Connection con = null;
    try {
      query = generadorQuery.getQueryBaja(borrar);
      con = ConexionSQL2o.getSql2o().open();
      return con.createQuery(query).addParameter(this.clave, id).executeUpdate().getResult() > 0;
      /*     if(res && !borrar){
             query = "UPDATE Gasto SET estado='INACTIVO' WHERE periodo_id = :id";
             con.createQuery(query).addParameter("id", id).executeUpdate();
             }*/
    } catch (Exception e) {
      logger.error("Baja: " + e.getMessage(), e);
    } finally {
      if (con != null) {
        con.close();
        logger.debug("Baja: Conexion cerrada");
      }
    }
    return false;
  }

  @Override
  public boolean modificar(Periodo t) {
    String query;
    Connection con = null;
    logger.debug("Modificar: "+t.toString());
    try {
      query = generadorQuery.getQueryModificar();
      con = ConexionSQL2o.getSql2o().open();
      return con.createQuery(query).bind(t).executeUpdate().getResult() > 0;            
      //  con.createQuery("CALL sum_periodo(:id)").addParameter("id", t.getId()).executeUpdate();
            
    }  catch (Exception e) {
      logger.error("Modificar: " + e.getMessage(), e);
    } finally {
      if (con != null) {
        con.close();
        logger.debug("Modificar: Conexion cerrada");
      }
    }
    return false;
	}

  public HashMap<String, Object> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores, Integer pagina, Integer elementos) {
    campos.add("estado");
    valores.add("ACTIVO");
    condiciones.add(0);
    conectores.add(true);
    Connection con = null;
    Integer totalElementos = null;
    Integer totalPaginas = null;
    logger.debug("Filtrar");
    try {
      HashMap<String, Object> response = new HashMap<>();
      con = ConexionSQL2o.getSql2o().open();
      String count = generadorQuery.getCountQuery(campos, valores, condiciones, conectores);

      pagina = pagina == null || pagina < 1 ? 1 : pagina;
      elementos = elementos == null || elementos < 1 ? 10 : Math.min(elementos, 1000);

      totalElementos = con.createQuery(count).executeAndFetchFirst(Integer.class); // Query extra
      totalPaginas = (int) Math.ceil((double) totalElementos / elementos);

      String queryDatos = generadorQuery.getQueryFiltrarOrdenadoYPaginado(campos, valores, condiciones, conectores, "DESC");
      if (logger.isDebugEnabled()) {
        logger.debug("Filtrar: " + queryDatos);
      }

      List<Periodo> result = con.createQuery(queryDatos)
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
    } catch (Exception e) {
      logger.error("Filtrar: " + e.getMessage(), e);
    } finally {
      if (con != null) {
        con.close();
        logger.debug("Filtrar: Conexion cerrada");
      }
    }
    return null;
  }
    
  public HashMap<String, Object> filtrarDetalladoOP(String filtro,String desde, String hasta, Integer pagina, Integer tamPag) {
    String select = " SELECT p.id AS pid, "
      + " p.costo costo, "
      + " p.gasto_id gasto_id, "
      + " p.periodo periodo, "
      + " gf.id gfid,"
      + " gf.nombre nombre,"
      + " gf.repeticion repeticion,"
      + " gf.estado estado"
      ;

    String from = " FROM Periodo p LEFT JOIN GastoFijo gf ON p.gasto_id=gf.id ";
        
    String where = " WHERE p.estado = 'ACTIVO'";
    if(filtro!=null){
      where += " AND (gf.nombre LIKE :filtro)";
    }
    if(desde != null && hasta !=null){
      where += " AND (periodo BETWEEN :desde AND :hasta)";
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
      Float costo_total = null;
      if(desde == null || hasta == null){costo_total = con.createQuery("SELECT SUM(p.costo) FROM Periodo p")
          .executeAndFetchFirst(Float.class);
      }
      else{
        costo_total = con.createQuery("SELECT SUM(p.costo) FROM Periodo p WHERE p.periodo >= :min AND p.periodo <= :max")
          .addParameter("max", hasta)
          .addParameter("min", desde)
          .executeAndFetchFirst(Float.class);
      }

      costo_total = (costo_total == null) ?  0 : costo_total;
            
      totalElementos = cq.executeAndFetchFirst(Integer.class); 
      totalPaginas = (int) Math.ceil((double) totalElementos / tamPag);
      logger.debug("Count query ejecutada: " + cq.toString());

      data = tablaPeriodoGasto(dq
                               .addParameter("limit", tamPag)
                               .addParameter("offset", (pagina - 1) * tamPag)
                               .executeAndFetchTable());
            
      logger.debug("Datos query ejecutada: " + dq.toString());

      response.put("pagina", pagina);
      response.put("elementos", tamPag);
      response.put("t_elementos", totalElementos);
      response.put("t_paginas", totalPaginas);
      response.put("costo_total", costo_total);
            

      if (data != null) response.put("data", data);
      return response;

    } catch (Exception e) {
      logger.error("FiltrarDetalladoOP: " + e.getMessage(), e);
    } finally {
      if (con != null) {
        con.close(); // Aunque Sql2o la cierra, aseguramos cierre 
      }
      logger.debug("FiltrarDetalladoOP: Conexión cerrada");
    }
    return null;
  }
    
  private List<HashMap<String, Object>> tablaPeriodoGasto(Table tpc) {
    List<HashMap<String, Object>> filas = new ArrayList<>();
    try {
      for (Row r : tpc.rows()) {
        Periodo p = new Periodo();
        p.setId(r.getInteger("pid"));
        p.setCosto(r.getFloat("costo"));
        p.setGasto_id(r.getInteger("gasto_id"));
        p.setPeriodo(r.getString("periodo"));

        GastoFijo gf = new GastoFijo();
        gf.setId(r.getInteger("gfid"));
        gf.setNombre(r.getString("nombre"));
        gf.setRepeticion(r.getInteger("repeticion"));
        gf.setEstado(r.getString("estado"));
                

        HashMap<String, Object> fila = new HashMap<>();
        fila.put("periodo", p);
        fila.put("gasto", gf);

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
    
    
  /*
    public Periodo ultimo(){
    String query;
    Connection con = null;
    logger.debug("Ultimo");
    try{
    query = "SELECT * FROM Periodo ORDER BY id DESC LIMIT 1";
    con = ConexionSQL2o.getSql2o().open();
    return con.createQuery(query).executeAndFetchFirst(Periodo.class);
    } catch (Exception e){
    logger.error("Ultimo: "+e.getMessage(), e);
    } finally {
    if(con != null){
    con.close();
    logger.debug("Ultimo: Conexion cerrada");
    }
    }
    return null;
    }
  */
}
