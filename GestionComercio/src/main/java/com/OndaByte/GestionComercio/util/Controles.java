package com.OndaByte.GestionComercio.util;

import java.util.List;

//import com.OndaByte.GestionComercio.modelo.ProductoItem;

public class Controles {
    public static void parametrosNoCeroNegativo(String nombre, float valor, boolean cero){
        if(cero){
            if(valor < 0){
                throw new IllegalArgumentException("El parametro '"+nombre+"' no puede ser negativo.\n");
            }
        }
        else if(valor <=0){
            throw new IllegalArgumentException("El parametro '"+nombre+"' debe ser positivo.\n");
        }
    }

    public static void parametroStringNoVacioNulo(String nombre, String valor){
        if(valor == null || valor.length()==0){
            throw new IllegalArgumentException("El parametro '"+nombre+"' no puede ser vacio.\n");
        }
    }

    public static void controlID(long id){
        if(id != 0){
            throw new IllegalArgumentException("No se puede inicializar el ID.\n");
        }
    }
/*
    public static void controlTotal(List<ProductoItem> prods, float total) throws Exception {
        float aux = 0;
        for(ProductoItem p : prods){
            aux += p.getSubtotal();
        }
        if (aux != total)
            throw new IllegalArgumentException("El precio total no coincide con los items.");
    }
*/
}
