package com.ensaf.arshopping.model;

public class ProductCategory {

    Integer productId;
    String categoryName;

    public ProductCategory(Integer productId, String categoryName) {
        this.productId = productId;
        this.categoryName = categoryName;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}