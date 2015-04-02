package group.lis.uab.trip2gether.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Jofré on 30/03/2015.
 */
public class User implements Serializable{

    private String mail;

    private String password;

    private String name;

    private String surname;

    private String country;

    private String city;

    private Date dateOfBirth;

    private String objectId;

    public User(String mail, String password, String name, String surname, String country,
                String city, Date dateOfBirth, String objectId) {
        this.setMail(mail);
        this.setPassword(password);
        this.setName(name);
        this.setSurname(surname);
        this.setCountry(country);
        this.setCity(city);
        this.setDateOfBirth(dateOfBirth);
        this.setObjectId(objectId);
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMail() {
        return this.mail;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return this.city;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectId() {
        return this.objectId;
    }


}
