package com.HelloWay.HelloWay.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.*;

@Entity
@DiscriminatorValue("promotion")
public class Promotion extends Event{
    @Column
    private  int percentage ;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="idProduct")
    private Product product;

    public Promotion(int percentage, Product product) {
        this.percentage = percentage;
        this.product = product;
    }

    public Promotion() {
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
