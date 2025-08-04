package com.OndaByte.GestionComercio;

import com.OndaByte.GestionComercio.utilsTest.ClienteHttp;
import com.OndaByte.config.ConfiguracionGeneral;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppTestEmpleado {

    private static Logger logger = LogManager.getLogger(com.OndaByte.GestionComercio.AppTest.class.getName());

    private static long id_altaEmpleado = -1;
    private static long id_altaPedido = -1;
    private static long id_altaCliente = -1;

    String adminBody = "{\"user\":\"fran\",\"pass\":\"123\"}";
    String empleadoBody = "{\"user\":\"fran2\",\"pass\":\"123\"}";
    String usuarioBody = "{\"user\":\"fran3\",\"pass\":\"123\"}";

    private static String adminToken = null;
    private static String empleadoToken = null;
    private static String usuarioToken = null;

    String getAdminToken() throws Exception {
        if (adminToken == null) {
            HttpResponse<String> response = ClienteHttp.peticionPost("/login", adminBody, "token", "asd");
            JSONObject aux = new JSONObject(new JSONObject(response.body()).getString("data"));
            adminToken = aux.getString("token");
        }
        return adminToken;
    }

    String getUsuarioToken() throws Exception {
        if (usuarioToken == null) {
            HttpResponse<String> response = ClienteHttp.peticionPost("/login", usuarioBody, "token", "asd");
            JSONObject aux = new JSONObject(new JSONObject(response.body()).getString("data"));
            usuarioToken = aux.getString("token");
        }
        return usuarioToken;
    }

    String getEmpleadoToken() throws Exception {
        if (empleadoToken == null) {
            HttpResponse<String> response = ClienteHttp.peticionPost("/login", empleadoBody, "token", "asd");
            JSONObject aux = new JSONObject(new JSONObject(response.body()).getString("data"));
            empleadoToken = aux.getString("token");
        }
        return empleadoToken;
    }

    @BeforeAll
    static void iniciarServer() {
        ConfiguracionGeneral.init();
        ConfiguracionGeneral.setCONFIG_MYSQL_NAME("AntartidaTest?serverTimezone=America/Argentina/Buenos_Aires");
        ConfiguracionGeneral.setCONFIG_HTTP_API_PORT("5423");
        ConfiguracionGeneral.setInicializado(true);
        App.main(null);
    }

    @AfterAll
    static void apagarServer() {
        App.salir();
        ConfiguracionGeneral.setInicializado(false);
    }

    @Test
    @Order(1)
    void empleadoAlta_FormularioCorrecto_TokenAdmin_Retorna201() throws Exception {
        String body = "{\"nombre\":\"UnNombre UnApellido\",\"dni\":8765,\"telefono\":6666}";
        HttpResponse<String> response = ClienteHttp.peticionPost("/p/a/empleado/alta", body, "token", getAdminToken());
        //id_altaEmpleado = new JSONObject(new JSONObject(response.body()).getString("data")).getInt("id");
        assertEquals(201, response.statusCode());
    }


}
