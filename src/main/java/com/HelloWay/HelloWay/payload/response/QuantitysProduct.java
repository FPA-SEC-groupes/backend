package com.HelloWay.HelloWay.payload.response;

public class QuantitysProduct {
    private int oldQuantity;
    private int Quantity;

    public QuantitysProduct(int oldQuantity, int quantity) {
        this.oldQuantity = oldQuantity;
        Quantity = quantity;
    }

    public int getOldQuantity() {
        return oldQuantity;
    }

    public void setOldQuantity(int oldQuantity) {
        this.oldQuantity = oldQuantity;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }
}
