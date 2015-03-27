package group.lis.uab.trip2gether.model;


/**
 * Created by Mireia on 27/03/2015.
 */
public class Site {

    private String nombre;

    private String descripcion;


    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public String getNombre(){
        return this.nombre;
    }

    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }

    public String getDescripcion(){
        return this.descripcion;
    }


    public Site(String nombre, String descripcion) {
        this.setNombre(nombre);
        this.setDescripcion(descripcion);

    }

}
