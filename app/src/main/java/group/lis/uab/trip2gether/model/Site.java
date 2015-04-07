package group.lis.uab.trip2gether.model;


import java.io.Serializable;

/**
 * Created by Mireia on 27/03/2015.
 */
public class Site implements Serializable {

    private String nombre;

    private String descripcion;

    private String idViaje;

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

    public void setIdViaje(String idViaje){
        this.idViaje = idViaje;
    }

    public String getIdViaje(){
        return this.idViaje;
    }


    public Site(String nombre, String descripcion) {
        this.setNombre(nombre);
        this.setDescripcion(descripcion);
        this.setIdViaje(idViaje);

    }

}
