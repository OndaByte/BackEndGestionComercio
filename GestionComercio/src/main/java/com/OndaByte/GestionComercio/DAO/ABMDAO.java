package com.OndaByte.GestionComercio.DAO;

import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2oException;

/**
 * ABMDAO generico SQL para entidades simples, soporta maximo una herencia de entidad, ej Usuario extend ObjetoBD (esto es old, hay que cambiarlo)
 * @param <T>
 */
public abstract class ABMDAO <T> {

    // Campos es una lista de string de todos los campos de las clases definidas por nosotros (ondabyte) y que ademas esten en el paquete "modelo", esto se podria abstraer mas
    private List<Field> campos = new ArrayList<Field>();
    protected Connection con;
    private static Logger logger = LogManager.getLogger(ABMDAO.class.getName());

    public ABMDAO(){setCampos();}
    /**
     * Este metodo debe ser definido por las clases especificas que hereden de ABMDAO, deberia devolver el tipo del objeto asociado a la entidad de la bd.
     * @return T
     */
    abstract public Class<T> getClase();
    /**
     * Este metodo debe ser definido por las clases especificas que hereden de ABMDAO, deberia devolver el nombre del campo clave de la entidad.
     * @return String
     */
    abstract public String getClave();

    public void setConexion(Connection con) {this.con = con;}

    public Connection getConexion() {return con;}

    public String getTabla(){return this.getClase().getSimpleName();}

    // Reviso que sean clases definidas por OndaByte del paquete modelo.
    private void setCampos(){
        Class clase = this.getClase();
        while(clase.getName().contains("OndaByte") && clase.getName().contains("modelo")){
            for (Field f : clase.getDeclaredFields()){
                campos.add(f);
            }
            clase = clase.getSuperclass();
        }
    }

    /**
     * Da de alta un objeto de la clase T en la base de datos.
     *
     * @param T t - elemento a ser dado de alta.
     *
     * @return int el número de insersiones.
     */
    public boolean alta(T t)  throws Exception {
            // Esto tendria que abstraerlo a un gestor de errores.
            if(t.getClass() != this.getClase()){
                logger.error("ABMDAO.alta: Tipo de objeto incorrecto. Se esperaba " + this.getClase().getName() + " pero se recibió " + t.getClass().getName());
                return false;
            }

            // Tendria que abstraer la conversion de objeto a lista de clave valor
            String columnas = " (";
            String valores = " (";
            for (Field f : this.campos){
                if (f.getName().equals(this.getClave()) || f.getName().equals("creado") || f.getName().equals("ultMod")) continue;
                columnas = columnas + f.getName() + ",";
                valores = valores + ":" + f.getName() + ",";
            }
            valores = valores.substring(0,valores.length()-1) + ")";
            columnas = columnas.substring(0,columnas.length()-1)+ ")";
            String query;
            query = "INSERT INTO " + this.getTabla() + columnas + " VALUES" + valores;

            if (logger.isDebugEnabled()) {
                logger.debug("ABMDAO.alta - SQL para inserción: " + query);
            }

            Query consulta = con.createQuery(query).bind(t);
            int filasAfectadas = consulta.executeUpdate().getResult(); // Ejecuta la inserción
            
            return filasAfectadas>0;
    }

    
    /**
     * Modifica la entidad en la base de datos con la misma clave que t,
     * se le asignan todos los atributos del objeto t a la entidad de la bd correspondiente
     *
     * @param T t - elemento a actualizar en bd.
     *
     * @return boolean - verdadero si la modificacion fue exitosa.
     */
    public boolean modificar(T t)  throws Exception{
            // Esto tendria que abstraerlo a un gestor de errores.
            if(t.getClass() != this.getClase()){
                throw (new Exception("ERROR: el objeto pasado por parametro es del tipo incorrecto, el tipo de este DAO es: "+this.getClase().getName()));
            }
            String set="";
            String query;
            for (Field f : this.campos) {
                if(f.getName().equals(this.getClave()) || f.getName().equals("ultMod") || f.getName().equals("creado")) continue;
                set = set + f.getName() + "=:" + f.getName()+", ";
            }
            if(set.length()>2)
                set = set.substring(0,set.length()-2);
            query = "UPDATE " + this.getTabla() + " SET " + set + " WHERE "+this.getClave() + "=:"+this.getClave();
            
            if (logger.isDebugEnabled()) {
                logger.debug("ABMDAO.filtrar - SQL para Filtrar: " + query);
            }
            int filasAfectadas = con.createQuery(query).bind(t).executeUpdate().getResult();
            return filasAfectadas > 0;
        }

