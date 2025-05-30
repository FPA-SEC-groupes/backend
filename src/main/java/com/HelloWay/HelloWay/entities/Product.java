package com.HelloWay.HelloWay.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@Table(	name = "products")
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idProduct")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduct;

    @NotBlank
    @Column(length = 50)
    private String productTitle;


    @Column(length = 20)
    private Float price;

    @Column(length = 100)
    private String description;

    private Boolean available = true ;

    @Column(name = "order_index")
    private int orderIndex;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<BasketProduct> basketProducts;

    @ToString.Exclude
    // @JsonIgnore
    @ManyToOne(optional = true)
    @JoinColumn(name = "id_categorie")
    private Categorie categorie;

    @ToString.Exclude
    @OneToMany(mappedBy="product", cascade = CascadeType.ALL)
    private List<Image> images;

    public Product(Long idProduct, String productTitle, Float price, String description, Boolean available, Categorie categorie) {
        this.idProduct = idProduct;
        this.productTitle = productTitle;
        this.price = price;
        this.description = description;
        this.available = available;
        this.categorie = categorie;
    }

    public Long getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(Long idProduct) {
        this.idProduct = idProduct;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        if (price != null) {
            BigDecimal bd = new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);
            this.price = bd.floatValue();
        } else {
            this.price = null;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public List<BasketProduct> getBasketProducts() {
        return basketProducts;
    }

    public void setBasketProducts(List<BasketProduct> basketProducts) {
        this.basketProducts = basketProducts;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @OneToMany(mappedBy="product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Promotion> promotions;

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    public void removeCategorie() {
        if (this.categorie != null) {
            this.categorie.getProducts().remove(this);
            this.categorie = null;
        }
    }
    
}
