package com.HelloWay.HelloWay.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@ToString
@Entity
@Table(	name = "primary_materials")
@NoArgsConstructor
public class PrimaryMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String unitOfMeasure;

    @NotNull
    private double stockQuantity;

    @NotNull
    private double price;

    @NotNull
    private LocalDateTime expirationDate;

    @NotNull
    private String supplier;

    @NotNull
    private String supplierNumber;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_space")
    private Space space;


    public PrimaryMaterial(Long id, String name,
                           String description,
                           String unitOfMeasure,
                           double stockQuantity,
                           double price,
                           LocalDateTime expirationDate,
                           String supplier,
                           String supplierNumber,
                           Space space) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unitOfMeasure = unitOfMeasure;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.expirationDate = expirationDate;
        this.supplier = supplier;
        this.supplierNumber = supplierNumber;
        this.space = space;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public double getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(double stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getSupplierNumber() {
        return supplierNumber;
    }

    public void setSupplierNumber(String supplierNumber) {
        this.supplierNumber = supplierNumber;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }
}
