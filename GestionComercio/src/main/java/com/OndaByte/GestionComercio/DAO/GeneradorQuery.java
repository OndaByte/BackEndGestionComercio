package com.OndaByte.GestionComercio.DAO;

import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * GeneradorQuery generico SQL para entidades simples, soporta maximo una herencia de entidad, ej Usuario extend ObjetoBD (esto es old, hay que cambiarlo)
 * @param <T>
 */
public class GeneradorQuery <T> {

    // Campos es una lista de string de todos los campos de las clases definidas por nosotros (ondabyte) y que ademas esten en el paquete "modelo", esto se podria abstraer mas
    private List<Field> campos = new ArrayList<Field>();
    private static Logger logger = LogManager.getLogger(GeneradorQuery.class.getName());
    
    private String clave;

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    private final Class<T> clase;
    public GeneradorQuery(Class<T> clase){this.clase=clase;setCampos();}
    public Class<T> getClase(){return clase;}
    
    
    public String getTabla(){return this.getClase().getSimpleName();}

    // Reviso que sean clases definidas por OndaByte del paquete modelo.
    private void setCampos(){
        Class claseAux = this.getClase();
        while(claseAux.getName().contains("OndaByte") && claseAux.getName().contains("modelo")){
            for (Field f : claseAux.getDeclaredFields()){
                campos.add(f);
            }
            claseAux = claseAux.getSuperclass();
        }
    }

    /**
     * Da de alta un objeto de la clase T en la base de datos.
     *
     * @param T t - elemento a ser dado de alta.
     *
     * @return int el número de insersiones.
     */
    public String getQueryAlta(T nuevo)  throws Exception {
        // Tendria que abstraer la conversion de objeto a lista de clave valor
        String columnas = " (";
        String valores = " (";
        for (Field f : this.campos){
            if (f.getName().equals(this.getClave()) || f.getName().equals("creado") || f.getName().equals("ultMod") 
                || (this.getClase()
                    .getMethod("get"+f.getName().substring(0,1).toUpperCase()+f.getName().substring(1))
                    .invoke(nuevo)
                    == null)){ continue;}
            columnas = columnas + f.getName() + ",";
            valores = valores + ":" + f.getName() + ",";
        }
        valores = valores.substring(0,valores.length()-1) + ")";
        columnas = columnas.substring(0,columnas.length()-1)+ ")";
        String query;
        query = "INSERT INTO " + this.getTabla() + columnas + " VALUES" + valores;

        logger.debug("QueryAlta:\n" + query);
        return query;
    }

    /*

    public String getColumnasValores(List<String> col, List<List<String>> val) throws Exception{
        String columnas = " (";
        String valores = " (";

        if(col.size()!=val.getFirst().size() || col.size()!=val.getLast().size() || col.size()!=val.get(col.size()/2).size()){
            return null;
        }
        
        for(int i=0; col.size() > i; i++){
            columnas = columnas + col.get(i) + ",";
        }

        for(List<String> v : val){
            for(int i=0; col.size() > i; i++){
                valores = valores + ":" + v.get(i) + ",";
            }
        }
        
        valores = valores.substring(0,valores.length()-1) + ")";
        columnas = columnas.substring(0,columnas.length()-1)+ ")";
        logger.debug(columnas + " VALUES" + valores);
        return columnas + " VALUES" + valores;    
        }*/

    
    /**
     * Modifica la entidad en la base de datos con la misma clave que t,
     * se le asignan todos los atributos del objeto t a la entidad de la bd correspondiente
     *
     * @param T t - elemento a actualizar en bd.
     *
     * @return boolean - verdadero si la modificacion fue exitosa.
     */
    public String getQueryModificar() throws Exception{
        String set="";
        String query;
        for (Field f : this.campos) {
            if(f.getName().equals(this.getClave()) || f.getName().equals("ultMod") || f.getName().equals("creado")) continue;
            set = set + f.getName() + "=:" + f.getName()+", ";
        }
        if(set.length()>2)
            set = set.substring(0,set.length()-2);
        query = "UPDATE " + this.getTabla() + " SET " + set + " WHERE "+this.getClave() + "=:"+this.getClave();
            
        logger.debug("QueryModificar:\n" + query);

        return query;
    }

    /**
     * Elimina el objeto de tipo T asociado a id
     *
     * @param int id - id del elemento a eliminar.
     * @param boolean borrar - si borar es verdadero se realiza la baja de forma permanente, si es falso se puede recuperar modificando la bd.
     * @return boolean - verdadero si la baja fue exitosa, falso en caso contrario.
     */
    public String getQueryBaja(boolean borrar) throws Exception{
        String query;
        query = (borrar ? "DELETE FROM ": "UPDATE ") 
            + this.getTabla() 
            + (borrar ? " " : " SET estado=\"INACTIVO\" ")+"WHERE "+this.getClave() + "=:"+this.getClave()
            + (borrar ? "" : " AND estado=\"ACTIVO\""); 
        logger.debug("QueryFiltrar:\n" + query);
        return query;
    }

    
    /**
     * Devulve todos los elementos de tipo T en la bd
     *
     * @return List<T> - lista de todos los elementos de tipo T
     */
    public String getQueryListar() throws Exception{
        Class c = this.getClase();
        String query = "SELECT * FROM "+ this.getTabla() +" WHERE estado=\"ACTIVO\""; 
        
        logger.debug("QueryListar:\n" + query);
        return query;
    }
    
