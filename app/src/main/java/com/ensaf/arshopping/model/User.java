package com.ensaf.arshopping.model;

public class User {

    String email;
    String idUser;
    String name;


    public User(String email, String idUser, String name) {
        this.email = email;
        this.idUser = idUser;
        this.name = name;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
