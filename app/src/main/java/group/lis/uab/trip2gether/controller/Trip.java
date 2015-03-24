package group.lis.uab.trip2gether.controller;

public class Trip {

    private String nombre;

    private String país;

    private String ciudad;

    private String fechaInicio;

    private String fechaFinal;

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

    public void setFechaInicio(String fechaInicio){
        this.fechaInicio = fechaInicio;
    }

    public String getFechaInicio(){
        return  this.fechaInicio;
    }

    public void setFechaFinal(String fechaFinal){
        this.fechaFinal = fechaFinal;
    }

    public String getFechaFinal(){
        return  this.fechaFinal;
    }

    public Trip(String nombre, String pais, String ciudad, String fechaInicio, String fechaFinal) {
        this.setNombre(nombre);
        this.setPais(pais);
        this.setCiudad(ciudad);
        this.setFechaInicio(fechaInicio);
        this.setFechaFinal(fechaFinal);
    }

}
