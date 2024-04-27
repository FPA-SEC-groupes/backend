package com.HelloWay.HelloWay.payload.response;

import com.HelloWay.HelloWay.entities.Product;


public class ProductQuantity {
    Product product;
    int oldQuantity;
    int quantity;

    public ProductQuantity(Product product, int oldQuantity, int quantity) {
        this.product = product;
        this.oldQuantity = oldQuantity;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getOldQuantity() {
        return oldQuantity;
    }

    public void setOldQuantity(int oldQuantity) {
        this.oldQuantity = oldQuantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
