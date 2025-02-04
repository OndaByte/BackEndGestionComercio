package com.OndaByte.GestionComercio.filtros;
 

import io.javalin.http.Context;
import io.javalin.http.Handler;

import com.OndaByte.GestionComercio.util.Seguridad;

/**
 * FiltroAutenticador
 * Autor: Fran
 */
public class FiltroAutenticador {
    public static void filtro(Context ctx) {
        String aux = ctx.header("Token");
        
        if (aux != null) {
            String token = Seguridad.validar(aux);
            if (token != null && token.equals(aux)) {
                ctx.header("Token", token);
                ctx.status(204);
            } else {
                ctx.redirect("/login");
            }
        } else {
            ctx.redirect("/login");
        }
    };
}

/**
 * 
 * 
 * 
import spark.Filter;
import spark.Request;
import spark.Response;

import com.OndaByte.GestionComercio.util.Seguridad;


public class FiltroAutenticador {
    public static Filter filtro = (Request req, Response res) -> {
        String aux = req.headers("Token");
        if (aux != null){
            String token = Seguridad.validar(aux);
            if(token != null && token.equals(aux)){
                res.header("Token", token);
                res.status(204);
            }
            else{
                res.redirect("/login");
            }
        }
        else{
            res.redirect("/login");
        }
    };
}

 */
