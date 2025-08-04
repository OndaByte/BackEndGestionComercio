package com.OndaByte.GestionComercio;
import com.OndaByte.GestionComercio.utilsTest.ClienteHttp;
import com.OndaByte.config.ConfiguracionGeneral;

import java.net.http.HttpResponse;

import org.json.JSONObject;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AppTest {
    
    private static Logger logger = LogManager.getLogger(AppTest.class.getName());
    
    private static long id_altaUsuario = -1;
    private static long id_altaPedido = -1;
    private static long id_altaCliente = -1;
    
    String adminBody = "{\"user\":\"fran\",\"pass\":\"123\"}";
    String empleadoBody = "{\"user\":\"fran2\",\"pass\":\"123\"}";
    String usuarioBody = "{\"user\":\"fran3\",\"pass\":\"123\"}";
    
    private static String adminToken = null;
    private static String empleadoToken = null;
    private static String usuarioToken = null;
    
    String getAdminToken() throws Exception {
        if(adminToken == null){
            HttpResponse<String> response = ClienteHttp.peticionPost("/login", adminBody, "token","asd");
            JSONObject aux = new JSONObject(new JSONObject(response.body()).getString("data"));
            adminToken = aux.getString("token");
        }
        return adminToken;
    }

    String getUsuarioToken() throws Exception{
        if(usuarioToken == null){
            HttpResponse<String> response = ClienteHttp.peticionPost("/login", usuarioBody, "token","asd");
            JSONObject aux = new JSONObject(new JSONObject(response.body()).getString("data"));
            usuarioToken = aux.getString("token");
        }
        return usuarioToken;
    }

    String getEmpleadoToken() throws Exception{
        if(empleadoToken == null){
            HttpResponse<String> response = ClienteHttp.peticionPost("/login", empleadoBody, "token","asd");
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
    void pedido_AltaExitosa_retorna201() throws Exception{
        String pedido = "{\"cliente_id\": 1,\"descripcion\":\"pedido test 1\"}";
        String turno = "{\"fecha_inicio\":\"2025-05-05\",\"descripcion\":\"turno test 1\"}";
        
        String body = "{\"turno\": "+turno+",\"pedido\":"+pedido+"}";
        HttpResponse<String> response = ClienteHttp.peticionPost("/p/e/pedido/alta",body,"token",getEmpleadoToken());
        assertEquals(201, response.statusCode());
        
    }



    



    
    

    @Test
    void registrarse_formularioIncorrecto_retorna400() throws Exception {
        String body = "{\"user\":\" ; \",\"pass\":\" :\"}";
        HttpResponse<String> response = ClienteHttp.peticionPost("/registrar",body,"token","");
        assertEquals(400, response.statusCode());
    }

    
    @Test
    @Order(1)
    void registrarse_retorna201() throws Exception {
        String body = "{\"user\":\"usuario\",\"pass\":\"usuario\"}";
        HttpResponse<String> response = ClienteHttp.peticionPost("/registrar",body,"token","");
        
        id_altaUsuario = new JSONObject(new JSONObject(response.body()).getString("data")).getLong("id");
        assertEquals(201, response.statusCode());
    }
    
    @Test
    void Admin_bajaUsuario_SinID_Retorna404() throws Exception {
        HttpResponse<String> response = ClienteHttp.peticionDelete("/p/a/usuario/"+"-1"+"/baja", "token",getAdminToken());
        assertEquals(404, response.statusCode());
    }

    @Test
    void Usuario_bajaUsuario_TokenUsuario_Retorna401() throws Exception {
        HttpResponse<String> response = ClienteHttp.peticionDelete("/p/a/usuario/3/baja", "token",getUsuarioToken());
        assertEquals(401, response.statusCode());
    }

    @Test
    @Order(2)
    void admin_bajaUsuario_Retorna204() throws Exception {
        HttpResponse<String> response = ClienteHttp.peticionDelete("/p/a/usuario/"+id_altaUsuario+"/baja", "token",getAdminToken());
        assertEquals(204, response.statusCode());
    }


    // CAMBIAR ROL
    @Test
    void cambiarRol_formularioIcorrecto_Retorna400() throws Exception {
        String body = "{\"rol\":\"rol\"}";
        HttpResponse<String> response = ClienteHttp.peticionPut("/p/a/usuario/3/rol", body, "token",getAdminToken());
        assertEquals(400, response.statusCode());
    }
    /*
    @Test
    void Admin_cambiarRol_aUsuario_Retorna401() throws Exception {
        String body = "{\"rol\":\"USUARIO\"}";
        HttpResponse<String> response = ClienteHttp.peticionPut("/p/a/usuario/"+"1"+"/rol", body, "token",getAdminToken());
        assertEquals(401, response.statusCode());
    }

    @Test
    @Order(1)
    void cambiarRol_Exitoso_Retorna201() throws Exception {
        String body = "{\"rol\":\"ADMIN\"}";
        HttpResponse<String> response = ClienteHttp.peticionPut("/p/a/usuario/3/rol", body, "token",getAdminToken());
        assertEquals(201, response.statusCode());
    }

    @Test
    @Order(2)
    void usuario_cambiarRol_aAdmin_Retorna201() throws Exception {
        String body = "{\"rol\":\"ADMIN\"}";
        HttpResponse<String> response = ClienteHttp.peticionPut("/p/a/usuario/"+"1"+"/rol", body, "token",getAdminToken());
        assertEquals(201, response.statusCode());
    }
    
    @Test
    void usuario_noExiste_cambiarRol_Retorna404() throws Exception {
        String body = "{\"rol\":\"ADMIN\"}";
        HttpResponse<String> response = ClienteHttp.peticionPut("/p/a/usuario/"+"-1"+"/rol", body, "token",getAdminToken());
        assertEquals(404, response.statusCode());
    }
    */
    // ALTA CLIENTE
    @Test
    @Order(1)
    void clienteAlta_FormularioCorrecto_TokenEmpleado_Retorna201() throws Exception {
        String body = "{\"nombre\":\"UnNombre UnApellido\",\"dni\":4444,\"cuit_cuil\":6666}";
        HttpResponse<String> response = ClienteHttp.peticionPost("/p/e/cliente/alta",body,"token",getEmpleadoToken());
        id_altaCliente = new JSONObject(new JSONObject(response.body()).getString("data")).getInt("id");
        assertEquals(201, response.statusCode());
    }
    
    @Test
    void clienteAlta_SinToken_Retorna403() throws Exception {
        String body = "{\"nombre\":\"UnNombre\",\"dni\":\"UnDNI\",\"cuit_cuil\":\"UnCUIT\"}";
        HttpResponse<String> response = ClienteHttp.peticionPost("/p/e/cliente/alta",body,"token","asd");
        assertEquals(403, response.statusCode());
    }

    @Test
    void clienteAlta_formularioInvalido_TokenEmpleado_Retorna400() throws Exception {
        String body = "{\"nombre\":\"?\",\"dni\":\"?\",\"cuit_cuil\":\"?\"}";
        HttpResponse<String> response = ClienteHttp.peticionPost("/p/e/cliente/alta",body,"token",getEmpleadoToken());
        assertEquals(400, response.statusCode());
    }
    
    @Test
    void clienteAlta_formularioIncorrecto_TokenEmpleado_Retorna400() throws Exception {
        String body = "{\"nombre\":\"Un Nuevo Cliente\",\"dni\":\"\",\"cuit_cuil\":\"\"}";
        HttpResponse<String> response = ClienteHttp.peticionPost("/p/e/cliente/alta",body,"token",getEmpleadoToken());
        assertEquals(400, response.statusCode());
    }
    //  MODIFICAR CLIENTE
    @Test
    void cliente_ModificarBodyIncorrecto_empleado_Retorna400() throws Exception {
        String body = "{\"nombre\":\"?\",\"dni\":\"?\",\"cuit_cuil\":\"?\",\"cond_iva\":\"exento\"}";
        HttpResponse<String> response = ClienteHttp.peticionPut("/p/e/cliente/3/modificar",body,"token",getEmpleadoToken());
        assertEquals(400, response.statusCode());
    }

    //  BAJA CLIENTE
    /*
     *
     * ctx.status(200).result(buildRespuesta(201, null, "Baja exitosa"));
     */
    @Test
    @Order(2)
    void cliente_BajaExitosa_empleado_Retorna200() throws Exception {
        HttpResponse<String> response = ClienteHttp.peticionDelete("/p/e/cliente/"+id_altaCliente+"/baja","token",getEmpleadoToken());
        assertEquals(200, response.statusCode());
    }


    //  BAJA CLIENTE
    @Test
    @Order(2)
    void cliente_Baja_idNoExiste_empleado_Retorna404() throws Exception {
        logger.info("ID de alta: "+ id_altaCliente);
        HttpResponse<String> response = ClienteHttp.peticionDelete("/p/e/cliente/689/baja","token",getEmpleadoToken());
        //id_altaCliente = new JSONObject(new JSONObject(response.body()).getString("data")).getInt("id");
        assertEquals(404, response.statusCode());
    }


    //  CLIENTE INACTIVO CAMBIO DE COND_IVA
    @Test
    @Order(3)
    void clienteInactivo_Modificar_BodyIncorrecto_empleado_Retorna400() throws Exception {
        String body = "{\"nombre\":\"?\",\"dni\":\"?\",\"cuit_cuil\":\"?\",\"cond_iva\":\"exento\"}";
        HttpResponse<String> response = ClienteHttp.peticionPut("/p/e/cliente/"+id_altaCliente+"/modificar",body,"token",getEmpleadoToken());
        //id_altaCliente = new JSONObject(new JSONObject(response.body()).getString("data")).getInt("id");
        assertEquals(400, response.statusCode());
    }

    //  CLIENTE INACTIVO CAMBIO DE COND_IVA
    @Test
    @Order(3)
    void clienteInactivo_Modificar_BodyCorrecto_empleado_Retorna201() throws Exception {
        String body = "{\"nombre\":\"?\",\"dni\":\"4444\",\"cuit_cuil\":\"6666\",\"cond_iva\":\"exento\"}";
        HttpResponse<String> response = ClienteHttp.peticionPut("/p/e/cliente/"+id_altaCliente+"/modificar",body,"token",getEmpleadoToken());
        //id_altaCliente = new JSONObject(new JSONObject(response.body()).getString("data")).getInt("id");
        assertEquals(201, response.statusCode());
    }


    //  LISTAR CLIENTES
    @Test
    void listar_clientes_empleado_Retorna200() throws Exception {
        HttpResponse<String> response = ClienteHttp.peticionGet("/p/e/clientes","token",getEmpleadoToken());
        assertEquals(200, response.statusCode());
    }

    @Test
    void listar_clientes_tokenInvalido_Retorna403() throws Exception {
        HttpResponse<String> response = ClienteHttp.peticionGet("/p/e/clientes","token","asd");
        assertEquals(403, response.statusCode());
    }


    //  LISTAR PEDIDOS
     @Test
     void listar_pedidos_empleado_Retorna200() throws Exception {
     HttpResponse<String> response = ClienteHttp.peticionGet("/p/e/pedidos","token",getEmpleadoToken());
     assertEquals(200, response.statusCode());
     }

     @Test
     void listar_pedidos_tokenUsuario_Retorna403() throws Exception {
     HttpResponse<String> response = ClienteHttp.peticionGet("/p/e/pedidos","token",getUsuarioToken());
     assertEquals(403, response.statusCode());
     }

     @Test
     void listar_pedidos_tokenInvalido_Retorna403() throws Exception {
     HttpResponse<String> response = ClienteHttp.peticionGet("/p/e/pedidos","token","asd");
     assertEquals(403, response.statusCode());
     }


     //  LISTAR PEDIDOS Y CLIENTES
     @Test
     void listar_pedidosYClientes_empleado_Retorna200() throws Exception {
     HttpResponse<String> response = ClienteHttp.peticionGet("/p/e/pedidosYClientes","token",getEmpleadoToken());
     assertEquals(200, response.statusCode());
     }

     @Test
     void listar_pedidosYClientes_tokenUsuario_Retorna403() throws Exception {
     HttpResponse<String> response = ClienteHttp.peticionGet("/p/e/pedidosYClientes","token",getUsuarioToken());
     assertEquals(403, response.statusCode());
     }

     @Test
     void listar_pedidosYClientes_tokenInvalido_Retorna403() throws Exception {
     HttpResponse<String> response = ClienteHttp.peticionGet("/p/e/pedidosYClientes","token","asd");
     assertEquals(403, response.statusCode());
     }


     //  LISTAR INSUMOS
     @Test
     void listar_insumos_empleado_Retorna200() throws Exception {
     HttpResponse<String> response = ClienteHttp.peticionGet("/p/e/insumos","token",getEmpleadoToken());
     assertEquals(200, response.statusCode());
     }

     @Test
     void listar_insumos_tokenUsuario_Retorna403() throws Exception {
     HttpResponse<String> response = ClienteHttp.peticionGet("/p/e/insumos","token",getUsuarioToken());
     assertEquals(403, response.statusCode());
     }

     @Test
     void listar_insumos_tokenInvalido_Retorna403() throws Exception {
     HttpResponse<String> response = ClienteHttp.peticionGet("/p/e/insumos","token","asd");
     assertEquals(403, response.statusCode());
     }

     //  CAMBIAR CONTRA
     /*
     *
     *  try catch
     *
     *
     */
    @Test
    @Order(2)
    void cambiarContra_retorno201(){
        String body = "{\"pass\":\"123\",\"nueva\":\"1234\"}";
        HttpResponse<String> response = null;
        try {
            response = ClienteHttp.peticionPut("/p/actualizar",body,"token",getEmpleadoToken());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(201, response.statusCode());
    }

    @Test
    @Order(2)
    void cambiarContra_passInvalido_retorno401(){
        String body = "{\"pass\":\"invalido\",\"nueva\":\"nueva\"}";
        HttpResponse<String> response = null;
        try {
            response = ClienteHttp.peticionPut("/p/actualizar",body,"token",getEmpleadoToken());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(401, response.statusCode());
    }


    @Test
    @Order(2)
    void cambiarContra_contraInvalida_retorno400(){
        String body = "{\"pass\":\"asd\",\"nu\":\"nueva\"}";
        HttpResponse<String> response = null;
        try {
            response = ClienteHttp.peticionPut("/p/actualizar",body,"token",getUsuarioToken());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(400, response.statusCode());
    }
    /*
    @Test
    @Order(1)
    void orden_Actualizar_Exitosa_retorna201(){
        String body = "{\"estado_orden\":\"CANCELADA\"}";
        HttpResponse<String> response = null;
        try {
            response = ClienteHttp.peticionPut("/p/e/orden/1/actualizar",body,"token",getEmpleadoToken());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(201, response.statusCode());
    }

    @Test
    @Order(1)
    void orden_Actualizar_Exitosa2_retorna201(){
        String body = "{\"estado_orden\":\"ENTREGADA\"}";
        HttpResponse<String> response = null;
        try {
            response = ClienteHttp.peticionPut("/p/e/orden/2/actualizar",body,"token",getEmpleadoToken());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(201, response.statusCode());
    }

    @Test
    @Order(1)
    void orden_Actualizar_invalido_retorna400(){
        String body = "{\"estado_orden\":\"invalido\"}";
        HttpResponse<String> response = null;
        try {
            response = ClienteHttp.peticionPut("/p/e/orden/1/actualizar",body,"token",getEmpleadoToken());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(400, response.statusCode());
    }


    @Test
    @Order(1)
    void orden_Actualizar_IDinvalido_retorna404(){
        String body = "{\"estado_orden\":\"ASIGNADA\"}";
        HttpResponse<String> response = null;
        try {
            response = ClienteHttp.peticionPut("/p/e/orden/-1/actualizar",body,"token",getEmpleadoToken());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(404, response.statusCode());
    }
    */
    /*
    // LOGIN
    @Test
    void login_Retorna400() throws Exception {
    String body = "{\"usr\":\"\",\"pass\":\"\"}";
    HttpResponse<String> response = ClienteHttp.peticionPost("/login", body, "token","asd");
    assertEquals(400, response.statusCode());
    }

    @Test
    void login_usuario_Retorna200() throws Exception {
    HttpResponse<String> response = ClienteHttp.peticionPost("/login", usuarioBody, "token","asd");
    JSONObject aux =new JSONObject(new JSONObject(response.body()).getString("data"));
    assertEquals(200, response.statusCode());
    }
    @Test
    void login_empleado_Retorna200() throws Exception {
    HttpResponse<String> response = ClienteHttp.peticionPost("/login", empleadoBody, "token","asd");
    JSONObject aux =new JSONObject(new JSONObject(response.body()).getString("data"));
    assertEquals(200, response.statusCode());
    }

    @Test
    void login_admin_Retorna200() throws Exception {
    HttpResponse<String> response = ClienteHttp.peticionPost("/login", adminBody, "token","asd");
    JSONObject aux =new JSONObject(new JSONObject(response.body()).getString("data"));
    assertEquals(200, response.statusCode());
    }

    @Test
    void login_Retorna403() throws Exception {
    String body = "{\"user\":\"fran\",\"pass\":\"12asdasd3\"}";

    HttpResponse<String> response = ClienteHttp.peticionPost("/login", body, "token","asd");
    assertEquals(403, response.statusCode());
    }
    */

    /*
    //RUTAS USUARIOS
    @Test
    void usuarios_token_invalido_Retorna403() throws Exception {
    HttpResponse<String> response = ClienteHttp.peticionGet("/p/a/usuarios","token","asdasd");
    assertEquals(403, response.statusCode());
    }
    
    @Test
    void usuarios_admin_Retorna200() throws Exception {
    HttpResponse<String> response = ClienteHttp.peticionGet("/p/a/usuarios","token",getAdminToken());
    assertEquals(200, response.statusCode());
    }
    
    @Test
    void usuarios_empleado_Retorna401() throws Exception {
    HttpResponse<String> response = ClienteHttp.peticionGet("/p/a/usuarios","token",getEmpleadoToken());
    assertEquals(401, response.statusCode());
    }
    
    @Test
    void usuarios_usuario_Retorna401() throws Exception {
    HttpResponse<String> response = ClienteHttp.peticionGet("/p/a/usuarios","token",getUsuarioToken());
    assertEquals(401, response.statusCode());
    }
    */

    /*
    // RUTAS CLIENTES
    @Test
    void clientes_token_invalido_Retorna403() throws Exception {
    HttpResponse<String> response = ClienteHttp.peticionGet("/p/e/clientes","token","asdasd");
    assertEquals(403, response.statusCode());
    }
    
    @Test
    void clientes_admin_Retorna200() throws Exception {
    HttpResponse<String> response = ClienteHttp.peticionGet("/p/e/clientes","token",getAdminToken());
    assertEquals(200, response.statusCode());
    }
    
    @Test
    void clientes_empleado_Retorna200() throws Exception {
    HttpResponse<String> response = ClienteHttp.peticionGet("/p/e/clientes","token",getEmpleadoToken());
    assertEquals(200, response.statusCode());
    }
    @Test
    void clientes_usuario_Retorna401() throws Exception {
    HttpResponse<String> response = ClienteHttp.peticionGet("/p/e/clientes","token",getUsuarioToken());
    assertEquals(401, response.statusCode());
    }
    */

    /*
    //RUTAS PEDIDOS
    @Test
    void pedidos_empleado_Retorna200() throws Exception {
    HttpResponse<String> response = ClienteHttp.peticionGet("/p/e/pedidos","token",getEmpleadoToken());
    assertEquals(200, response.statusCode());
    }
    @Test
    void pedidos_usuario_Retorna401() throws Exception {
    HttpResponse<String> response = ClienteHttp.peticionGet("/p/e/pedidos","token",getUsuarioToken());
    assertEquals(401, response.statusCode());
    }
    
    @Test
    void pedido_Actualizar_token_invalido_Retorna403() throws Exception {        
    String body = "{\"estado_pedido\":\"presupuestado\"}";
    HttpResponse<String> response = ClienteHttp.peticionPut("/p/e/pedido/1/actualizar",body,"token","asdasd");
    assertEquals(403, response.statusCode());
    }
    
    @Test
    void pedido_Actualizar_empleado_pedidoArchivado_Retorna400() throws Exception {
    HttpResponse<String> response = ClienteHttp.peticionPut("/p/e/pedido/2/actualizar","","token",getEmpleadoToken());
    assertEquals(400, response.statusCode());
    }

    @Test
    void pedido_Actualizar_empleado_bodyIncorrecto_Retorna400() throws Exception {
    String body = "{\"estado_pedido\":\"\"}";
    HttpResponse<String> response = ClienteHttp.peticionPut("/p/e/pedido/2/actualizar",body,"token",getEmpleadoToken());
    assertEquals(400, response.statusCode());
    }

    @Test
    void pedido_Actualizar_empleado_estadoIncorrecto_Retorna400() throws Exception {
    String body = "{\"estado_pedido\":\"asdasd\"}";
    HttpResponse<String> response = ClienteHttp.peticionPut("/p/e/pedido/2/actualizar",body,"token",getEmpleadoToken());
    assertEquals(400, response.statusCode());
    }  
    
    @Test
    void pedido_Actualizar_usuario_Retorna401() throws Exception {
    String body = "{\"estado_pedido\":\"presupuestado\"}";
    HttpResponse<String> response = ClienteHttp.peticionPut("/p/e/pedido/1/actualizar",body,"token",getUsuarioToken());
    assertEquals(401, response.statusCode());
    }
    
    @Test
    void pedido_Actualizar_admin_Retorna201() throws Exception {
    String body = "{\"estado_pedido\":\"presupuestado\"}";
    HttpResponse<String> response = ClienteHttp.peticionPut("/p/e/pedido/1/actualizar",body,"token",getAdminToken());
    assertEquals(201, response.statusCode());
    }
    
    @Test
    void pedido_Actualizar_empleado_Retorna201() throws Exception {
    String body = "{\"estado_pedido\":\"pendiente\"}";
    HttpResponse<String> response = ClienteHttp.peticionPut("/p/e/pedido/1/actualizar",body,"token",getEmpleadoToken());
    assertEquals(201, response.statusCode());
    }
    
    @Test
    @Order(1)
    void pedido_Alta_empleado_Retorna201() throws Exception {
    String body = "{\"cliente_id\":1,\"descripcion\":\"pedido dado de alta en testing.\"}";
    HttpResponse<String> response = ClienteHttp.peticionPost("/p/e/pedido/alta",body,"token",getEmpleadoToken());
    id_altaPedido = new JSONObject(new JSONObject(response.body()).getString("data")).getInt("id");
    assertEquals(201, response.statusCode());
    }

    @Test
    @Order(2)
    void pedido_Baja_admin_Retorna200() throws Exception {
    HttpResponse<String> response = ClienteHttp.peticionDelete("/p/a/pedido/"+id_altaPedido+"/baja","token",getAdminToken());
    assertEquals(200, response.statusCode());
    }

    /*

    //Rutas ordenes
    @Test
    void ordenes_empleado_Retorna200() throws Exception {
    HttpResponse<String> response = ClienteHttp.peticionGet("/p/e/ordenes","token",getAdminToken());
    assertEquals(200, response.statusCode());
    } */

    /*
      private int pedido_id;
      private String descripcion;
      private String precio_final;
      private String costo_total;
      private String estado_orden;
      private String fecha_fin;
      private String tipo;
    */

    /*
      @Test
      void pedido_Actualizar_AltaOrden_empleado_Retorna201() throws Exception {
      String body = "{\"cliente_id\":2,\"descripcion\":\"pedido dado de alta en testing.\"}";
      HttpResponse<String> response = ClienteHttp.peticionPost("/p/e/pedido/alta",body,"token",getEmpleadoToken());
      long id_Pedido = new JSONObject(new JSONObject(response.body()).getString("data")).getInt("id");

        
      //  body = "{\"estado_pedido\":\"aprobado\",\"orden\":{\"descripcion\":\"orden dada de alta en testing\",\"precio_final\":10,\"costo_total\":5,\"tipo\":\"auto\",\"fecha_fin\":\"2025-03-20\",\"estado_orden\":\"pendiente\"},\"turno_id\":2}";

      body = "{\"estado_pedido\":\"aprobado\",\"orden\":{\"descripcion\":\"orden dada de alta en testing\",\"precio_final\":10,\"costo_total\":5},\"turno_id\":2}";
        
      HttpResponse<String> response2 = ClienteHttp.peticionPut("/p/e/pedido/"+id_Pedido+"/actualizar",body,"token",getEmpleadoToken());
      assertEquals(201, response2.statusCode());
      }

    */
}
