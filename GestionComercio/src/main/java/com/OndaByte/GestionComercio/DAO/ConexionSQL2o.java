package com.OndaByte.GestionComercio.DAO;

import com.OndaByte.config.ConfiguracionGeneral;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Connection;

import org.sql2o.Sql2o;

public class ConexionSQL2o {
    protected static Sql2o sql2o;
    private static HikariDataSource dataSource;
    private static final Logger logger = LogManager.getLogger(ConexionSQL2o.class);

    public static Sql2o getSql2o() {
        if (sql2o == null) {
            logger.info("Inicializando HikariCP + Sql2o");
            logger.info("Configuración.propierties leída :" + ConfiguracionGeneral.getCONFIG_MYSQL_DRIVER() + "://" +
                    ConfiguracionGeneral.getCONFIG_MYSQL_URL() + ":" +
                    ConfiguracionGeneral.getCONFIG_MYSQL_PORT() + "/" +
                    ConfiguracionGeneral.getCONFIG_MYSQL_NAME());
            //TODO : cambiar mysql por MariaDB en nombres
            HikariConfig config = new HikariConfig(); 
            config.setJdbcUrl(ConfiguracionGeneral.getCONFIG_MYSQL_DRIVER() + "://" +
                    ConfiguracionGeneral.getCONFIG_MYSQL_URL() + ":" +
                    ConfiguracionGeneral.getCONFIG_MYSQL_PORT() + "/" +
                    ConfiguracionGeneral.getCONFIG_MYSQL_NAME());
            config.setUsername(ConfiguracionGeneral.getCONFIG_MYSQL_USER());
            config.setPassword(ConfiguracionGeneral.getCONFIG_MYSQL_PASSWORD());

            // Dibuje al gusto .. 
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2); // idle significa conexion abierta en espera 
            config.setIdleTimeout(60000); // tiempo maximo de espera
            config.setConnectionTimeout(30000); // coneccion timeout
            config.setLeakDetectionThreshold(15000); // ni idea la primera vezque lo veo

            dataSource = new HikariDataSource(config);
            sql2o = new Sql2o(dataSource); // El datasource del sql20 default es reemplazado por el nuestro..
        }

        return sql2o;
    }
    //UTILS: 
    public static Connection beginTransaction() {
        logger.debug("Iniciando transacción");
        return getSql2o().beginTransaction();
    }

    public static void commit(Connection con) {
        if (con != null) {
            logger.debug("Commit");
            con.commit();
        }
    }

    public static void rollback(Connection con) {
        if (con != null) {
            logger.debug("Rollback");
            con.rollback();
        }
    }

    public static void close(Connection con) {
        if (con != null) {
            logger.debug("Cerrando conexión");
            con.close();
        }
    }
}
