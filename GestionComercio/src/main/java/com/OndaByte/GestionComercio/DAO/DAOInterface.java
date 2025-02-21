package com.OndaByte.GestionComercio.DAO;

import java.util.List;

public interface DAOInterface<T> {
    public boolean alta(T t);
    public boolean baja(String id, boolean borrar);
    public boolean modificar(T t);
    public List<T> listar();
    public List<T> listar(String... ids);
    public List<T> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones);
}
