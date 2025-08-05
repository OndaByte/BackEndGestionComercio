package com.OndaByte.GestionComercio.DAO;

import com.OndaByte.GestionComercio.modelo.Pedido;
import org.sql2o.Connection;

import java.util.List;

public interface DAOInterface<T> {
    public Integer alta(T t);
    public boolean baja(String id, boolean borrar);
    public boolean modificar(T t);
    // public boolean modificar(Pedido p, Connection con);
    public T buscar(String id);
    public List<T> listar();
    public List<T> listar(String... ids);
    public List<T> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones,List<Boolean> conectores);
}
