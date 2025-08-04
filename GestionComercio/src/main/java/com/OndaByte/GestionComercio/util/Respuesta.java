package com.OndaByte.GestionComercio.util;

import org.json.JSONObject;

public class Respuesta {
    /**
     * 
     * @param status estado http
     * @param data payload-data-info.
     * @param mensaje mensaje
     * @return 
     */
    public static String buildRespuesta(int status, String data, String mensaje) {
        JSONObject respuesta = new JSONObject();
        respuesta.put("status",status);
        respuesta.put("data",data);
        respuesta.put("mensaje",mensaje);
        return respuesta.toString();
    }
    /**
     * 
     * @param status estado http
     * @param data payload-data-info.
     * @param mensaje mensaje
     * @param pagina nro. de página
     * @param elementos elementos por página
     * @param totalElementos total de elementos 
     * @param totalPaginas total de páginas 
     * @return respuestaJson
     */
    public static String buildRespuesta(int status, String data, String mensaje,String pagina, String elementos, String totalElementos, String totalPaginas) {
        JSONObject respuesta = new JSONObject();
        respuesta.put("status",status);
        respuesta.put("data",data);
        respuesta.put("pagina",pagina);
        respuesta.put("elementos",elementos);
        respuesta.put("t_elementos",totalElementos);
        respuesta.put("t_paginas",totalPaginas);
        respuesta.put("mensaje",mensaje);
        return respuesta.toString();
    }

    public static String buildRespuesta(int status, String data, String mensaje,String pagina, String elementos, String totalElementos, String totalPaginas, String costo_total) {
        JSONObject respuesta = new JSONObject();
        respuesta.put("status",status);
        respuesta.put("data",data);
        respuesta.put("pagina",pagina);
        respuesta.put("elementos",elementos);
        respuesta.put("t_elementos",totalElementos);
        respuesta.put("t_paginas",totalPaginas);
        respuesta.put("costo_total",costo_total);
        respuesta.put("mensaje",mensaje);
        return respuesta.toString();
    }
    
}
