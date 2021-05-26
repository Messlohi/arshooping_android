package com.ensaf.arshopping.settings_activities.hotorique_achat.model;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class OrderHistory {

    Long amount;
    String date;
    List<ProductMinify> prodcuts = new ArrayList<>();
    String key;
    Boolean deatilVisible = false;


    public OrderHistory() {
    }

    public OrderHistory(Long amount, String date, List<ProductMinify> prodcuts, String key) {
        this.amount = amount;
        this.date = date;
        this.prodcuts = prodcuts;
        this.key = key;
    }

    public Boolean getDeatilVisible() {
        return deatilVisible;
    }

    public void setDeatilVisible(Boolean deatilVisible) {
        this.deatilVisible = deatilVisible;
    }


    public void setProdcuts(List<ProductMinify> prodcuts) {
        this.prodcuts = prodcuts;
    }

    public List<ProductMinify> getProducts() {
        return prodcuts;
    }

    public void setProducts(List<ProductMinify> products) {
        this.prodcuts = products;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
