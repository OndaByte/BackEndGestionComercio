package com.OndaByte.GestionComercio.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Calendar;
import java.util.Date;

public class Seguridad {
	//ESTO SE TIENE QUE MOVER A UN CONFIG
    private static String clave = "IqZks/oD7sogY2zrLdkcluLiezFP/s/UbUsxiGEV/ksvFNREePrGYvX5e6dO7xC0KE7LDkyQMfNW";
    private static int expiracion = 42;
    private static int limite = expiracion;

    /**
     * Devuelve un token dado un usuario.
	 *
     * @param usuario
     * @return token
    **/
    public static String getToken(String usuario){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, expiracion);
        try{
        return Jwts.builder()
            .setSubject(usuario)
            .setExpiration(cal.getTime())
			.signWith(SignatureAlgorithm.HS512, clave.getBytes())
            .compact();
        }
        catch (Exception e){
            Log.log(e, Seguridad.class);
        }
        return null;
    }

	  /**
     * Valida si el token es valido/no expiro, retorna el token si es valido/uno nuevo si habia expirado, nulo si el token era invalido.
	 * 
     * @param usuario
     * @return token
    **/
    public static String validar(String token){
	    try{
			Jwts.parserBuilder()
			.setSigningKey(clave.getBytes())
			.build()
            .parseClaimsJws(token);
            return token;
        }
        catch (ExpiredJwtException e){
            Claims cls = e.getClaims();
            Date exp = cls.getExpiration();
            String usr = cls.getSubject();
            Date aux = new Date();
            long milSeg = aux.getTime() - exp.getTime();
            long hras = milSeg/ 3600000;
            if (hras > limite){
                return null;
            }
            return getToken(usr);
        }
        catch (Exception e){
            Log.log(e, Seguridad.class);
        }
        return null;
    }
}
