package com.OndaByte.GestionComercio.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

public class Parsero {
    /**
     * Nulo si retorna invalido
     * @param valor
     * @return 
     */
    public static Integer safeParse(String valor) {
        try {
            return valor != null ? Integer.valueOf(valor) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    public static JSONObject toJson(Object obj) throws JsonProcessingException {
        return new JSONObject(new ObjectMapper().writeValueAsString(obj));
    }
}
