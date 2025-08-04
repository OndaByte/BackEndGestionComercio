package com.OndaByte.GestionComercio.utilsTest;

import com.OndaByte.config.ConfiguracionGeneral;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class ClienteHttp {

    private static HttpClient httpClient = null;

    private static void init(){if(httpClient == null){httpClient = HttpClient.newHttpClient();}}    
    
	public static HttpResponse<String> peticionPut(String endpoint, String body, String header, String header_val) throws Exception {
        init();
        String requestBody = body.toString();
        
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:"
                            +ConfiguracionGeneral.getCONFIG_HTTP_API_PORT()
                            + endpoint))
            .header("Content-Type", "application/json")
            .header(header,header_val)
            .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();
        return httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    }
    
    public static HttpResponse<String> peticionPost(String endpoint, String body, String header, String header_val) throws Exception {
        init();
        String requestBody = body.toString();
        
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:"
                            +ConfiguracionGeneral.getCONFIG_HTTP_API_PORT()
                            + endpoint))
            .header("Content-Type", "application/json")
            .header(header,header_val)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();
        return httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    }


    public static HttpResponse<String> peticionDelete(String endpoint, String header, String header_val) throws Exception {
        init();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:"
                        +ConfiguracionGeneral.getCONFIG_HTTP_API_PORT()
                        + endpoint))
                .header("Content-Type", "application/json")
                .header(header,header_val)
                .DELETE()
                .build();
        return httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    }



    public static HttpResponse<String> peticionGet(String endpoint, String header, String header_val) throws Exception {
        init();
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:"
                            +ConfiguracionGeneral.getCONFIG_HTTP_API_PORT()
                            + endpoint))
            .header("Content-Type", "application/json")
            .header(header,header_val)
            .GET()
            .build();
        return httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    }
}
