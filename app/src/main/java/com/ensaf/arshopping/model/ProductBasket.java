package com.ensaf.arshopping.model;

public class ProductBasket {

    String productId;
    String quantite;
    String catego ;

    public ProductBasket() {
    }

    public ProductBasket(String productId, String quantite, String catego) {
        this.productId = productId;
        this.quantite = quantite;
        this.catego = catego;
    }

    public String getCatego() {
        return catego;
    }

    public void setCatego(String catego) {
        this.catego = catego;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getQuantite() {
        return quantite;
    }

    public void setQuantite(String quantite) {
        quantite = quantite;
    }
}
