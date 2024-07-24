package com.HelloWay.HelloWay.entities;

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
@Table(name = "primary_material_histories")
@NoArgsConstructor
public class PrimaryMaterialHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private double stockQuantity;

    @NotNull
    private LocalDateTime date;

    @NotBlank
    private String status;

    @ManyToOne
    @JoinColumn(name="primary_material_id")
    private PrimaryMaterial primaryMaterial;

    public PrimaryMaterialHistory(Long id, String name, double stockQuantity, LocalDateTime date, String status, PrimaryMaterial primaryMaterial) {
        this.id = id;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.date = date;
        this.status = status;
        this.primaryMaterial = primaryMaterial;
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

    public double getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(double stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PrimaryMaterial getPrimaryMaterial() {
        return primaryMaterial;
    }

    public void setPrimaryMaterial(PrimaryMaterial primaryMaterial) {
        this.primaryMaterial = primaryMaterial;
    }
}
