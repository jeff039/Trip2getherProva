package group.lis.uab.trip2gether.model;


import com.parse.ParseFile;

import java.io.Serializable;

/**
 * Created by Mireia on 27/03/2015.
 */
public class Site implements Serializable {

    private String id;

    private String nombre;

    private String descripcion;

    private String idViaje;

    private ParseFile imagen;

    public void setId(String id){ this.id = id; }

    public String getId(){ return this.id; }

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

    public ParseFile getImagen() { return imagen; }

    public void setImagen(ParseFile imagen) { this.imagen = imagen; }

    public Site(String nombre, String descripcion, ParseFile imagen) {
        this.setNombre(nombre);
        this.setDescripcion(descripcion);
        this.setIdViaje(idViaje);
        this.setImagen(imagen);

    }

}
