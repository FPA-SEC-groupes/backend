package com.HelloWay.HelloWay.payload.response;

import com.HelloWay.HelloWay.entities.Product;
import com.HelloWay.HelloWay.entities.ProductStatus;

public class ProductQuantity {
    private Product product;
    private int oldQuantity;
    private int quantity;
    private ProductStatus status;

    public ProductQuantity(Product product, int oldQuantity, int quantity, ProductStatus status) {
        this.product = product;
        this.oldQuantity = oldQuantity;
        this.quantity = quantity;
        this.status = status;
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

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }
}
