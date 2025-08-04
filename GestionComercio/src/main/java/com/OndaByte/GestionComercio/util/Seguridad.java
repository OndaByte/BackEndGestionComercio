package com.OndaByte.GestionComercio.util;

import java.security.Key;

import io.jsonwebtoken.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Seguridad {
    //ESTO SE TIENE QUE MOVER A UN CONFIG
    private static int expiracion = 42;
    private static int maxLimite = 24;
    private static Key clave = io.jsonwebtoken.security.Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static Logger logger = LogManager.getLogger(Seguridad.class.getName());

    /**
     * Devuelve un token dado un usuario.
	 *
     * @param usuario
     * @return token
     **/
    public static String getToken(String id){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, expiracion);
        try{
            return Jwts.builder()
                .setSubject(id)
                .setExpiration(cal.getTime())
                .signWith(clave, SignatureAlgorithm.HS512)
                .compact();
        }
        catch (Exception e){
            logger.error("GetToken: " + e);
        }
        return null;
    }

    /**
     * Valida si el token es valido/no expiro, retorna el token si es valido/uno nuevo si habia expirado, nulo si el token era invalido.
	 * 
     * @param usuario
     * @return token
     **/
    public static Claims validar(String token){
	    try{
            return Jwts.parserBuilder()
                .setSigningKey(clave)
                .build()
                .parseClaimsJws(token).getBody();
        }
        catch (ExpiredJwtException e){
            Claims cls = e.getClaims();
            Date exp = cls.getExpiration();
            String usr = cls.getSubject();
            Date aux = new Date();
            long milSeg = aux.getTime() - exp.getTime();
            long hras = milSeg/ 3600000;
            if (hras > maxLimite){
                return null;
            }            
            return Jwts.parserBuilder()
                .setSigningKey(clave)
                .build()
                .parseClaimsJws(getToken(usr)).getBody();
        }
        catch (Exception e){
            logger.error("Validar: " + e);
        }
        return null;
    }
}
