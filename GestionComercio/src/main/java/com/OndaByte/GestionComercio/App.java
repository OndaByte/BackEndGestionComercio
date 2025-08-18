package com.OndaByte.GestionComercio;

import com.OndaByte.GestionComercio.DAO.DAOGasto;
import com.OndaByte.GestionComercio.control.*;
import com.OndaByte.config.ConfiguracionGeneral;
import com.OndaByte.config.Constantes;

import io.javalin.Javalin;
import io.javalin.community.ssl.SslPlugin;
import static io.javalin.apibuilder.ApiBuilder.*;

import java.io.File;
import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
 
public class App {
    private static Logger logger = LogManager.getLogger(App.class.getName());
    private static Javalin app;

    public static void main(String[] args) {
        ConfiguracionGeneral.init();
        String strPath = App.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        String log4jConfigPath = strPath.replace(Constantes.JAR_FILE, "classes"+File.separator) + Constantes.CONFIG_LOG_FILE;
        Configurator.initialize(null, log4jConfigPath);        
        
        // FIN config Log4j del archivo XML
        logger.debug("Init Server");  
        int puerto = Integer.parseInt(ConfiguracionGeneral.getCONFIG_HTTP_API_PORT());

        int ssl = Integer.parseInt(ConfiguracionGeneral.getCONFIG_SSL());
    
        app = Javalin.create(config -> {
            if(ssl !=0) {
                config.registerPlugin(new SslPlugin(conf -> {
                    conf.pemFromPath("/etc/ssl/domain.cert.pem", "/etc/ssl/private.key.pem");
                    conf.insecure = false;
                    conf.securePort = puerto;
                }));
            }
        
            config.bundledPlugins.enableCors(cors -> {cors.addRule(x -> {x.anyHost();});});
            config.router.apiBuilder(()->{
                path("/p",()->{
                    before(FiltroAutenticador::filtroLogin);
                
                    put("/actualizar", UsuarioControlador::cambiarContra);

                    path("/e",()->{
                        before(FiltroAutenticador::filtroEmpleado);
                    
                        path("/caja",()->{
                            post("/movimiento",CajaControlador::movimiento);
                            post("/venta",CajaControlador::altaVenta);
                            post("/{id}",CajaControlador::abrir);
                            put("/{id}",CajaControlador::cerrar);
                            get(CajaControlador::filtrar);
                            //  get("/{id}",CajaControlador::movimientosCaja);
                            get("/movimientos",CajaControlador::movimientosCajaOP);
                            get("/resumen",CajaControlador::resumenCaja);
//                            get("/ultimaCaja",CajaControlador::ultimaCaja);
                            
                        });
                    
                        path("/empleado",()->{
                            get("/filtrar",EmpleadoControlador::filtrar);
                            put("/{id}", EmpleadoControlador::modificar);
                            delete("/{id}", EmpleadoControlador::baja);
                            get("/{id}", EmpleadoControlador::buscar);
                            post(EmpleadoControlador::alta);
                            get(EmpleadoControlador::filtrarPaginado);
                        });
                    
                        path("/cliente",()->{
                            get("/filtrar",ClienteControlador::filtrar);
                            put("/{id}", ClienteControlador::modificar);
                            delete("/{id}", ClienteControlador::baja);
                            get("/{id}", ClienteControlador::buscar);
                            post(ClienteControlador::alta);
                            get(ClienteControlador::filtrarPaginado);
                        });
                    
                        path("/pedido",()->{
                            put("/{id}/actualizar", PedidoControlador::actualizar);
                            put("/{id}", PedidoControlador::modificar);
                            delete("/{id}", PedidoControlador::baja);
                            post(PedidoControlador::alta);
                            get(PedidoControlador::filtrarDetalladoPaginado);
                        });
                    
                        path("/insumo",()->{
                            put("/{id}",InsumoControlador::modificar);
                            delete(InsumoControlador::baja);
                            get(InsumoControlador::filtrarPaginado);
                            post(InsumoControlador::alta);
                        });
                    
                        path("/producto",()->{
                            put("/{id}",ProductoControlador::modificar);
                            delete(ProductoControlador::baja);
                            get(ProductoControlador::filtrarPaginado);
                            post(ProductoControlador::alta);
                        });
                    
                        path("/presupuesto",()->{
                            delete("/{id}",PresupuestoControlador::baja);
                            get(PresupuestoControlador::filtrarDetalladoPaginado);
                            get("/{id}",PresupuestoControlador::buscarDetallado);
                            post(PresupuestoControlador::alta);
                            put("/{id}/actualizar",PresupuestoControlador::actualizar);
                            put("/{id}",PresupuestoControlador::editar);
                                                
                        });
                    
                        path("/orden",()->{
                            //post(OrdenControlador::alta);
                            //put("/{id}", OrdenControlador::actualizar);
                            get("/cant", OrdenControlador::cantEstado);
                            get(OrdenControlador::filtrarDetalladoPaginado);
                            put("/{id}/actualizar",OrdenControlador::actualizar);
                        });
                    
                        path("/remito",()->{
                            post(RemitoControlador::alta);
                            put("/{id}",RemitoControlador::editar);
                            get(RemitoControlador::filtrarDetalladoPaginado);
                            put("/{id}/actualizar",RemitoControlador::actualizar);
                            get("/{id}",RemitoControlador::buscarDetallado);
                        
                        });
                    
                        path("/periodo",()->{
                            delete("/{id}", PeriodoControlador::baja);
                            get(PeriodoControlador::filtrarDetalladoPaginado);
                        });
                    
                        path("/gasto",()->{
                            delete("/{id}", GastoControlador::baja);
                            put("/{id}", GastoControlador::modificar);
                            post(GastoControlador::alta);
                        });
                    
                        path("/turno",()->{
                            get(TurnoControlador::filtrarDetalladoPaginado);
                            put("/{id}",TurnoControlador::modificar);
                            post("/{tipo}",TurnoControlador::altaConAsignacion);
                            get("/cant",TurnoControlador::cantEstado);
                            //delete("/{id}",TurnoControlador::baja);
                        });
                    });
                
                    path("/a",()->{
                        before(FiltroAutenticador::filtroAdmin);
                    
                        path("/caja",()->{
                            post(CajaControlador::alta);
                            delete("{id}",CajaControlador::baja);
                        });
                    
                        path("/usuario",()->{
                            delete("/{id}", UsuarioControlador::baja);
                            put("/{id}", UsuarioControlador::modificar);
                            put("/{id}/rol", UsuarioControlador::cambiarRol);
                            get("/filtrar", UsuarioControlador::filtrar);
                            get(UsuarioControlador::filtrarPaginado);
                        });
                    });

                });

            });
        }).start(puerto);
        // Rutas públicas de usuario
        app.post("/registrar", UsuarioControlador::registrar);
        app.post("/login", UsuarioControlador::login);
        app.post("/inicializar", EmpresaControlador::inicializar);

            //Esta no se si ponerla protegida o no, en teoria si, empleado/admin?
            app.get("/salir", (x -> {salir();  System.exit(0);}));
        generarPeriodosAutomatico();
    }

    public static int salir(){
        System.out.println("salida");
        app.stop();
        ConfiguracionGeneral.setInicializado(false);
        return 0;
    }

    private static void generarPeriodosAutomatico() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            DAOGasto.generarPeriodos(LocalDate.now());
        }, 0, 1, TimeUnit.DAYS); // Diario
    }
}