    /**
     * Devulve todos los elementos de tipo T en la bd
     *
     * @return List<T> - lista de todos los elementos de tipo T
     */
    public String getQueryListarOrder(String campo, String tipoOrden) throws Exception{
        Class c = this.getClase();
        String query = "SELECT * FROM "+ this.getTabla() +" WHERE estado=\"ACTIVO\" "
                        +" ORDER BY "+campo+ " " +tipoOrden+";"; 
        
        logger.debug("QueryListar:\n" + query);
        return query;
    }
    
 
    /**
     * Devulve una lista de los elementos asociados a los ids, la lista tendra los objetos asociados que encuentre, un id puede no tener elemento asociado.
     *
     * @param String[] - arreglo de ids
     *
     * @return List<T> - lista de todos los elementos asociados a ids
     */
    public String getQueryListar(String... ids) throws Exception{
        String aux="";
        for (String id : ids){
            aux += this.getTabla()+"."+this.getClave()+"="+id+" OR ";
        }
        aux = aux.length() > 2 ? aux.substring(0,aux.length()-4) : aux;

        String query = "SELECT DISTINCT * FROM "+ this.getTabla() + " WHERE "+aux +" AND estado=\"ACTIVO\"";
            
        logger.debug("QueryListar:\n" + query);
        return query;
        
    }

    /**
     * Devuelve el primer elemento asociado a dicho id
     *
     * @param String id - valor de la clave
     *
     * @return List<T> - lista de todos los elementos asociados a ids
     */
    public String getQueryFiltarId() throws Exception{
        String query = "SELECT * FROM "+ this.getTabla() + " WHERE "+ this.getClave()+" = :"+this.getClave();

        logger.debug("QueryFiltrarId:\n" + query);
        return query;
    }

    /**
     * Genera un string 'WHERE campos1 condicional1 valores1 conector1 campos2=valores2..'
     * las listas de los parametros deben tener el mismo tamaño
     *
     * @param List<String> campos - Lista de campos de la entidad a ser evaluados
     * @param List<String> condiciones - Lista de condiciones a ser aplicadas a los campos, ej: <=, >=, LIKE, etc. Mapeados a entero. Hay que moverlo a un config
     * @param List<String> valores - Lista de valores con los que se evaluaran las condiciones a cada campo
     *
     * @return List<T> - lista de todos los elementos asociados a ids
     */
    public String getWhere(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores) throws Exception {
        logger.debug("getWhere");
        if(campos == null || valores == null || condiciones == null || condiciones.size() != campos.size() || campos.size() != valores.size() || valores.size() != conectores.size()){
            throw(new Exception("Las listas deben tener el mismo tamaño"));
        }
        for (String campo : campos){

            if(!this.campos.stream().anyMatch(x -> x.getName().equals(campo))){
                throw(new Exception("El campo \""+campo+"\" no existe"));
            }
        }
        
        String queryAux = "";
        
        for (int i = 0; campos.size() > i; i++){
            
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
            if(conectores.get(i))
                queryAux +=valores.get(i) + "\" OR ";
            else
                queryAux +=valores.get(i) + "\" AND ";
           // i++;
        }
            
        if(queryAux.length() > 1 && !conectores.getLast()){
            queryAux = queryAux.substring(0, queryAux.length()-5);
        }else if(queryAux.length() > 1 && conectores.getLast()){
            queryAux = queryAux.substring(0, queryAux.length()-4);
        }
        String query = " WHERE ";
        query+= queryAux;
        logger.debug("getWhere:\n" + query);
        return query;
    }

    public String getQueryFiltrar(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores) throws Exception{
        return "SELECT * FROM "+this.getTabla() + this.getWhere(campos, valores, condiciones, conectores);
    }
    
    public String getCountQuery(List<String> campos, List<String> valores, List<Integer> condiciones, List<Boolean> conectores) throws Exception {
        String query = "SELECT COUNT(*) FROM "+ this.getTabla() + this.getWhere(campos, valores, condiciones, conectores); 
        logger.debug("QueryContar:\n" + query);
        return query;
    }
    
    public String getQueryFiltrarOrdenado(List<String> campos, List<String> valores, List<Integer> condiciones,  List<Boolean> conectores, String tipoOrden) throws Exception { 
        String query = "SELECT * FROM "+ this.getTabla() + this.getWhere(campos, valores, condiciones, conectores);
        
        query += " ORDER BY :orden " +tipoOrden+";"; 
        
        logger.debug("QueryFiltrarOrdenado:\n" + query);
        return query;
    }
    
    public String getQueryFiltrarOrdenadoYPaginado(List<String> campos, List<String> valores, List<Integer> condiciones,  List<Boolean> conectores, String tipoOrden) throws Exception { 
        String query = "SELECT * FROM "+ this.getTabla();
        String where = this.getWhere(campos, valores, condiciones, conectores);
        String orden = " ORDER BY :orden " + tipoOrden; 
        String page = " LIMIT :limit OFFSET :offset";
        query += where + orden + page + ";"; 
        logger.debug("QueryFiltrar:OrdenadoYPaginado\n" + query);
        return query;
    }
}
