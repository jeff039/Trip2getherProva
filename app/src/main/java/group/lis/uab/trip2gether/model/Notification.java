package group.lis.uab.trip2gether.model;

import java.io.Serializable;

/**
 * Created by Jofré on 26/04/2015.
 */
public class Notification implements Serializable {

    private String objectId;

    private Boolean estado;

    private String idEmisor;

    private String idReceptor;

    private String idTipo;

    private String tipo;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getIdEmisor() {
        return idEmisor;
    }

    public void setIdEmisor(String idEmisor) {
        this.idEmisor = idEmisor;
    }

    public String getIdReceptor() {
        return idReceptor;
    }

    public void setIdReceptor(String idReceptor) {
        this.idReceptor = idReceptor;
    }

    public String getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(String idTipo) {
        this.idTipo = idTipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Notification(String objectId, Boolean estado, String idEmisor, String idReceptor,
                        String idTipo, String tipo) {
        this.setObjectId(objectId);
        this.setEstado(estado);
        this.setIdEmisor(idEmisor);
        this.setIdReceptor(idReceptor);
        this.setIdTipo(idTipo);
        this.setTipo(tipo);
    }
}
