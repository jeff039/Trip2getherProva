package group.lis.uab.trip2gether.model;

import android.net.Uri;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

import group.lis.uab.trip2gether.Resources.ParseProxyObject;

public class Trip implements Serializable{

    private String id;

    private String nombre;

    private String país;

    private String ciudad;

    private Date fechaInicio;

    private Date fechaFinal;

    private ParseFile imagen;

/*
    private ParseProxyObject imagen;

    public void setImagen(ParseFile imagen){

        ParseObject object = new ParseObject("TestObject");
        object.put("imagenViaje", imagen);
        try {
            object.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.imagen = new ParseProxyObject(object);
    }

    public ParseFile getImagen(){
        return imagen.getParseFile("imagenViaje");
    }
    */

    public void setId(String id){ this.id = id; }

    public String getId(){ return this.id; }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public String getNombre(){
        return this.nombre;
    }

    public void setPais(String país){
        this.país = país;
    }

    public String getPais(){
        return  this.país;
    }

    public void setCiudad(String ciudad){
        this.ciudad = ciudad;
    }

    public String getCiudad(){
        return  this.ciudad;
    }

    public void setFechaInicio(Date fechaInicio){
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaInicio(){
        return  this.fechaInicio;
    }

    public void setFechaFinal(Date fechaFinal){
        this.fechaFinal = fechaFinal;
    }

    public Date getFechaFinal(){
        return  this.fechaFinal;
    }

    public ParseFile getImagen() { return imagen; }

    public void setImagen(ParseFile imagen) { this.imagen = imagen; }

    public Trip(String nombre, String pais, String ciudad, Date fechaInicio, Date fechaFinal, ParseFile imagen) {
        this.setNombre(nombre);
        this.setPais(pais);
        this.setCiudad(ciudad);
        this.setFechaInicio(fechaInicio);
        this.setFechaFinal(fechaFinal);
        this.setImagen(imagen);
    }
/*
    public Trip(String nombre, String pais, String ciudad, Date fechaInicio, Date fechaFinal, Uri imagen) {
        this.setNombre(nombre);
        this.setPais(pais);
        this.setCiudad(ciudad);
        this.setFechaInicio(fechaInicio);
        this.setFechaFinal(fechaFinal);
        this.setImagen(imagen);
    }
*/
}
