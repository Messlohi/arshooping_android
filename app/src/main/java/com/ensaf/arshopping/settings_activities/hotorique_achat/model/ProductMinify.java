package com.ensaf.arshopping.settings_activities.hotorique_achat.model;

public class ProductMinify {
    String catego;
    String id;
    String price;
    String quantite;
    String title;
    String urlImage;


    public ProductMinify(String catego, String id, String price, String quantite) {
        this.catego = catego;
        this.id = id;
        this.price = price;
        this.quantite = quantite;
    }

    public ProductMinify() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCatego() {
        return catego;
    }

    public void setCatego(String catego) {
        this.catego = catego;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantite() {
        return quantite;
    }

    public void setQuantite(String quantite) {
        this.quantite = quantite;
    }
}
