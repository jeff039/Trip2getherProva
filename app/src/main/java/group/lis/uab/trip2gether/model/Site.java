package group.lis.uab.trip2gether.model;

import com.parse.ParseFile;
import java.io.Serializable;

public class Site implements Serializable {

    private String objectId;

    private String nombre;

    private String descripcion;

    private String idViaje;

    private ParseFile imagen;

    private double duracion;

    private double precio;

    private double latitud;

    private double longitud;

    private double estrellas;

    public double getEstrellas() {return estrellas;}

    public void setEstrellas(double estrellas) {this.estrellas = estrellas;}

    public void setId(String id){ this.objectId = id; }

    public String getId(){ return this.objectId; }

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

    public void setDuracion(double duracion){
        this.duracion = duracion;
    }

    public double getDuracion(){
        return this.duracion;
    }

    public void setPrecio(double precio){
        this.precio = precio;
    }

    public double getPrecio(){
        return this.precio;
    }

    public void setLatitud(double latitud){
        this.latitud = latitud;
    }

    public double getLatitud(){
        return this.latitud;
    }

    public void setLongitud(double longitud){
        this.longitud = longitud;
    }

    public double getLongitud(){
        return this.longitud;
    }


    public Site(String nombre, String descripcion, ParseFile imagen, String idViaje, String objectId, double duracion, double precio, double latitud, double longitud) {
        this.setNombre(nombre);
        this.setDescripcion(descripcion);
        this.setIdViaje(idViaje);
        this.setImagen(imagen);
        this.setId(objectId);
        this.setDuracion(duracion);
        this.setPrecio(precio);
        this.setLatitud(latitud);
        this.setLongitud(longitud);
    }
}