    /**
     * Elimina el objeto de tipo T asociado a id
     *
     * @param int id - id del elemento a eliminar.
     * @param boolean borrar - si borar es verdadero se realiza la baja de forma permanente, si es falso se puede recuperar modificando la bd.
     * @return boolean - verdadero si la baja fue exitosa, falso en caso contrario.
     */
    public boolean baja(String id, boolean borrar) throws Exception{
        String query;
        query = (borrar ? "DELETE FROM ": "UPDATE ") 
            + this.getTabla() 
            + (borrar ? " " : " SET estado=\"INACTIVO\" ")+"WHERE "+this.getClave() + "=:"+this.getClave()
            + (borrar ? "" : " AND estado=\"ACTIVO\""); 
        if (logger.isDebugEnabled()) {
            logger.debug("ABMDAO.filtrar - SQL para Filtrar: " + query);
        }
        int filasAfectadas = con.createQuery(query, true)
                                .addParameter(this.getClave(), id)
                                .executeUpdate()
                                .getResult(); 
        return filasAfectadas > 0; 
    }

    
    /**
     * Devulve todos los elementos de tipo T en la bd
     *
     * @return List<T> - lista de todos los elementos de tipo T
     */
    public List<T> listar() throws Exception{
        Class c = this.getClase();
        String query = "SELECT * FROM "+ this.getTabla() +" WHERE estado=\"ACTIVO\""; 
        
        if (logger.isDebugEnabled()) {
            logger.debug("ABMDAO.filtrar - SQL para Filtrar: " + query);
        }
        return con.createQuery(query).executeAndFetch(c);
    }
    
    
    /**
     * Devulve una lista de los elementos asociados a los ids, la lista tendra los objetos asociados que encuentre, un id puede no tener elemento asociado.
     *
     * @param String[] - arreglo de ids
     *
     * @return List<T> - lista de todos los elementos asociados a ids
     */
    public List<T> listar(String... ids) throws Exception{
            String aux="";
            for (String id : ids){
                aux += this.getTabla()+"."+this.getClave()+"="+id+" OR ";
            }
            aux = aux.length() > 2 ? aux.substring(0,aux.length()-4) : aux;

            String query = "SELECT DISTINCT * FROM "+ this.getTabla() + " WHERE "+aux +" AND estado=\"ACTIVO\"";
            
            if (logger.isDebugEnabled()) {
                logger.debug("ABMDAO.filtrar - SQL para Filtrar: " + query);
            }
            
            return con.createQuery(query).executeAndFetch(this.getClase());
        
    }

    /**
     * Devuelve el primer elemento asociado a dicho id
     *
     * @param String id - valor de la clave
     *
     * @return List<T> - lista de todos los elementos asociados a ids
     */
    public T filtrar(String id) throws Exception{
        String query = "SELECT * FROM "+ this.getTabla() + " WHERE "+ this.getClave()+" = :"+this.getClave();

        if (logger.isDebugEnabled()) {
            logger.debug("ABMDAO.filtrar - SQL para Filtrar: " + query);
        }
        return con.createQuery(query).addParameter(this.getClave(), id).executeAndFetchFirst(this.getClase());
    }

    /**
     * Devuelve todos los elementos que cumplan las condiciones para ciertos valores dados evaluados en los campos, las listas de los parametros deben tener el mismo tamaño
     *
     * @param List<String> campos - Lista de campos de la entidad a ser evaluados
     * @param List<String> condiciones - Lista de condiciones a ser aplicadas a los campos, ej: <=, >=, LIKE, etc
     * @param List<String> valores - Lista de valores con los que se evaluaran las condiciones a cada campo
     *
     * @return List<T> - lista de todos los elementos asociados a ids
     */
    public List<T> filtrar(List<String> campos, List<String> valores, List<Integer> condiciones) throws Exception {
            if(campos == null || valores == null || condiciones == null || condiciones.size() != campos.size() || campos.size() != valores.size()){
                throw(new Exception("Las listas deben tener el mismo tamaño"));
            }
            String queryAux = " ";
            int i = 0; 
            for (String campo : campos){
                if(!this.campos.stream().anyMatch(x -> x.getName().equals(campo))){
                    throw(new Exception("El campo \""+campo+"\" no existe"));
                }

                queryAux += campos.get(i);
                switch (condiciones.get(i)) {
                case 0:
                    queryAux += "=\"";
                    break;
                case 1:
                    queryAux +="<=\"";
                    break;
                case 2:
                    queryAux +="<\"";
                    break;
                case 3:
                    queryAux +=">=\"";
                    break;
                case 4:
                    queryAux +=">\"";
                    break;
                case 5:
                    queryAux +=" LIKE \"";
                    break;
                }
                queryAux +=valores.get(i) + "\" AND ";
                i++;
            }
            
            if(queryAux.length() > 1){queryAux = queryAux.substring(0, queryAux.length()-5);}
            String query = "SELECT * FROM "+ this.getTabla() + " WHERE";
            query+= queryAux;
            
            if (logger.isDebugEnabled()) {
                logger.debug("ABMDAO.filtrar - SQL para Filtrar: " + query);
            }

            return con.createQuery(query).executeAndFetch(this.getClase());
        
        }
}
