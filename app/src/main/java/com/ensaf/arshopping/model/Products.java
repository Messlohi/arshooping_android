package com.ensaf.arshopping.model;

import java.io.Serializable;

public class Products  {

    String id;
    String Quantite;
    String catego;
    String title;
    String price;
    String desc;
    String urlImage;
    String url3dShape;
    Boolean isAvailable =true;
    Boolean isSelected =false;

    public Products() {
    }


    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getProductId() {
        return id;
    }

    public void setProductId(String productId) {
        id = productId;
    }

    public String getQuantite() {
        return Quantite;
    }

    public void setQuantite(String quantite) {
        Quantite = quantite;
    }

    public String getCatego() {
        return catego;
    }

    public void setCatego(String catego) {
        this.catego = catego;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getUrl3dShape() {
        return url3dShape;
    }

    public void setUrl3dShape(String url3dShape) {
        this.url3dShape = url3dShape;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }
}
