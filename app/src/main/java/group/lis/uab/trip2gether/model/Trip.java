package group.lis.uab.trip2gether.model;

import java.util.Date;

public class Trip {

    private String nombre;

    private String país;

    private String ciudad;

    private Date fechaInicio;

    private Date fechaFinal;

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

    public Trip(String nombre, String pais, String ciudad, Date fechaInicio, Date fechaFinal) {
        this.setNombre(nombre);
        this.setPais(pais);
        this.setCiudad(ciudad);
        this.setFechaInicio(fechaInicio);
        this.setFechaFinal(fechaFinal);
    }

}
