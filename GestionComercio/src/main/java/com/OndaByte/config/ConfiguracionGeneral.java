package com.OndaByte.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguracionGeneral {

  private final static Properties properties = new Properties();
  private final static String CONFIG_FILE_NAME = "config/generalconfig.properties";

  private final static String CONST_CONFIG_MYSQL_DRIVER = "MYSQL_DRIVER";
  private final static String CONST_CONFIG_MYSQL_URL = "MYSQL_URL";
  private final static String CONST_CONFIG_MYSQL_PORT = "MYSQL_PORT";
  private final static String CONST_CONFIG_MYSQL_NAME = "MYSQL_NAME";
  private final static String CONST_CONFIG_MYSQL_USER = "MYSQL_USER";
  private final static String CONST_CONFIG_MYSQL_PASSWORD = "MYSQL_PASSWORD";
  private final static String CONST_CONFIG_HTTP_API_PORT = "HTTP_API_PORT"; 
  private final static String CONST_SSL = "SSL";

  
    
  private static String CONFIG_MYSQL_DRIVER = "jdbc:mysql";
  private static String CONFIG_MYSQL_URL = "localhost";
  private static String CONFIG_MYSQL_PORT = "3306";
  private static String CONFIG_MYSQL_NAME = "GestionComercio";
  private static String CONFIG_MYSQL_USER = "root";
  private static String CONFIG_MYSQL_PASSWORD = "root";
    
  private static String CONFIG_HTTP_API_PORT = "4567";
  private static String CONFIG_SSL = "0";

  private static boolean inicializado = false;
	
  private static Logger logger = LogManager.getLogger(ConfiguracionGeneral.class.getName());

  public static void setInicializado(boolean inicializado) {
		ConfiguracionGeneral.inicializado = inicializado;
	}
    
  public static void init() {

    if(inicializado){return;}
            
    if(logger.isInfoEnabled()){
      logger.info("ConfiguracionGeneral config file: " + CONFIG_FILE_NAME);
    }
    try {
      logger.debug("ConfiguracionGeneral config file: " + CONFIG_FILE_NAME);
      properties.load(ConfiguracionGeneral.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME));
                
      if (!properties.getProperty(ConfiguracionGeneral.CONST_CONFIG_MYSQL_DRIVER).equals(""))
        ConfiguracionGeneral.CONFIG_MYSQL_DRIVER = properties.getProperty(ConfiguracionGeneral.CONST_CONFIG_MYSQL_DRIVER);
                
      if (!properties.getProperty(ConfiguracionGeneral.CONST_CONFIG_MYSQL_URL).equals(""))
        ConfiguracionGeneral.CONFIG_MYSQL_URL = properties.getProperty(ConfiguracionGeneral.CONST_CONFIG_MYSQL_URL);
                
      if (!properties.getProperty(ConfiguracionGeneral.CONST_CONFIG_MYSQL_PORT).equals(""))
        ConfiguracionGeneral.CONFIG_MYSQL_PORT = properties.getProperty(ConfiguracionGeneral.CONST_CONFIG_MYSQL_PORT);
                
      if (!properties.getProperty(ConfiguracionGeneral.CONST_CONFIG_MYSQL_NAME).equals(""))
        ConfiguracionGeneral.CONFIG_MYSQL_NAME = properties.getProperty(ConfiguracionGeneral.CONST_CONFIG_MYSQL_NAME);
                
      if (!properties.getProperty(ConfiguracionGeneral.CONST_CONFIG_MYSQL_USER).equals(""))
        ConfiguracionGeneral.CONFIG_MYSQL_USER = properties.getProperty(ConfiguracionGeneral.CONST_CONFIG_MYSQL_USER);
                
      if (!properties.getProperty(ConfiguracionGeneral.CONST_CONFIG_MYSQL_PASSWORD).equals(""))
        ConfiguracionGeneral.CONFIG_MYSQL_PASSWORD = properties.getProperty(ConfiguracionGeneral.CONST_CONFIG_MYSQL_PASSWORD);
                
      if (!properties.getProperty(ConfiguracionGeneral.CONST_CONFIG_HTTP_API_PORT).equals(""))
        ConfiguracionGeneral.CONFIG_HTTP_API_PORT = properties.getProperty(ConfiguracionGeneral.CONST_CONFIG_HTTP_API_PORT);
      
      if (!properties.getProperty(ConfiguracionGeneral.CONST_SSL).equals(""))
        ConfiguracionGeneral.CONFIG_SSL = properties.getProperty(ConfiguracionGeneral.CONST_SSL);
                
      /*
        if (!properties.getProperty(ConfiguracionGeneral.CONST_MSD_GENERIC_COUNT).equals("")){
        ConfiguracionGeneral.MSD_GENERIC_COUNT = Integer.parseInt(properties.getProperty(ConfiguracionGeneral.CONST_MSD_GENERIC_COUNT));
        if (ConfiguracionGeneral.MSD_GENERIC_COUNT > ConfiguracionGeneral.MSD_GLOBAL_MAX_COUNT)
        ConfiguracionGeneral.MSD_GENERIC_COUNT = ConfiguracionGeneral.MSD_GLOBAL_MAX_COUNT;
        }else{
        ConfiguracionGeneral.MSD_GENERIC_COUNT = ConfiguracionGeneral.MSD_GLOBAL_MAX_COUNT;
        }
      */
                
    }
    catch (FileNotFoundException ex) {
      logger.fatal(ConfiguracionGeneral.class.getName() + " Error al inicializar configuraciones generales " + ex.getMessage());
    } catch (IOException ex) {
      logger.fatal(ConfiguracionGeneral.class.getName() + " Error al inicializar configuraciones generales " + ex.getMessage());
      System.exit(0);
    } catch (NumberFormatException ex) {
      logger.fatal(ConfiguracionGeneral.class.getName() + " Error al inicializar configuraciones generales " + ex.getMessage());
      System.exit(0);
    } catch (Exception ex) {
      logger.fatal(ConfiguracionGeneral.class.getName() + " Error al inicializar configuraciones generales " + ex.getMessage());
      System.exit(0);
    }
  }

  public static String getCONFIG_MYSQL_DRIVER() {
    return CONFIG_MYSQL_DRIVER;
  }

  public static void setCONFIG_MYSQL_DRIVER(String CONFIG_MYSQL_DRIVER) {
    ConfiguracionGeneral.CONFIG_MYSQL_DRIVER = CONFIG_MYSQL_DRIVER;
  }

  public static String getCONFIG_MYSQL_URL() {
    return CONFIG_MYSQL_URL;
  }

  public static void setCONFIG_MYSQL_URL(String CONFIG_MYSQL_URL) {
    ConfiguracionGeneral.CONFIG_MYSQL_URL = CONFIG_MYSQL_URL;
  }

  public static String getCONFIG_MYSQL_PORT() {
    return CONFIG_MYSQL_PORT;
  }

  public static void setCONFIG_MYSQL_PORT(String CONFIG_MYSQL_PORT) {
    ConfiguracionGeneral.CONFIG_MYSQL_PORT = CONFIG_MYSQL_PORT;
  }

  public static String getCONFIG_MYSQL_NAME() {
    return CONFIG_MYSQL_NAME;
  }

  public static void setCONFIG_MYSQL_NAME(String CONFIG_MYSQL_NAME) {
    ConfiguracionGeneral.CONFIG_MYSQL_NAME = CONFIG_MYSQL_NAME;
  }

  public static String getCONFIG_MYSQL_USER() {
    return CONFIG_MYSQL_USER;
  }

  public static void setCONFIG_MYSQL_USER(String CONFIG_MYSQL_USER) {
    ConfiguracionGeneral.CONFIG_MYSQL_USER = CONFIG_MYSQL_USER;
  }

  public static String getCONFIG_MYSQL_PASSWORD() {
    return CONFIG_MYSQL_PASSWORD;
  }

  public static void setCONFIG_MYSQL_PASSWORD(String CONFIG_MYSQL_PASSWORD) {
    ConfiguracionGeneral.CONFIG_MYSQL_PASSWORD = CONFIG_MYSQL_PASSWORD;
  }

  public static String getCONFIG_HTTP_API_PORT() {
    return CONFIG_HTTP_API_PORT;
  }

  public static void setCONFIG_HTTP_API_PORT(String CONFIG_HTTP_API_PORT) {
    ConfiguracionGeneral.CONFIG_HTTP_API_PORT = CONFIG_HTTP_API_PORT;
  }
  
  public static String getCONFIG_SSL() {
    return CONFIG_SSL;
  }
  public static void setCONFIG_SSL(String cONFIG_SSL) {
    CONFIG_SSL = cONFIG_SSL;
  }
    
}
