package com.OndaByte.GestionComercio.control;
import java.util.List;

import com.OndaByte.GestionComercio.DAO.DAOProducto;
import com.OndaByte.GestionComercio.modelo.Producto;
import com.OndaByte.GestionComercio.util.Controles;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;

public class ProductoControl{
    /*
	private static ObjectMapper objectMapper = new ObjectMapper();

	public static void listar(Context ctx) {
		try{
			DAOProducto dao = new DAOProducto();
			List<Producto> productos = dao.listar();
			ctx.status(200).json(productos);
		}
		catch(Exception e){
			Log.log(e,ProductoControl.class);
		}
    }

    public static void alta(Context ctx) {
		try {
			DAOProducto dao = new DAOProducto();
			Producto nuevo;
			nuevo = objectMapper.readValue(ctx.body(), Producto.class);
			if (dao.alta(nuevo)){
				ctx.status(201).result("Alta exitosa");
			}
		}
		catch(Exception e){
			Log.log(e,ProductoControl.class);
			ctx.status(500).result("Error al insertar producto");
		}       
    }

    public static void modificar(Context ctx)  {
		try {
			DAOProducto dao = new DAOProducto();
			Producto nuevo;
            
            String id = ctx.pathParam("id");
			nuevo = objectMapper.readValue(ctx.body(), Producto.class);
			nuevo.setId(Integer.parseInt(id));
			if (dao.modificar(nuevo)){
				ctx.status(201).result("Actualización exitosa");
			}
		}
		catch(Exception e){
			Log.log(e,ProductoControl.class);
			ctx.status(500).result("Error al actualizar el producto");
		}
    }

    public static void baja(Context ctx) {		
		try{
			DAOProducto dao = new DAOProducto();
			String id = ctx.pathParam("id");
			if (dao.baja(id, false)){
				ctx.status(200).result("Baja exitosa");
			}
		}
		catch(Exception e){
			Log.log(e,ProductoControl.class);
			ctx.status(500).result("Error al dar de baja");
		}
    }

    public static void sumarStock(Context ctx) {
		try {
			DAOProducto dao = new DAOProducto();
		
			String id = ctx.pathParam("id");
			String cant = ctx.queryParam("cant");
			if (dao.actualizarStock(id,cant)){
				ctx.status(200).result("Stock actualizado");
			}
		}
		catch(Exception e){
			Log.log(e,ProductoControl.class);
			ctx.status(500).result("Error al actualizar");
		}
    }

	public void controlesProducto(Producto nuevo){
        Controles.parametroStringNoVacioNulo("nombre", nuevo.getNombre());
		//        parametroStringNoVacioNulo("ingredientes_receta", nuevo.getIngredientes_receta());
        Controles.parametrosNoCeroNegativo("precio_costo",nuevo.getPrecio_costo(),true);
        Controles.parametrosNoCeroNegativo("precio_venta",nuevo.getPrecio_venta(),false);
        Controles.parametrosNoCeroNegativo("stock_actual", nuevo.getStock_actual(),true); 
        if(nuevo.getPrecio_costo() > nuevo.getPrecio_venta()){
            throw new IllegalArgumentException("El costo del producto no puede ser mayor a su precio de venta.");
        }
    }*/
}
