package com.ensaf.arshopping.settings_activities.collections.model;

import java.util.List;

public class Collection {
    String key;
    String name;
    String date;
    long nbProduit;

    public Collection(String key, String name, String date, long nbProduitl) {
        this.key = key;
        this.name = name;
        this.date = date;
        this.nbProduit = nbProduit;
    }

    public Collection() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getNbProduit() {
        return nbProduit;
    }

    public void setNbProduit(long nbProduit) {
        this.nbProduit = nbProduit;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
