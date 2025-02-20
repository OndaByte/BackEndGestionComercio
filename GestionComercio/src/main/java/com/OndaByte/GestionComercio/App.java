package com.OndaByte.GestionComercio;
import com.OndaByte.GestionComercio.control.CajaControl;
import com.OndaByte.GestionComercio.control.ProductoControl;
import com.OndaByte.GestionComercio.control.UsuarioControlador;
import com.OndaByte.GestionComercio.filtros.FiltroAutenticador;
import com.OndaByte.config.Constantes;

import io.javalin.Javalin;
import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
 
/**
 * App
 * Autor: Fran
 */
public class App {

    private static Logger logger = LogManager.getLogger(App.class.getName());

    public static void main(String[] args) {
        
        String strPath = App.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        String log4jConfigPath = strPath.replace(Constantes.JAR_FILE, "classes"+File.separator) + Constantes.CONFIG_LOG_FILE;
        Configurator.initialize(null, log4jConfigPath );        
        
        // FIN config Log4j del archivo XML
        logger.debug("Init Server");  
       
        Javalin app = Javalin.create(config -> {
                config.bundledPlugins.enableCors(cors -> {
                        cors.addRule(x -> {x.anyHost();});
                    });
            }).start(4567);

        // Rutas protegidas
        app.before("/protegido/*", FiltroAutenticador::filtro);
        app.post("/protegido/actualizar", UsuarioControlador::cambiarcontra);
        app.get("/protegido/usuarios", UsuarioControlador::usuarios);
        app.post("/protegido/eliminar", UsuarioControlador::baja);

        // Rutas públicas de usuario
        app.post("/registrar", UsuarioControlador::registrar);
        app.get("/login", UsuarioControlador::loginForm);
        app.post("/login", UsuarioControlador::login);

        // Rutas para productos
        app.get("/Productos", ProductoControl::listar);
        app.post("/Productos/Alta", ProductoControl::alta);
        app.patch("/Productos/{id}/SumarStock", ProductoControl::sumarStock); // Cambiado a PATCH por ser actualización parcial
        app.delete("/Productos/{id}", ProductoControl::baja);
        app.put("/Productos/{id}/Modificar", ProductoControl::modificar); // Cambiado a PUT por ser una actualización completa

		// Rutas Caja
		app.post("/AbrirCaja", CajaControl::abrirCaja);
		app.get("/Caja", CajaControl::getCaja);
		app.patch("/CerrarCaja", CajaControl::cerrarCaja);
		app.get("/Cajas", CajaControl::getCajas);
		app.get("/Caja/{id}", CajaControl::getMovimientos);
		app.get("/Transacciones", CajaControl::getMovimientosFechas);

    }
}
